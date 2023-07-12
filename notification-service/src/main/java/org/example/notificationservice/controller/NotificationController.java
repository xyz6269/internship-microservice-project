package org.example.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.notificationservice.dto.NotificationDTO;
import org.example.notificationservice.entity.Notification;
import org.example.notificationservice.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/noti")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    @PostMapping("/new-noti")
    public String addNotification(@RequestBody NotificationDTO dto) {
         notificationService.addNotification(dto);
         return "notification sent";
    }
    @GetMapping("/my-noti")
    public List<Notification> addNotification() {
        return notificationService.getUserNotifications();
    }

    @GetMapping("/all-notis")
    public List<Notification> getAllNotis() {
        return notificationService.getAllNotifications();
    }
}
