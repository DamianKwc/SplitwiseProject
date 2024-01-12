package com.splitwiseapp.dto.expense;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExpenseDto {

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Name cannot be null")
    private String cost;

    @NotNull(message = "Name cannot be null")
    private String participantUsername;
}
