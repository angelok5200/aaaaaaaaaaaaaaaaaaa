
package com.booking.repository;

import com.booking.entity.BookingEntity;
import com.booking.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByUserEntity(UserEntity userEntity);
}
