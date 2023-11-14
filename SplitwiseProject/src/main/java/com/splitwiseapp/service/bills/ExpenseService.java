package com.splitwiseapp.service.bills;

import com.splitwiseapp.entity.Expense;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface ExpenseService {

    public List<Expense> findAll();
}
