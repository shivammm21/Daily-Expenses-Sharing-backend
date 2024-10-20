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
        return expenseRepository.findExpensesByUserId(userId);
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

    /**
     * Generate a CSV of all expenses.
     *
     * @return Byte array representing the CSV.
     */
    public byte[] generateBalanceSheetCSV() {
        List<Expense> expenses = expenseRepository.findAll();
        StringBuilder csvBuilder = new StringBuilder();

        // Add CSV header
        csvBuilder.append("UserID,UserName,ExpenseID,ExpenseDescription,Amount\n");

        // Append expense data
        for (Expense expense : expenses) {
            // Assuming you have a way to retrieve user info from the expense
            String userName = "User_" + expense.getId(); // Replace with actual logic to get user name
            csvBuilder.append(expense.getId()).append(",")
                    .append(userName).append(",")
                    .append(expense.getId()).append(",")
                    .append(expense.getDescription()).append(",")
                    .append(expense.getAmount()).append("\n");
        }

        return csvBuilder.toString().getBytes();
    }

    /**
     * Generate CSV input stream for expenses.
     *
     * @return ByteArrayInputStream of the generated CSV.
     */
    public ByteArrayInputStream generateCSV() {
        List<Expense> expenses = expenseRepository.findAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        // Write CSV headers
        writer.println("User ID,Description,Amount,Date");

        // Write data rows
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

    /**
     * Get expense details with participant split.
     *
     * @param expenseId ID of the expense.
     * @return A map containing expense description, total amount, and split details.
     */
    public Map<String, Object> getExpenseWithSplit(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        BigDecimal totalAmount = expense.getAmount();
        Map<Long, BigDecimal> split = new HashMap<>();
        BigDecimal totalPercentage = BigDecimal.ZERO;

        // Calculate and assign split amounts to participants
        for (Participant participant : expense.getParticipants()) {
            totalPercentage = totalPercentage.add(BigDecimal.valueOf(participant.getPercentage()));
        }

        // Ensure that the total percentage does not exceed 100
        if (totalPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Total percentage cannot exceed 100.");
        }

        // Calculate split amounts
        for (Participant participant : expense.getParticipants()) {
            BigDecimal participantAmount = totalAmount
                    .multiply(BigDecimal.valueOf(participant.getPercentage()))
                    .divide(totalPercentage, BigDecimal.ROUND_HALF_UP);

            participant.setAmount(participantAmount);  // Store split amount in participant object
            split.put(participant.getUserId(), participantAmount);
        }

        // Create response structure
        Map<String, Object> response = new HashMap<>();
        response.put("description", expense.getDescription());
        response.put("amount", totalAmount);
        response.put("split", split);

        return response;
    }
}
