package com.splitwiseapp.entity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table( name = "bills")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private Integer id;

    @Column(name = "amount")
    private double amount;

    @Column(name = "owner_id")
    private int idOwner;

    @Column(name = "bill_name")
    private String billName;

    @Column(name = "event_id")
    private int idEvent;
}
