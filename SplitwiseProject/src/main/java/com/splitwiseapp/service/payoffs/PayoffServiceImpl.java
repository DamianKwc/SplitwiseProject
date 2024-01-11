package com.splitwiseapp.service.payoffs;

import com.splitwiseapp.entity.Payoff;
import com.splitwiseapp.repository.PayoffRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PayoffServiceImpl implements PayoffService {

    private PayoffRepository payoffRepository;

    @Override
    public void save(Payoff payoff) {
        payoffRepository.save(payoff);
    }
}