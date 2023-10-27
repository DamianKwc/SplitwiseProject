package com.splitwiseapp.dto.events;

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
}
