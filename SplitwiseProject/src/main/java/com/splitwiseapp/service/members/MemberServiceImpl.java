//package com.splitwiseapp.service.members;
//
//import com.splitwiseapp.dto.events.EventDto;
//import com.splitwiseapp.entity.Event;
//import com.splitwiseapp.entity.User;
//import com.splitwiseapp.repository.MemberRepository;
//import com.splitwiseapp.repository.UserRepository;
//import com.splitwiseapp.service.events.EventService;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import static com.splitwiseapp.shared.UserUtils.getCurrentlyLoggedInUser;
//
//@Service
//@AllArgsConstructor
//public class MemberServiceImpl implements MemberService {
//
//    private UserRepository userRepository;
//    private MemberRepository memberRepository;
//    private EventService eventService;
//
//    @Override
//    public void saveEventMember(EventDto eventDto) {
//        User currentlyLoggedInUser = getCurrentlyLoggedInUser(userRepository);
//        Event currentEvent = eventService.findByEventName(eventDto.getEventName());
//
//        EventMember eventMember = EventMember.builder()
//                .memberName(currentlyLoggedInUser.getUsername())
//                .eventName(eventDto.getEventName())
//                .memberId(currentlyLoggedInUser.getId())
//                .eventId(currentEvent.getId())
//                .build();
//
//        memberRepository.save(eventMember);
//    }
//}
