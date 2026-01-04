package com.astitva.zomatoBackend.ZomatoApp.repository;

import com.astitva.zomatoBackend.ZomatoApp.entities.MenuItem;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.MenuCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    // Find menu items by restaurant ID with pagination
    Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable);

    // Find all menu items by restaurant ID (non-paginated)
    List<MenuItem> findByRestaurantId(Long restaurantId);

    // Find menu items by restaurant ID and category
    /**
     * JPQL to find menu items by restaurant ID and category (Case Insensitive)
     * Note: We cast the Enum to String for comparison
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId " +
            "AND UPPER(CAST(m.category AS string)) = UPPER(:category)")
    List<MenuItem> findByRestaurantIdAndCategoryIgnoreCase(
            @Param("restaurantId") Long restaurantId,
            @Param("category") String category
    );

    // Find available menu items for a restaurant
    List<MenuItem> findByRestaurantIdAndAvailableTrue(Long restaurantId);

    List<MenuItem> findByNameIgnoreCase(String name);
}
