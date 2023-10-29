package com.splitwiseapp.service.members;

import com.splitwiseapp.dto.events.EventDto;
import com.splitwiseapp.entity.EventEntity;
import com.splitwiseapp.entity.EventMemberEntity;
import com.splitwiseapp.entity.UserEntity;
import com.splitwiseapp.repository.MemberRepository;
import com.splitwiseapp.repository.UserRepository;
import com.splitwiseapp.service.events.EventService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static com.splitwiseapp.shared.UserUtils.getCurrentlyLoggedInUser;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private UserRepository userRepository;
    private MemberRepository memberRepository;
    private EventService eventService;

    @Override
    public void saveEventMember(EventDto eventDto) {
        UserEntity currentlyLoggedInUser = getCurrentlyLoggedInUser(userRepository);
        EventEntity currentEvent = eventService.findByEventName(eventDto.getEventName());

        EventMemberEntity eventMember = EventMemberEntity.builder()
                .memberName(currentlyLoggedInUser.getUsername())
                .eventName(eventDto.getEventName())
                .memberId(currentlyLoggedInUser.getId())
                .eventId(currentEvent.getId())
                .build();

        memberRepository.save(eventMember);
    }
}
