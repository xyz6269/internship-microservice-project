package org.example.deliveryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.deliveryservice.entity.Driver;
import org.example.deliveryservice.enums.Status;
import org.example.deliveryservice.repository.DriverRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {
    private final DriverRepository driverRepository;

    public void addDriver(String phoneNumber) {
        String driverEmail = getCurrentUserEmail();
        Driver driver = new Driver();
        driver.setDriverEmail(driverEmail);
        driver.setDriverNumber(phoneNumber);
        driver.setStatus(Status.UNAVAILABLE);
        driverRepository.save(driver);
    }
    public void checkIn() {
        String driverEmail = getCurrentUserEmail();
        Driver driver =  driverRepository.findDriverByDriverEmail(driverEmail).orElseThrow(() -> new RuntimeException("no driver with the given Email"));
        driver.setStatus(Status.AVAILABLE);
        driverRepository.save(driver);
    }

    public Driver getAvailableDriver() {
        List<Driver> allDrivers = driverRepository.findAll();
        List<Driver> availableDrivers = new ArrayList<>();
        log.info(allDrivers.toString());
        for (Driver driver: allDrivers) {
            if(driver.getStatus() == Status.AVAILABLE){
                availableDrivers.add(driver);
            }
        }
        log.info(availableDrivers.toString());
        return availableDrivers.get(0);
    }

    public String getAvailableDriverEmail() {
        return getAvailableDriver().getDriverEmail();
    }

    public String getAvailableDriverPhone() {
        return getAvailableDriver().getDriverNumber();
    }

    public void backFromDuty() {
        String driverEmail = getCurrentUserEmail();
        Driver driver = driverRepository.findDriverByDriverEmail(driverEmail).orElseThrow(() -> new RuntimeException("no driver with the given Email"));
        driver.setStatus(Status.AVAILABLE);
        driverRepository.save(driver);
    }

    public void clockout() {
        String driverEmail = getCurrentUserEmail();
        Driver driver = driverRepository.findDriverByDriverEmail(driverEmail).orElseThrow(() -> new RuntimeException("no driver with the given Email"));
        driver.setStatus(Status.UNAVAILABLE);
        driverRepository.save(driver);
    }

    public void takeDelivery(String email) {
        Driver driver = driverRepository.findDriverByDriverEmail(email).orElseThrow(() -> new RuntimeException("no driver with the given Email"));
        driver.setStatus(Status.ON_DUTY);
        driverRepository.save(driver);
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
