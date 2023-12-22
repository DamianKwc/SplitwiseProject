package com.splitwiseapp.service.users;

import com.splitwiseapp.dto.users.UserDto;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.Role;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.repository.RoleRepository;
import com.splitwiseapp.repository.UserRepository;
import com.splitwiseapp.service.events.EventService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final EventService eventService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private Role checkRoleExist(){
        Role role = new Role();
        role.setRole("ROLE_ADMIN");
        return roleRepository.save(role);
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setUsername(userDto.getUsername());
        // encrypt the password using spring security
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByRole("ROLE_ADMIN");
        if(role == null){
            role = checkRoleExist();
        }
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    private UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

    @Override
    public User findById(Integer userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getCurrentlyLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    @Override
    public BigDecimal calculateUserDebt(Integer userId) {
        User foundUser = userRepository.findById(userId).orElseThrow();

        List<Event> eventsWithUserAsParticipant = eventService.findAllEvents().stream()
                .filter(event -> event.getEventUsers().contains(foundUser))
                .collect(Collectors.toList());

        return eventsWithUserAsParticipant.stream()
                .map(Event::getExpenses)
                .flatMap(Set::stream)
                .filter(expense -> expense.getParticipants().contains(foundUser))
                .map(Expense::getEqualSplit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateUserBalance(Integer userId, BigDecimal paidOffAmount) {
        User foundUser = userRepository.findById(userId).orElseThrow();
        BigDecimal balance = foundUser.getBalance();
        BigDecimal calculatedBalance = null;
        if (balance != null) {
            calculatedBalance = balance.add(paidOffAmount);
        }
        return calculatedBalance;
    }

    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

}