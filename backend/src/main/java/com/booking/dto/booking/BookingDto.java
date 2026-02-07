package com.booking.dto.booking;

import com.booking.entity.BookingStatus;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BookingDto(
        Long id,
        Long roomId,
        String roomTitle,
        LocalDate checkIn,
        LocalDate checkOut,
        Double totalPrice,
        BookingStatus status
) {
}
