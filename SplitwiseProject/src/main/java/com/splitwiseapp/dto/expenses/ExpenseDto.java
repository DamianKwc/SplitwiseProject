package com.splitwiseapp.dto.expenses;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExpenseDto {

    @NotEmpty
    private String name;

    @NotEmpty
    private String amount;

    @NotEmpty
    private String participantUsername;
}
