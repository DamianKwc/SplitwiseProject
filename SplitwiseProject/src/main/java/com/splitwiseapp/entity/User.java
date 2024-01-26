package com.splitwiseapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @JsonIgnore
    @Column(name = "balance")
    private BigDecimal balance;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "eventMembers", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @Builder.Default
    private List<Event> userEvents = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @ManyToMany(mappedBy = "participants", cascade = {CascadeType.ALL})
    private Set<Expense> expenses = new HashSet<>();

    @OneToMany(mappedBy = "userPaying",  cascade = {CascadeType.ALL})
    private List<Payoff> payoffs;

    public void addEvent(Event event) {
        this.userEvents.add(event);
    }

    public void removeEvent(Event event) {
        this.userEvents.remove(event);
    }

    public void addExpense(Expense expense) {
        this.expenses.add(expense);
    }

    public void removeExpense(Expense expense) {
        this.expenses.remove(expense);
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                ", roles=" + roles.size() +
                ", userEvents=" + userEvents.size() +
                ", expenses=" + expenses.size() +
                ", payoffs=" + payoffs.size() +
                '}';
    }

}




