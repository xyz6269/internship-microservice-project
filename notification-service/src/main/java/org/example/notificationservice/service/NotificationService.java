package org.example.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.dto.NotificationDTO;
import org.example.notificationservice.entity.Notification;
import org.example.notificationservice.repository.NotificationRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    public void addNotification(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setContext(dto.getContext());
        notification.setEmail(dto.getEmail());
        notification.setPrice(dto.getPrice());
        notification.setDeliveryGuyNumber(dto.getDriverNumber());
        notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications() {
         List<Notification> notifications = notificationRepository.findAllByEmail(getCurrentUserEmail());
         Collections.reverse(notifications);
         return notifications;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(authentication.getName());
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            throw new RuntimeException("the jwt is dead or some shit lol");
        }
        return authentication.getName();
    }
}
