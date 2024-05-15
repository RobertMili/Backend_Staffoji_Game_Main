package com.example.backend_staffoji_game.repository;

import com.example.backend_staffoji_game.model.UserScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, Long>{


      Optional<UserScore> findByUserNameIs(String username);
}
