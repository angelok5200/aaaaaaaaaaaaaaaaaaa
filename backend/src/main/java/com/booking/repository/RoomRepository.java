
package com.booking.repository;

import com.booking.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    @Query("""
        SELECT r
        FROM RoomEntity r
        WHERE r.city = :city
          AND NOT EXISTS (
              SELECT 1
              FROM BookingEntity b
              WHERE b.roomEntity = r
                AND b.status = com.booking.entity.BookingStatus.CONFIRMED
                AND b.checkIn < :checkOut
                AND b.checkOut > :checkIn
          )
        """)
    List<RoomEntity> findAvailableRooms(
            @Param("city") String city,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}
