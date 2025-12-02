package com.astitva.zomatoBackend.ZomatoApp.repository;

import com.astitva.zomatoBackend.ZomatoApp.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
