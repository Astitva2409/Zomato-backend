package com.astitva.zomatoBackend.ZomatoApp.service.order.Impl;

import com.astitva.zomatoBackend.ZomatoApp.dto.OrderResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.OrderItemResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.CreateOrderRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.RestaurantBasicDTO;
import com.astitva.zomatoBackend.ZomatoApp.dto.PriceBreakdownResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.Order;
import com.astitva.zomatoBackend.ZomatoApp.entities.OrderItem;
import com.astitva.zomatoBackend.ZomatoApp.entities.MenuItem;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.Restaurant;
import com.astitva.zomatoBackend.ZomatoApp.entities.Address;
import com.astitva.zomatoBackend.ZomatoApp.entities.Payment;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.OrderStatus;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.PaymentMethod;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.PaymentStatus;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.RestaurantStatus;
import com.astitva.zomatoBackend.ZomatoApp.exception.ResourceNotFoundException;
import com.astitva.zomatoBackend.ZomatoApp.exception.UnauthorizedException;
import com.astitva.zomatoBackend.ZomatoApp.exception.RuntimeConflictException;
import com.astitva.zomatoBackend.ZomatoApp.repository.OrderRepository;
import com.astitva.zomatoBackend.ZomatoApp.repository.OrderItemRepository;
import com.astitva.zomatoBackend.ZomatoApp.repository.MenuItemRepository;
import com.astitva.zomatoBackend.ZomatoApp.repository.UserRepository;
import com.astitva.zomatoBackend.ZomatoApp.repository.AddressRepository;
import com.astitva.zomatoBackend.ZomatoApp.repository.PaymentRepository;
import com.astitva.zomatoBackend.ZomatoApp.service.order.OrderService;
import com.astitva.zomatoBackend.ZomatoApp.service.restaurant.RestaurantService;
import com.astitva.zomatoBackend.ZomatoApp.strategies.ChargeResult;
import com.astitva.zomatoBackend.ZomatoApp.strategies.OrderPriceContext;
import com.astitva.zomatoBackend.ZomatoApp.strategies.PriceBreakdown;
import com.astitva.zomatoBackend.ZomatoApp.strategies.PricingEngine;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final RestaurantService restaurantService;
    private final PricingEngine pricingEngine;
    private final ModelMapper modelMapper;

    // Fields that should NOT be updated
    private static final Set<String> PROTECTED_FIELDS = Set.of(
            "id", "createdAt", "updatedAt", "user", "restaurant", "orderItems", "totalAmount"
    );

    /**
     * Helper: Convert Order to OrderResponse
     */
    private OrderResponse convertToResponse(Order order) {
        // Use ModelMapper for basic mapping
        OrderResponse response = modelMapper.map(order, OrderResponse.class);

        // Manually map restaurant to lightweight DTO
        if (order.getRestaurant() != null) {
            RestaurantBasicDTO restaurantDTO = modelMapper.map(order.getRestaurant(), RestaurantBasicDTO.class);
            response.setRestaurant(restaurantDTO);
            response.setRestaurantId(order.getRestaurant().getId());
        }

        // Convert order items
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            List<OrderItemResponse> items = order.getOrderItems().stream()
                    .map(this::convertOrderItemToResponse)
                    .toList();
            response.setItems(items);
        }

        // Build price breakdown
        response.setPriceBreakdown(buildPriceBreakdownResponse(order));

        return response;
    }

    /**
     * Helper: Convert OrderItem to OrderItemResponse
     */
    private OrderItemResponse convertOrderItemToResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setOrderId(item.getOrder().getId());
        response.setMenuItemId(item.getMenuItem().getId());
        response.setMenuItemName(item.getMenuItem().getName());
        response.setMenuItemDescription(item.getMenuItem().getDescription());
        response.setMenuItemCategory(item.getMenuItem().getCategory());
        response.setQuantity(item.getQuantity());
        response.setPriceAtPurchase(item.getPriceAtPurchase());
        response.setItemTotal(item.getPriceAtPurchase() * item.getQuantity());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }

    /**
     * Helper: Build price breakdown response from stored charges
     */
    private PriceBreakdownResponse buildPriceBreakdownResponse(Order order) {
        // Calculate cart value from order items
        double cartValue = order.getOrderItems().stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();

        double finalPrice = order.getTotalAmount();

        // Retrieve actual charges from stored JSON
        List<PriceBreakdownResponse.ChargeBreakdown> charges;
        if (order.getChargesBreakdown() != null && !order.getChargesBreakdown().isEmpty()) {
            charges = convertJsonToCharges(order.getChargesBreakdown());
        } else {
            // Fallback if charges not stored (for existing orders)
            double chargesTotal = finalPrice - cartValue;
            charges = List.of(
                    new PriceBreakdownResponse.ChargeBreakdown("Total Charges", chargesTotal)
            );
        }

        return PriceBreakdownResponse.builder()
                .cartValue(cartValue)
                .charges(charges)
                .finalPrice(finalPrice)
                .build();
    }

    /**
     * Helper: Calculate if peak hours
     */
    private boolean isPeakHours(LocalDateTime orderTime) {
        int hour = orderTime.getHour();
        return (hour >= 12 && hour < 14) || (hour >= 19 && hour < 21);
    }

    /**
     * Helper: Safely update entity fields using reflection
     */
    private void updateEntityFields(Order order, Map<String, Object> updates) {
        updates.forEach((fieldName, value) -> {
            // Prevent updating protected fields
            if (PROTECTED_FIELDS.contains(fieldName)) {
                return;  // Skip this field
            }

            Field field = ReflectionUtils.findField(Order.class, fieldName);
            if (field != null) {
                field.setAccessible(true);
                try {
                    ReflectionUtils.setField(field, order, value);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid value for field: " + fieldName);
                }
            }
        });
    }

    /**
     * Helper: Format delivery address from Address entity
     */
    private String formatDeliveryAddress(Address address) {
        return String.format("%s, %s, %s %s, %s",
                address.getLine1() != null ? address.getLine1() : "N/A",
                address.getLine2() != null ? address.getLine2() : "N/A",
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry()
        );
    }

    /**
     * Helper: Convert ChargeResult list to JSON string
     * Uses com.fasterxml.jackson for JSON serialization
     */
    private String convertChargesToJson(List<ChargeResult> charges) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(charges);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return "[]";  // Return empty array if conversion fails
        }
    }

    /**
     * Helper: Convert JSON string back to ChargeBreakdown list
     */
    private List<PriceBreakdownResponse.ChargeBreakdown> convertJsonToCharges(String chargesJson) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return objectMapper.readValue(
                    chargesJson,
                    objectMapper.getTypeFactory().constructCollectionType(
                            List.class,
                            PriceBreakdownResponse.ChargeBreakdown.class
                    )
            );
        } catch (Exception e) {
            return List.of();  // Return empty list if conversion fails
        }
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Step 1: Fetch entities
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Restaurant restaurant = restaurantService.getRestaurantOrThrow(request.getRestaurantId());

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        // Check if restaurant is open
        if (!restaurant.getRestaurantStatus().equals(RestaurantStatus.OPEN)) {
            throw new RuntimeConflictException("Sorry this Restaurant is currently not serviceable");
        }

        // Step 2: Validate address belongs to user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Address does not belong to this user");
        }

        // Step 3: Fetch menu items and validate
        List<MenuItem> menuItems = request.getItems().stream()
                .map(itemRequest -> {
                    MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found " + itemRequest.getMenuItemId()));

                    // Validate menu item belongs to restaurant
                    if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                        throw new RuntimeConflictException("Menu item does not belong to this restaurant");
                    }

                    // Validate menu item is available
                    if (!menuItem.isAvailable()) {
                        throw new RuntimeConflictException("Menu item is not available: " + menuItem.getName());
                    }

                    return menuItem;
                })
                .toList();

        // Step 4: Calculate cart value
        double cartValue = 0.0;
        for (int i = 0; i < request.getItems().size(); i++) {
            cartValue += menuItems.get(i).getPrice() * request.getItems().get(i).getQuantity();
        }

        // Step 5: Build pricing context and calculate charges
        OrderPriceContext context = OrderPriceContext.builder()
                .userId(user.getId())
                .restaurantId(restaurant.getId())
                .cartValue(cartValue)
                .orderTime(LocalDateTime.now())
                .totalItems(request.getItems().size())
                .isPeakHours(isPeakHours(LocalDateTime.now()))
                .deliveryCity(address.getCity())
                .build();

        PriceBreakdown priceBreakdown = pricingEngine.calculateDetailedPrice(context);

        // Step 6: Create Order
        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setTotalAmount(priceBreakdown.getFinalPrice());
        order.setPaid(false);
        order.setDeliveryAddress(formatDeliveryAddress(address));

        String chargesJson = convertChargesToJson(priceBreakdown.getCharges());
        order.setChargesBreakdown(chargesJson);

        Order savedOrder = orderRepository.save(order);

        // Step 7: Create OrderItems
        List<OrderItem> orderItems = new java.util.ArrayList<>();
        for (int i = 0; i < request.getItems().size(); i++) {
            MenuItem menuItem = menuItems.get(i);
            Integer quantity = request.getItems().get(i).getQuantity();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtPurchase(menuItem.getPrice());

            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);
        savedOrder = orderRepository.save(savedOrder);

        // Step 8: Create Payment (PENDING status - as per Option A)
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(priceBreakdown.getFinalPrice());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setTransactionId(null);
        paymentRepository.save(payment);

        return convertToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        return null;
    }

    @Override
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return List.of();
    }

    @Override
    public List<OrderResponse> getOrdersByRestaurant(Long restaurantId) {
        return List.of();
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        return null;
    }

    @Override
    public OrderResponse updateOrder(Long orderId, Map<String, Object> updates) {
        return null;
    }

    @Override
    public void cancelOrder(Long orderId) {

    }
}