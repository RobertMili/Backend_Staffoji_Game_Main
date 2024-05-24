package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.controller.NotificationController;
import com.example.backend_staffoji_game.dto.EmailNotificationSendNowDto;
import com.example.backend_staffoji_game.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private UserRepository userRepository;

    Logger logger = Logger.getLogger(NotificationController.class.getName());


    public EmailService(JavaMailSender javaMailSender, UserRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
    }

    public boolean sendNotification(EmailNotificationSendNowDto emailNotificationSendNowDto) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom("robertmilicevic3869@gmail.com");
            mail.setText(emailNotificationSendNowDto.getMessage());
            mail.setSubject(emailNotificationSendNowDto.getTitle());
            setEmailRecipients(emailNotificationSendNowDto, mail);

            javaMailSender.send(mail);
            logger.info("Email sent successfully, to: {}" + emailNotificationSendNowDto.toString());
            return true;
        } catch (MailException e) {
            logger.info("An error occurred while sending the email: " + e.getMessage());
            return false;
        }
    }

    public boolean sendFail() {
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);

            helper.setFrom("robertmilicevic3869@gmail.com");
            helper.setTo("robertmilicevic3869@gmail.com");
            helper.setText("Backup file is created successfully");
            helper.setSubject("Backup file is created successfully");

            FileSystemResource file = new FileSystemResource(new File("/home/robert/IdeaProjects/Backend_staffoji_game/src/main/java/com/example/backend_staffoji_game/python_script/Backup/backup_file.xlsx"));
            helper.addAttachment("backup_file.xlsx", file); // Corrected file name

            javaMailSender.send(mail);
            logger.info("Email sent successfully");
            return true;
        } catch (Exception e) {
            logger.info("An error occurred while sending the email: " + e.getMessage());
            return false;
        }
    }

    private void setEmailRecipients(EmailNotificationSendNowDto emailNotificationSendNowDto, SimpleMailMessage mail) {
        if ("all".equals(emailNotificationSendNowDto.getNotificationTarget())) {
            List<String> allEmailAddresses = getAllEmailAddresses();
            String[] emailArray = new String[allEmailAddresses.size()];
            allEmailAddresses.toArray(emailArray);
            mail.setTo(emailArray);
        } else if ("premium".equals(emailNotificationSendNowDto.getNotificationTarget())) {
            List<String> premiumEmailAddresses = userRepository.findEmailsByPremiumUsers();
            String[] emailArray = new String[premiumEmailAddresses.size()];
            premiumEmailAddresses.toArray(emailArray);
            mail.setTo(emailArray);
        } else if ("notPremium".equals(emailNotificationSendNowDto.getNotificationTarget())) {
            List<String> notPremiumEmailAddresses = userRepository.findEmailsByNotPremiumUsers();
            String[] emailArray = new String[notPremiumEmailAddresses.size()];
            notPremiumEmailAddresses.toArray(emailArray);
            mail.setTo(emailArray);
        }

    }

    private List<String> getAllEmailAddresses() {
        return userRepository.findAllEmails();
    }
}
