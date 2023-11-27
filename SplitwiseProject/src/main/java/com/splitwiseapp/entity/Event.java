package com.splitwiseapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer id;

    @Column(name = "event_name", unique = true)
    private String eventName;

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

    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @Builder.Default
    private List<Expense> expenses = new ArrayList<>();

    public void addExpense(Expense expense) {
        this.expenses.add(expense);
        expense.setEvent(this);
    }

    public void removeExpense(Expense expense) {
        this.expenses.remove(expense);
        expense.setEvent(null);
    }

    public void addUser (User user) {
        this.eventUsers.add(user);
    }

    public void removeUser(User user) {
        this.eventUsers.remove(user);
    }
}
