package com.splitwiseapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table( name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Integer id;

    @JsonIgnore
    @Column(name = "expense_name", nullable = false)
    private String name;

    @JsonIgnore
    @Column(name = "cost", nullable = false)
    private BigDecimal cost;

    @JsonIgnore
    @Column(name = "paid_off_amount")
    private BigDecimal paidOffAmount;

    @JsonIgnore
    @Column(name = "expenseBalance")
    private BigDecimal expenseBalance;

    @JsonIgnore
    @Column(name = "equal_split")
    private BigDecimal equalSplit;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "expense_participants",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> participants = new HashSet<>();

    public void addEvent(Event event) {
        setEvent(event);
    }

    public void removeEvent() {
        this.event = null;
    }

    public void addParticipant(User participant) {
        this.participants.add(participant);
    }

    public void removeParticipant(User participant) {
        this.participants.remove(participant);
    }
}
