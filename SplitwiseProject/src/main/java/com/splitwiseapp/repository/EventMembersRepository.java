package com.splitwiseapp.repository;

import com.splitwiseapp.entity.EventMembers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventMembersRepository extends JpaRepository<EventMembers, Integer> {
    EventMembers findByEventId(Integer eventId);
}
