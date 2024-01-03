package com.splitwiseapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "events")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer id;

    @Column(name = "event_name", unique = true)
    private String eventName;

    @Temporal(TemporalType.DATE)
    @Column(name = "creation_date")
    private LocalDate creationDate;

    @JsonIgnore
    @Column(name = "eventBalance")
    private BigDecimal eventBalance;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "event_members",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> eventUsers = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH,CascadeType.REMOVE})
    private Set<Expense> expenses;

    public void addUser(User user) {
        this.eventUsers.add(user);
    }

    public void removeUser(User user) {
        this.eventUsers.remove(user);
    }

    public void addExpense(Expense expense) {
        this.expenses.add(expense);
    }

    public void removeExpense(Expense expense) {
        this.expenses.remove(expense);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventName='" + eventName + '\'' +
                ", eventBalance=" + eventBalance +
                ", owner=" + owner.getFirstName() +
                ", eventUsers=" + eventUsers +
                ", expenses=" + expenses +
                '}';
    }
}
