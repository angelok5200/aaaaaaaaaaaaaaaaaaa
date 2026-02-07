
package com.booking.controller;

import com.booking.dto.room.RoomDto;
import com.booking.service.BookingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {

    BookingService bookingService;

    @GetMapping
    public List<RoomDto> findByDateAndCity(
            @RequestParam String city,
            @RequestParam LocalDate dateFrom,
            @RequestParam LocalDate dateTo
    ) {
        return bookingService.findAvailableRoomsByDateAndCity(city, dateFrom, dateTo);
    }
}
