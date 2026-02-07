
package com.booking.controller;

import com.booking.dto.booking.BookingDto;
import com.booking.dto.booking.CreateBookingRequest;
import com.booking.entity.BookingEntity;
import com.booking.entity.BookingStatus;
import com.booking.service.BookingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {

    BookingService service;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody CreateBookingRequest request) {
        BookingDto result = service.createBooking(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/myBookings")
    public List<BookingDto> getUserBookings() {
        return service.getUserBookings();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/confirm")
    public ResponseEntity<String> confirmBooking(@PathVariable Long id) {
        boolean result = service.changeBookingStatus(id, BookingStatus.CONFIRMED);
        if(result) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.status(500).build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectBooking(@PathVariable Long id) {
        boolean result = service.changeBookingStatus(id, BookingStatus.REJECTED);
        if(result) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.status(500).build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        boolean result = service.changeBookingStatus(id, BookingStatus.CANCELLED);
        if(result) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.status(500).build();
    }
}
