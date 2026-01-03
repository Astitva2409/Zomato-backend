package com.astitva.zomatoBackend.ZomatoApp.entities;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.RestaurantStatus;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.RestaurantType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 150)
    private String address;

    @Column(nullable = false)
    private Double rating = 0.0;  // Default rating

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestaurantStatus restaurantStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestaurantType type;

    // One restaurant has many menu items
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> menuItems;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private User owner;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
