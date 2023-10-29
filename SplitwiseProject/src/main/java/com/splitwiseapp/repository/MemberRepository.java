package com.splitwiseapp.repository;

import com.splitwiseapp.entity.EventMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<EventMemberEntity, Integer> {
}
