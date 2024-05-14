package com.example.backend_staffoji_game.repository;

import com.example.backend_staffoji_game.model.Notification;
import com.example.backend_staffoji_game.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{

    Optional<Notification> findByTitle(String notificationTitle);

//    @Query(value = "SELECT * FROM Notification WHERE date between :dayStart and :dayEnd", nativeQuery = true)
//    List<Notification> findNotificationByDate(@Param("dayStart")LocalDateTime dayStart, @Param("dayEnd")LocalDateTime dayEnd);
}
