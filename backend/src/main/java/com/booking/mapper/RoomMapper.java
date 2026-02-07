package com.booking.mapper;

import com.booking.dto.room.OwnerDto;
import com.booking.dto.room.RoomDto;
import com.booking.entity.RoomEntity;

import java.math.BigDecimal;

public final class RoomMapper {

    public static RoomDto convertToDto(RoomEntity entity) {
        return new RoomDto(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCity(),
                BigDecimal.valueOf(entity.getPricePerNight()),
                entity.getMaxGuests(),
                entity.getImageUrl(),
                new OwnerDto(
                        entity.getOwner().getId(),
                        entity.getOwner().getName(),
                        entity.getOwner().getEmail()
                )
        );
    }
}
