package com.splitwiseapp.repository;

import com.splitwiseapp.entity.EventsEntity;
import com.splitwiseapp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventsRepository extends JpaRepository<EventsEntity, Integer> {

    EventsEntity findByEventName(String eventName);

}
