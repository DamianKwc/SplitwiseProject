package com.splitwiseapp.service.members;

import com.splitwiseapp.dto.events.EventDto;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    void saveEventMember(EventDto eventDto);
}
