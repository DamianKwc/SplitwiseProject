package com.splitwiseapp.service.users;

import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.repository.EventRepository;
import com.splitwiseapp.repository.UserRepository;
import com.splitwiseapp.service.events.EventServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private UserServiceImpl userService;

    @BeforeEach
    void init() {
        userService = new UserServiceImpl(eventService, userRepository);
    }

    @Test
    void should_calculate_proper_user_balance() {
        // given
        BigDecimal expectedBalance = BigDecimal.valueOf(30).negate();
        Optional<User> testUser = Optional.ofNullable(User.builder()
                .id(1)
                .firstName("Józef")
                .username("Jozi")
                .userEvents(List.of(Event.builder().build()))
                .build());
        Expense testExpense1 = Expense.builder()
                .participants(Set.of(testUser.get()))
                .costPerUser(Map.of(1, BigDecimal.valueOf(10)))
                .build();
        Expense testExpense2 = Expense.builder()
                .participants(Set.of(testUser.get()))
                .costPerUser(Map.of(1, BigDecimal.valueOf(20)))
                .build();
        Event event = Event.builder()
                .id(1)
                .eventName("testEvent")
                .creationDate(LocalDate.of(2024, 1, 24))
                .eventMembers(List.of(testUser.get()))
                .expenses(List.of(testExpense1, testExpense2))
                .build();
        when(userRepository.findById(any())).thenReturn(testUser);
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventService.findAllEvents()).thenReturn(List.of(event));

        // when
        BigDecimal result = userService.calculateUserBalance(1);

        // then
        Assertions.assertThat(result).isEqualTo(expectedBalance);
    }
}