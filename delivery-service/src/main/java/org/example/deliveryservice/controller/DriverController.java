package org.example.deliveryservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.deliveryservice.service.DriverService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    @GetMapping("/get-driver")
    @PreAuthorize("hasRole('ROLE_CHEF')")
    public String getDriverPhone() {
        return driverService.getAvailableDriverPhone();
    }

    @GetMapping("/get-driver-email")
    @PreAuthorize("hasRole('ROLE_CHEF')")
    public String getDriverEmail() {
        return driverService.getAvailableDriverEmail();
    }

    @PostMapping("/register-driver")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public String registerDriver(@RequestBody() String phoneNumber) {
        driverService.addDriver(phoneNumber);
        return "Welcome driver";
    }
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public String checkIn() {
        driverService.checkIn();
        return "driver check in";
    }

    @PostMapping("/deliver/{email}")
    public String takeDelivery(@PathVariable("email") String email) {
        driverService.takeDelivery(email);
        return "driver went for delivery";
    }

    @PostMapping("/clock-out")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public String clockOut() {
        driverService.clockout();
        return "driver clocked out ";
    }

    @PostMapping("/back-delivery")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public String backToDuty() {
        driverService.backFromDuty();
        return "driver back on standby";
    }
}
