package com.splitwiseapp.dto.eventsDto;

import com.splitwiseapp.entity.UserEntity;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class EventsDto {

    @NotEmpty
    private String eventName;


}
