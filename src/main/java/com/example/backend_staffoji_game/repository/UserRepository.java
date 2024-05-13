package com.example.backend_staffoji_game.repository;

import com.example.backend_staffoji_game.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u.email FROM User u")
    List<String> findAllEmails();

    @Query("SELECT u.email FROM User u WHERE u.isPremium = true")
    List<String> findEmailsByPremiumUsers();

    @Query("SELECT u.email FROM User u WHERE u.isPremium = false")
    List<String> findEmailsByNotPremiumUsers();

}
