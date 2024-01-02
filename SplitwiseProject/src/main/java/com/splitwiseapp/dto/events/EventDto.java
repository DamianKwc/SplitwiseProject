package com.splitwiseapp.dto.events;

import com.splitwiseapp.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventDto {

    @NotBlank
    private String eventName;

}
