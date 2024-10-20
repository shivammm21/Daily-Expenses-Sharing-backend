package com.expensetracker.services;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Participant;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getExpensesByUserId(Long userId) {
        return expenseRepository.findExpensesByUserId(userId);
    }

    public BigDecimal calculateTotalExpensesByUserId(Long userId) {
        return getExpensesByUserId(userId).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public byte[] generateBalanceSheetCSV() {
        List<Expense> expenses = expenseRepository.findAll();
        StringBuilder csvBuilder = new StringBuilder();

        csvBuilder.append("UserID,UserName,ExpenseID,ExpenseDescription,Amount\n");

        for (Expense expense : expenses) {
            String userName = "User_" + expense.getId();
            csvBuilder.append(expense.getId()).append(",")
                    .append(userName).append(",")
                    .append(expense.getId()).append(",")
                    .append(expense.getDescription()).append(",")
                    .append(expense.getAmount()).append("\n");
        }

        return csvBuilder.toString().getBytes();
    }

    public ByteArrayInputStream generateCSV() {
        List<Expense> expenses = expenseRepository.findAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        writer.println("User ID,Description,Amount,Date");

        for (Expense expense : expenses) {
            writer.printf("%d,%s,%.2f,%s%n",
                    expense.getId(),
                    expense.getDescription(),
                    expense.getAmount(),
                    expense.getDate()
            );
        }
        writer.flush();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public Map<String, Object> getExpenseWithSplit(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        BigDecimal totalAmount = expense.getAmount();
        Map<Long, BigDecimal> split = new HashMap<>();
        BigDecimal totalPercentage = BigDecimal.ZERO;

        for (Participant participant : expense.getParticipants()) {
            totalPercentage = totalPercentage.add(BigDecimal.valueOf(participant.getPercentage()));
        }

        if (totalPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Total percentage cannot exceed 100.");
        }

        for (Participant participant : expense.getParticipants()) {
            BigDecimal participantAmount = totalAmount
                    .multiply(BigDecimal.valueOf(participant.getPercentage()))
                    .divide(totalPercentage, BigDecimal.ROUND_HALF_UP);

            participant.setAmount(participantAmount);
            split.put(participant.getUserId(), participantAmount);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("description", expense.getDescription());
        response.put("amount", totalAmount);
        response.put("split", split);

        return response;
    }
}
