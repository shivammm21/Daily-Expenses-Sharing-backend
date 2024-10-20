package com.expensetracker.model;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class Participant {

    private Long userId;
    private BigDecimal amount;  // The split amount
    private Integer percentage; // Percentage of the total amount

    // Constructors
    public Participant() {}

    public Participant(Long userId, Integer percentage) {
        this.userId = userId;
        this.percentage = percentage;
    }

    public Participant(Long userId, BigDecimal amount, Integer percentage) {
        this.userId = userId;
        this.amount = amount;
        this.percentage = percentage;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    // toString Method
    @Override
    public String toString() {
        return "Participant{" +
                "userId=" + userId +
                ", amount=" + amount +
                ", percentage=" + percentage +
                '}';
    }
}
