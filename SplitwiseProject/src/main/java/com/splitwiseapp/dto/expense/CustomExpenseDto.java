package com.splitwiseapp.dto.expense;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomExpenseDto {

    @NotEmpty
    private String name;

    @NotEmpty
    private String cost;
}