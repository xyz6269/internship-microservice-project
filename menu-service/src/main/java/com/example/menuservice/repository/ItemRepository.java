package com.example.menuservice.repository;

import com.example.menuservice.entity.Item;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {

    Optional<Item> findMenuItemByName(String name);
    List<Item> findMenuItemByNameIn(List<String> names);
}
