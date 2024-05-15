package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.dto.UserScoreDTO;
import com.example.backend_staffoji_game.exception.UserDoesNotExistsException;
import com.example.backend_staffoji_game.model.UserScore;
import com.example.backend_staffoji_game.repository.UserScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserScoreService implements Level {

    @Autowired
    private final UserScoreRepository userScoreRepository;


    public UserScoreService(UserScoreRepository userScoreRepository) {
        this.userScoreRepository = userScoreRepository;
    }

    public UserScoreDTO updateUserScore(String userName, int level, int score) {
        UserScore userScore = findUserScore(userName);
        updateScore(level, score, userScore);
        userScoreRepository.save(userScore);
        return userScoreToUserScoreDTO(userScore);
    }

    private UserScore findUserScore(String userName) {
        UserScore userScore = userScoreRepository.findByUserNameIs(userName)
                .orElseThrow(() -> new UserDoesNotExistsException("No UserScore found with id: " + userName));
        return userScore;
    }

    private static void updateScore(int level, int score, UserScore userScore) {
        int updateScore;
        switch (level) {
            case LEVEL_ONE:
                updateScore = userScore.getLevel_one() + score;
                userScore.setLevel_one(updateScore);
                break;
            case LEVEL_TWO:
                updateScore = userScore.getLevel_two() + score;
                userScore.setLevel_two(updateScore);
                break;
            case LEVEL_THREE:
                updateScore = userScore.getLevel_three() + score;
                userScore.setLevel_three(updateScore);
                break;
            default:
                throw new IllegalArgumentException("Invalid level: " + level);
        }
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
