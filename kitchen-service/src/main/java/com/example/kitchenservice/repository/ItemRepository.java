package com.example.kitchenservice.repository;

import com.example.kitchenservice.entity.Item;
import com.example.kitchenservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}