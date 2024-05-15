package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.dto.UserScoreDTO;
import com.example.backend_staffoji_game.dto.UserTotalScoreDTO;
import com.example.backend_staffoji_game.exception.UserDoesNotExistsException;
import com.example.backend_staffoji_game.model.UserScore;
import com.example.backend_staffoji_game.repository.UserScoreRepository;
import com.example.backend_staffoji_game.service.Level.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserScoreService {

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
            case Level.LEVEL_ONE:
                updateScore = userScore.getLevel_one() + score;
                userScore.setLevel_one(updateScore);
                break;
            case Level.LEVEL_TWO:
                updateScore = userScore.getLevel_two() + score;
                userScore.setLevel_two(updateScore);
                break;
            case Level.LEVEL_THREE:
                updateScore = userScore.getLevel_three() + score;
                userScore.setLevel_three(updateScore);
                break;
            default:
                throw new UserDoesNotExistsException("Invalid level: " + level);
        }
    }

    private UserScoreDTO userScoreToUserScoreDTO(UserScore userScore) {
        UserScoreDTO userScoreDTO = new UserScoreDTO();
        userScoreDTO.setUserId(userScore.getId());
        userScoreDTO.setUserName(userScore.getUserName());
        userScoreDTO.setLevel_one(userScore.getLevel_one());
        userScoreDTO.setLevel_two(userScore.getLevel_two());
        userScoreDTO.setLevel_three(userScore.getLevel_three());
        return userScoreDTO;
    }

    public UserScoreDTO deletingUserScore(String userName) {
        UserScore userScore = findUserScore(userName);
        setAllLevelsNull(userScore);
        userScoreRepository.save(userScore);
        return userScoreToUserScoreDTO(userScore);
    }

    private static void setAllLevelsNull(UserScore userScore) {
        userScore.setLevel_one(0);
        userScore.setLevel_two(0);
        userScore.setLevel_three(0);
    }

    public List<UserTotalScoreDTO> getLeaderboard() {
       var totalScoreCompare =  userScoreRepository.findAll().stream()
                .map(userScore -> {
                    UserTotalScoreDTO userTotalScore = new UserTotalScoreDTO();
                    userTotalScore.setUserName(userScore.getUserName());
                    int totalScore = userScore.getLevel_one() + userScore.getLevel_two() + userScore.getLevel_three();
                    userTotalScore.setTotalScore(totalScore);
                    return userTotalScore;
                })
                .sorted(Comparator.comparingInt(UserTotalScoreDTO::getTotalScore).reversed())
                .collect(Collectors.toList());
       return totalScoreCompare;
    }
}
