package com.challenge.authorizer.domain;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionTest {

    @Test
    public void doReturnGreaterThanZeroIfTransactionMoreRecent() {

        Transaction transaction1 = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:01:00.000Z"));
        Transaction transaction2 = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:00:00.000Z"));

        Assert.assertTrue(transaction1.compareTo(transaction2) > 0);
    }

    @Test
    public void doReturnLessThanZeroIfTransactionBeforeActual() {

        Transaction transaction1 = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:01:00.000Z"));
        Transaction transaction2 = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:00:00.000Z"));

        Assert.assertTrue(transaction2.compareTo(transaction1) < 0);
    }

    @Test
    public void doReturnZeroIfTransactionsOnSameTime() {

        Transaction transaction1 = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:01:00.000Z"));
        Transaction transaction2 = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:01:00.000Z"));

        Assert.assertTrue(transaction2.compareTo(transaction1) == 0);
    }

    @Test
    public void doReturnZeroWhenCallingMinutesBetweenTransactionsWithNullTransaction() {

        Transaction transaction = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:01:00.000Z"));
        Assert.assertEquals(0.0D, transaction.minutesBetweenTransactions(null),0);
    }

    @Test
    public void doReturnValidIntervalWhenCallingMinutesBetweenTransactionsWithValidTransaction() {

        Transaction transaction1 = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:00:00.000Z"));
        Transaction transaction2 = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:01:00.000Z"));

        Assert.assertEquals(1.0D, transaction2.minutesBetweenTransactions(transaction1),0);
    }

    @Test
    public void doReturnTrueWhenCallingEqualsForSameObject() {

        Transaction transaction = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:01:00.000Z"));
        Assert.assertTrue(transaction.equals(transaction));
    }

    @Test
    public void doReturnFalseWhenCallingEqualsForObjectOfAnotherClass() {

        Transaction transaction = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:01:00.000Z"));
        Assert.assertFalse(transaction.equals(BigDecimal.ZERO));
    }

    @Test
    public void doReturnFalseWhenCallingEqualsForNullObject() {

        Transaction transaction = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:01:00.000Z"));
        Assert.assertFalse(transaction.equals(null));
    }

    @Test
    public void doReturnTrueWhenCallingEqualsForObjectsWithSameMerchantAndAmount() {

        Transaction transaction1 = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:01:00.000Z"));
        Transaction transaction2 = new Transaction("teste", BigDecimal.ONE,
                Instant.parse("2019-02-13T10:01:00.000Z"));

        Assert.assertTrue(transaction1.equals(transaction2));
    }
}