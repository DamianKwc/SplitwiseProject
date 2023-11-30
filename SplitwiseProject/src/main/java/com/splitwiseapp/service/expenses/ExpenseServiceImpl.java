package com.splitwiseapp.service.expenses;

import com.splitwiseapp.dto.expenses.ExpenseDto;
import com.splitwiseapp.dto.users.UserDto;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.Role;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.repository.EventRepository;
import com.splitwiseapp.repository.ExpenseRepository;
import com.splitwiseapp.repository.UserRepository;
import lombok.Data;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;


@Data
@Service
public class ExpenseServiceImpl implements ExpenseService {

    private ExpenseRepository expenseRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;

    @Override
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    public List<Expense> getExpensesByEvent(Event event) {
        return expenseRepository.findByEvent(event);
    }

    @Override
    public List<Expense> getExpensesByUser(User user) {
        return expenseRepository.findByUser(user);
    }

    @Override
    public Expense getExpenseById(Integer expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
    }

//    @Override
//    public Expense createExpense(ExpenseDto expenseDto) {
//        Event event = eventRepository.findById(expenseDto.getEventId())
//                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + expenseDto.getEventId()));
//
//        User user = userRepository.findById(expenseDto.getUserId())
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + expenseDto.getUserId()));
//
//        Expense expense = new Expense();
//        expense.setExpenseName(expenseDto.getExpenseName());
//        expense.setEvent(event);
//        expense.setUser(user);
//
//        return expenseRepository.save(expense);
//    }

    @Override
    public void deleteExpense(Integer expenseId) {
        Expense expense = getExpenseById(expenseId);
        expenseRepository.delete(expense);
    }

    @Override
    public void saveExpense(ExpenseDto expenseDto) {
        Expense expense = new Expense();
        expense.setExpenseName(expenseDto.getExpenseName());

        expenseRepository.save(expense);
    }

    @Override
    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public void saveExpense(Expense expense) {
        expenseRepository.save(expense);
    }

    private ExpenseDto mapToExpenseDto(Expense expense){
        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setExpenseName(expense.getExpenseName());
        return expenseDto;
    }
}
