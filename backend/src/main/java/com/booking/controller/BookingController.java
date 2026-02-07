
package com.booking.controller;

import com.booking.entity.Booking;
import com.booking.entity.UserEntity;
import com.booking.service.BookingService;
import com.booking.repository.UserRepository;
import com.booking.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @PostMapping
    public ResponseEntity<?> createBooking() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public List<Map<String, Object>> getMyBookings(Authentication auth) {
        return null;
    }

    @GetMapping("/managed")
    public List<Map<String, Object>> getManagedBookings(Authentication auth) {
        return null;
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Long id, Authentication auth) {
        return null;
    }
}
