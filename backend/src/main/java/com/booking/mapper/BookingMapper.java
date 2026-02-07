package com.booking.mapper;

import com.booking.dto.booking.BookingDto;
import com.booking.entity.BookingEntity;

public final class BookingMapper {

    public static BookingDto convertToDto(BookingEntity entity) {
        return BookingDto.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .checkIn(entity.getCheckIn())
                .checkOut(entity.getCheckOut())
                .totalPrice(entity.getTotalPrice())
                .roomTitle(entity.getRoomEntity().getTitle())
                .roomId(entity.getRoomEntity().getId())
                .build();
    }
}
