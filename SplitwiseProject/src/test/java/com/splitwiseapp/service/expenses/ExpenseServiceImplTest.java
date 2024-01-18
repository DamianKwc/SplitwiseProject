package com.splitwiseapp.service.expenses;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static com.splitwiseapp.service.TestData.testExpense;

@SpringBootTest
class ExpenseServiceImplTest {

    private final Map<Integer, BigDecimal> testAmountPerUser = new HashMap<>();

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Test
    void should_divide_cost_equally_per_each_participant() {
        // given
        // when
        BigDecimal actualOutcome = expenseService.splitCostEquallyPerParticipants(BigDecimal.TEN, 2);
        // then
        Assertions.assertThat(actualOutcome).isEqualTo(BigDecimal.valueOf(5).setScale(2, RoundingMode.CEILING));
    }

    @Test
    void should_calculate_payoff_amount_for_each_participant_of_expense() {
        // given
        testAmountPerUser.put(1, BigDecimal.valueOf(10));
        testAmountPerUser.put(2, BigDecimal.valueOf(20));
        testAmountPerUser.put(3, BigDecimal.valueOf(30));

        // when
        Map<Integer, BigDecimal> paidOffPerUser = expenseService.mapUserToPayoffAmount(testExpense);
        // then
        Assertions.assertThat(paidOffPerUser).isNotEmpty();
        Assertions.assertThat(paidOffPerUser).containsExactlyEntriesOf(testAmountPerUser);
    }

    @Test
    void should_calculate_balance_for_each_participant_of_expense() {
        // given
        testAmountPerUser.put(1, BigDecimal.valueOf(-20));
        testAmountPerUser.put(2, BigDecimal.valueOf(-10));
        testAmountPerUser.put(3, BigDecimal.ZERO);

        // when
        Map<Integer, BigDecimal> actualUserBalances = expenseService.mapUserToBalance(testExpense);

        // then
        Assertions.assertThat(actualUserBalances).isNotEmpty();
        Assertions.assertThat(actualUserBalances).containsExactlyEntriesOf(testAmountPerUser);
    }
}