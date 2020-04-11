package com.challenge.authorizer.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public class Transaction implements Comparable<Transaction> {

    private final String merchant;
    private final BigDecimal amount;
    private final Instant time;

    public Transaction(String merchant, BigDecimal amount, Instant time) {

        this.merchant = merchant;
        this.amount = amount;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return merchant.equals(that.merchant) &&
                amount.equals(that.amount);
    }

    @Override
    public int compareTo(Transaction transaction) {

        return this.time.compareTo(transaction.time);
    }

    public double minutesBetweenTransactions(Transaction transaction){

        if(transaction != null) {

            Duration duration = Duration.between(this.time, transaction.time);
            double durationMinutes = ((double) duration.toMillis()) / (60000F);
            return Math.abs(durationMinutes);
        }
        return 0.0D;
    }
}
