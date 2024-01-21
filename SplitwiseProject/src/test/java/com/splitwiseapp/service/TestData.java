package com.splitwiseapp.service;

import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.Payoff;
import com.splitwiseapp.entity.User;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestData {

    static User user_1 = User.builder()
            .id(1)
            .firstName("Zdich")
            .username("zdichu")
            .build();

    static User user_2 = User.builder()
            .id(2)
            .firstName("Krycha")
            .username("kriszi")
            .build();

    static User user_3 = User.builder()
            .id(3)
            .firstName("Waldek")
            .username("waldi")
            .build();

    static Payoff payoff_1 = Payoff.builder()
            .userPaying(user_1)
            .payoffAmount(BigDecimal.valueOf(10))
            .build();

    static Payoff payoff_2 = Payoff.builder()
            .userPaying(user_2)
            .payoffAmount(BigDecimal.valueOf(20))
            .build();

    static Payoff payoff_3 = Payoff.builder()
            .userPaying(user_3)
            .payoffAmount(BigDecimal.valueOf(30))
            .build();

    public static Expense testExpense = Expense.builder()
            .costPerUser(prepareCostPerUser())
            .payoffs(List.of(payoff_1, payoff_2, payoff_3))
            .participants(Set.of(user_1, user_2, user_3))
            .build();

    private static Map<Integer, BigDecimal> prepareCostPerUser() {
        Map<Integer, BigDecimal> costPerUser = new HashMap<>();
        costPerUser.put(1, BigDecimal.valueOf(30));
        return costPerUser;
    }

}