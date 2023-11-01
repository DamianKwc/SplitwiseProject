package com.splitwiseapp.dto.events;

import com.splitwiseapp.entity.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventDto {

    @NotEmpty
    private String eventName;

    @NotEmpty
    private User participant;
}
