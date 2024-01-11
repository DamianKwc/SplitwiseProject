package com.splitwiseapp.dto.user;

import com.splitwiseapp.dto.expense.ExpenseDto;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.Role;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@AllArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public User mapToDomain(UserDto userDto) {
        Role role = roleRepository.findByRole("ROLE_ADMIN");
        if (role == null) {
            role = checkRoleExist();
        }

        return User.builder()
                .firstName(userDto.getFirstName())
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .balance(BigDecimal.ZERO)
                .roles(List.of(role))
                .build();
    }

    public static ExpenseDto mapToDto(Expense expense) {
        return null;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setRole("ROLE_ADMIN");
        return roleRepository.save(role);
    }

}
