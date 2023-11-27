package com.splitwiseapp.dto.expenses;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExpenseDto {

    @NotEmpty
    private BigDecimal amount;

    @NotEmpty
    private String expenseName;

}
