package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@EnableScheduling
@AllArgsConstructor
public class BackUpService {

    private final UserRepository userRepository;
    private final EmailService emailService;

//     @Scheduled(fixedRate = 30000)
    @Scheduled(cron = "0 0 9 * * MON")
    public void executePythonScript() throws IOException {
        String argument = userRepository.findAll().toString();
        ProcessBuilder processBuilder = new ProcessBuilder("python3", "/home/robert/IdeaProjects/Backend_staffoji_game/src/main/java/com/example/backend_staffoji_game/python_script/python.py", argument);
        Process process = processBuilder.start();
        readStandardOutput(process);
        readErrorOutput(process);

        boolean isEmailSend = emailService.sendFail();
        if (!isEmailSend) {
            System.out.println("email is not send");
        }
    }

    private void readErrorOutput(Process process) throws IOException {
        String line;
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        System.out.println("Standard error:\n");
        while ((line = stdError.readLine()) != null) {
            System.out.println(line);
        }
    }

    private void readStandardOutput(Process process) throws IOException {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        System.out.println("Standard output:\n");
        while ((line = stdInput.readLine()) != null) {
            System.out.println(line);
        }
    }
}