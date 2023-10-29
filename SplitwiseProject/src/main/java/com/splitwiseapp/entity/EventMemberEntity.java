package com.splitwiseapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_members")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "id_user")
    private int memberId;

    @Column(name = "id_event")
    private int eventId;

}
