package com.splitwiseapp.dto.event;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventDto {

    private Integer id;

    @NotBlank
    private String eventName;

}
