package com.booking.dto.room;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RoomDto (
        Long id,
        String title,
        String description,
        String city,
        BigDecimal pricePerNight,
        Integer maxGuests,
        String imageUrl,
        OwnerDto owner
) {
}
