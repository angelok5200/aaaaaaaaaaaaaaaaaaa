package com.booking.dto.booking;

import java.time.LocalDate;

public record CreateBookingRequest(
        Long roomId,
        LocalDate checkIn,
        LocalDate checkOut
) {
}
