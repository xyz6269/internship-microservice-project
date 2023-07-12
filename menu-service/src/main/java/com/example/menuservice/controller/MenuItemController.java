package com.example.menuservice.controller;



import com.example.menuservice.dto.ItemDTO;
import com.example.menuservice.dto.ItemResponse;
import com.example.menuservice.entity.Item;
import com.example.menuservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Slf4j
public class MenuItemController {

    private final ItemService itemService;

    @PostMapping("/add-item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewMenuItem(@RequestBody ItemDTO dto) {
        log.info("why tf you not working reeeeeeeeeeeeeeeeeeeeeeeetard");
        itemService.addItem(dto);
    }

    @DeleteMapping("/delete-item/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void removeNewMenuItem(@PathVariable("id") String id) {
        itemService.removeItem(id);
    }

    @GetMapping("/full-menu")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getFullMenu() {
        return itemService.getFullMenu();
    }

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public String getUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/place-items/{id}/{quantity}")
    @ResponseStatus(HttpStatus.OK)
    public String addItems(@PathVariable("id") String id,@PathVariable("quantity") int quantity) {
        return itemService.AddItem(id,quantity);
    }







}
