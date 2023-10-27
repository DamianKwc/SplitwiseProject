package com.splitwiseapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private int idUser;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName="id_user"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName="id"))
    private List<RoleEntity> roles = new ArrayList<>();



    @OneToMany(mappedBy = "owner",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    private List<EventsEntity> events;



    @ManyToMany(fetch = FetchType.LAZY,
                        cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "event_members",
            joinColumns = @JoinColumn(name = "id_user", referencedColumnName = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_event", referencedColumnName = "id_event")
    )
    private List<EventsEntity> members;




}




