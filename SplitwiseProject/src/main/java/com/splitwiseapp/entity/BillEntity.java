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
public class BillEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bill")
    private int idBill;

    @Column(name = "amount")
    private double amount;

    @Column(name = "id_owner")
    private int idOwner;

    @Column(name = "bill_name")
    private String billName;

    @Column(name = "id_event")
    private int idEvent;

    @Column(name = "id_members")
    private String idMembers;




}
