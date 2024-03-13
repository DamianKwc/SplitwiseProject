package com.splitwiseapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "payoff")
public class Payoff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "payoff_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expensePaid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userPaying;

    @Column(name = "payoff_amount", nullable = false)
    private BigDecimal payoffAmount;
}
