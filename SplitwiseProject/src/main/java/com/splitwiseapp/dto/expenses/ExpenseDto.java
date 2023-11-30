package com.splitwiseapp.dto.expenses;

import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExpenseDto {

    private String expenseName;
    private Integer eventId;
    private Integer userId;
    private User user;
    private Event event;

}
