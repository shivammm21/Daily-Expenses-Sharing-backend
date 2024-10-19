package com.expensetracker.services;

import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    /**
     * Add a new expense to the system.
     *
     * @param expense Expense object containing expense details.
     * @return The saved Expense object.
     */
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    /**
     * Retrieve all expenses.
     *
     * @return List of all expenses.
     */
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    /**
     * Retrieve expenses for a specific user.
     *
     * @param userId ID of the user whose expenses are to be retrieved.
     * @return List of expenses for the user.
     */
    public List<Expense> getExpensesByUserId(Long userId) {
        return expenseRepository.findByParticipantsContaining(String.valueOf(userId));
    }

    /**
     * Calculate total expenses for a specific user.
     *
     * @param userId ID of the user.
     * @return Total expenses for the user.
     */
    public BigDecimal calculateTotalExpensesByUserId(Long userId) {
        return getExpensesByUserId(userId).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public byte[] generateBalanceSheetCSV() {
        List<Expense> expenses = expenseRepository.findAll();
        StringBuilder csvBuilder = new StringBuilder();

        // Add CSV header
        csvBuilder.append("UserID,UserName,ExpenseID,ExpenseDescription,Amount\n");

        // Append expense data
        for (Expense expense : expenses) {
            // Assuming you have a way to retrieve user info from the expense
            // This can be an external call to a UserService or similar
            String userName = "User_" + expense.getId(); // Replace with actual logic to get user name
            csvBuilder.append(expense.getId()).append(",")
                    .append(userName).append(",")
                    .append(expense.getId()).append(",")
                    .append(expense.getDescription()).append(",")
                    .append(expense.getAmount()).append("\n");
        }

        // Convert to byte array
        return csvBuilder.toString().getBytes();
    }
}
