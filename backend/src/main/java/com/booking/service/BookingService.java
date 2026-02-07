package com.booking.service;

import com.booking.dto.booking.BookingDto;
import com.booking.dto.booking.CreateBookingRequest;
import com.booking.dto.room.RoomDto;
import com.booking.entity.BookingEntity;
import com.booking.entity.BookingStatus;
import com.booking.entity.RoomEntity;
import com.booking.entity.UserEntity;
import com.booking.mapper.BookingMapper;
import com.booking.mapper.RoomMapper;
import com.booking.repository.BookingRepository;
import com.booking.repository.RoomRepository;
import com.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {

    BookingRepository bookingRepository;
    RoomRepository roomRepository;
    UserRepository userRepository;

    @Transactional
    public BookingDto createBooking(CreateBookingRequest dto) {

        RoomEntity room = roomRepository.findById(dto.roomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (dto.checkIn().isAfter(dto.checkOut())
                || dto.checkIn().isBefore(LocalDate.now())) {
            throw new RuntimeException("Invalid booking dates");
        }

        UserEntity user = userRepository
                .findByEmail(SecurityContextHolder.getContext()
                        .getAuthentication().getName())
                .orElseThrow();

        BookingEntity booking = new BookingEntity();
        booking.setRoomEntity(room);
        booking.setUserEntity(user);
        booking.setCheckIn(dto.checkIn());
        booking.setCheckOut(dto.checkOut());
        booking.setStatus(BookingStatus.PENDING);

        long days = ChronoUnit.DAYS.between(dto.checkIn(), dto.checkOut());
        booking.setTotalPrice(days * room.getPricePerNight());

        BookingEntity entity = bookingRepository.save(booking);
        return BookingMapper.convertToDto(entity);
    }

    public List<BookingDto> getUserBookings() {
        UserEntity user = userRepository
                .findByEmail(SecurityContextHolder.getContext()
                        .getAuthentication().getName())
                .orElseThrow();
        List<BookingEntity> resultEntities = bookingRepository.findByUserEntity(user);
        return resultEntities.stream().map(BookingMapper::convertToDto).toList();
    }

    public List<RoomDto> findAvailableRoomsByDateAndCity(String city,
                                                         LocalDate dateFrom,
                                                         LocalDate dateTo
    ) {
        List<RoomEntity> roomEntities = roomRepository.findAvailableRooms(city, dateTo, dateFrom);
        return roomEntities.stream().map(RoomMapper::convertToDto).toList();
    }

    @Transactional
    public boolean changeBookingStatus(Long id, BookingStatus status) {
        try {
            Optional<BookingEntity> entity = bookingRepository.findById(id);
            if(entity.isPresent()) {
                entity.get().setStatus(status);
                bookingRepository.save(entity.get());
                return true;
            }
            log.info("Booking {} not found in database.", id);
            return false;
        } catch (Exception ex) {
            log.error("Status {} cannot be accepted during to error: ", status, ex);
            return false;
        }
    }
}
