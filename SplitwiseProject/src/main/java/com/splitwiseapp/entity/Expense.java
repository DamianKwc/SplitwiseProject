package com.splitwiseapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@Builder
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
    @Column(name = "expense_cost", nullable = false)
    private BigDecimal totalCost;

    @JsonIgnore
    @Column(name = "expenseBalance")
    private BigDecimal expenseBalance;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "debt_per_user_mapping",
            joinColumns = @JoinColumn(name = "expense_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "debt_per_user")
    private Map<Integer, BigDecimal> debtPerUser = new HashMap<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "cost_per_user_mapping",
            joinColumns = @JoinColumn(name = "expense_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "cost_per_user")
    private Map<Integer, BigDecimal> costPerUser = new HashMap<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "payoff_per_user_mapping",
            joinColumns = @JoinColumn(name = "expense_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "payoff_per_user")
    private Map<Integer, BigDecimal> payoffPerUser = new HashMap<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "balance_per_user_mapping",
            joinColumns = @JoinColumn(name = "expense_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "balance_per_user")
    private Map<Integer, BigDecimal> balancePerUser = new HashMap<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "expense_participants",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> participants = new LinkedList<>();

    @OneToMany(mappedBy = "expensePaid", cascade = {CascadeType.REMOVE})
    private List<Payoff> payoffs;

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

    @Override
    public String toString() {
        return "Expense{" +
                "name='" + name + '\'' +
                ", totalCost=" + totalCost +
                ", expenseBalance=" + expenseBalance +
                ", event=" + event.getEventName() +
                ", participants=" + participants +
                ", payoffs=" + payoffs +
                '}';
    }
}
