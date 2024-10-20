package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByParticipantsContaining(String participant);

        // JPQL query to search for a specific userId in the participants list
        @Query("SELECT e FROM Expense e JOIN e.participants p WHERE p.userId = :userId")
        List<Expense> findExpensesByUserId(@Param("userId") Long userId);


}
