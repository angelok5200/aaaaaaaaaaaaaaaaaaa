
package com.booking.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "rooms")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String title;

    @Column
    String description;

    @Column
    String city;

    @Column
    Double pricePerNight;

    @Column
    Integer maxGuests;

    @Column
    String imageUrl;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    UserEntity owner;

}
