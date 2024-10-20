package com.expensetracker.controller;

import com.expensetracker.model.User;
import com.expensetracker.model.Expense;
import com.expensetracker.services.ExpenseService;
import com.expensetracker.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PageController {

    @Autowired
    private UserServices userService;

    @Autowired
    private ExpenseService expenseService;

    // ====================== User Endpoints ======================

    // Create a new user
    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        if (userService.createUser(user)) {
            return new ResponseEntity<>("User created successfully!", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("User creation failed!", HttpStatus.BAD_REQUEST);
    }

    // Retrieve user details by ID
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ====================== Expense Endpoints ======================

    // Add a new expense
    @PostMapping("/expenses")
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {
        Expense savedExpense = expenseService.addExpense(expense);
        if (savedExpense != null) {
            return new ResponseEntity<>(savedExpense, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Retrieve expenses for a specific user
    @GetMapping("/expenses/{userId}")
    public ResponseEntity<List<Expense>> getUserExpenses(@PathVariable Long userId) {
        List<Expense> expenses = expenseService.getExpensesByUserId(userId);
        if (!expenses.isEmpty()) {
            return ResponseEntity.ok(expenses);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Retrieve overall expenses (all users)
    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getAllExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    /*
    // Download balance sheet as a CSV file
    @GetMapping("/balancesheet")
    public ResponseEntity<byte[]> downloadBalanceSheet() {
        byte[] csvData = expenseService.generateBalanceSheetCSV();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=balancesheet.csv");
        return ResponseEntity.ok().headers(headers).body(csvData);
    }

     */


}
