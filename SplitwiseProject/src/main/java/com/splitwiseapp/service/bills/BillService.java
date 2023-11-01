package com.splitwiseapp.service.bills;

import com.splitwiseapp.entity.Bill;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface BillService {

    public List<Bill> findAll();
}
