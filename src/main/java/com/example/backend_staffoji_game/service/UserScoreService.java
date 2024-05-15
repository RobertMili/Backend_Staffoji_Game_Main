package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.dto.UserScoreDTO;
import com.example.backend_staffoji_game.model.UserScore;
import com.example.backend_staffoji_game.repository.UserScoreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserScoreService {

    @Autowired
    private final UserScoreRepository userScoreRepository;


    public UserScoreService(UserScoreRepository userScoreRepository) {
        this.userScoreRepository = userScoreRepository;
    }

    public UserScoreDTO updateUserScore(String userName, int level, int score) {
        UserScore userScore = userScoreRepository.findByUserNameIs(userName)
                .orElseThrow(() -> new EntityNotFoundException("No UserScore found with id: " + userName));
        if (level == 1){
            var updateScore = userScore.getLevel_one() + score;
            userScore.setLevel_one(updateScore);
        } else if (level == 2){
            var updateScore = userScore.getLevel_two() + score;
            userScore.setLevel_two(updateScore);
        } else if (level == 3){
            var updateScore = userScore.getLevel_three() + score;
            userScore.setLevel_three(updateScore);
        }
        userScoreRepository.save(userScore);
        return userScoreToUserScoreDTO(userScore);
    }

    private UserScoreDTO userScoreToUserScoreDTO(UserScore userScore) {
        UserScoreDTO userScoreDTO = new UserScoreDTO();
        userScoreDTO.setUserId(userScoreDTO.getUserId());
        userScoreDTO.setLevel_one(userScore.getLevel_one());
        userScoreDTO.setLevel_two(userScore.getLevel_two());
        userScoreDTO.setLevel_three(userScore.getLevel_three());
        return userScoreDTO;
    }
}
