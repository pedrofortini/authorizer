package com.challenge.authorizer.domain;

import com.challenge.authorizer.enums.ViolationEnum;
import com.challenge.authorizer.events.AccountCreationEvent;
import com.challenge.authorizer.events.TransactionAuthorizationEvent;
import com.challenge.authorizer.state.AccountDataStore;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;

public class AccountTest {

    @Test
    public void doReturnCopyOfTheAccountWhenCallingCopyAccountMethod() {

        Account account = new Account(true, BigDecimal.TEN);
        Assert.assertEquals(account, account.copyAccount());
    }

    @Test
    public void doPersistAccountOnAccountDataStoreWhenCallingHandleEventForAccountCreationEvent() {

        Account account = new Account(true, BigDecimal.TEN);
        account.handleEvent(new AccountCreationEvent(true, BigDecimal.TEN));
        Assert.assertEquals(AccountDataStore.getInstance().getAccount(), account);
    }

    @Test
    public void shouldReturnFalseWhenCallingCheckCardNotActiveViolationOnActiveCard() {

        Account account = new Account(true, BigDecimal.TEN);
        Assert.assertFalse(account.checkCardNotActiveViolation());
    }

    @Test
    public void shouldReturnTrueWhenCallingCheckCardNotActiveViolationOnInActiveCard() {

        Account account = new Account(false, BigDecimal.TEN);
        Assert.assertTrue(account.checkCardNotActiveViolation());
    }

    @Test
    public void shouldReturnTrueWhenCallingCheckInsufficientLimitViolationOnCardWithInsufficientLimit() {

        Account account = new Account(true, BigDecimal.TEN);
        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent("test",
                new BigDecimal(20), Instant.now());

        Assert.assertTrue(account.checkInsufficientLimitViolation(event));
    }

    @Test
    public void shouldReturnFalseWhenCallingCheckInsufficientLimitViolationOnNullEvent() {

        Account account = new Account(true, BigDecimal.TEN);
        Assert.assertFalse(account.checkInsufficientLimitViolation(null));
    }

    @Test
    public void shouldReturnFalseWhenCallingCheckInsufficientLimitViolationOnEventWithNullAmount() {

        Account account = new Account(true, BigDecimal.TEN);
        Assert.assertFalse(account.checkInsufficientLimitViolation(new TransactionAuthorizationEvent("teste", null, Instant.now())));
    }

    @Test
    public void shouldReturnFalseWhenCallingCheckInsufficientLimitViolationWithNegativeAmountTransactionEvent() {

        Account account = new Account(true, BigDecimal.TEN);
        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent("test",
                new BigDecimal(-20), Instant.now());

        Assert.assertFalse(account.checkInsufficientLimitViolation(event));
    }

    @Test
    public void shouldReturnFalseWhenCallingCheckInsufficientLimitViolationWithZeroAmountTransactionEvent() {

        Account account = new Account(true, BigDecimal.TEN);
        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent("test",
                BigDecimal.ZERO, Instant.now());

        Assert.assertFalse(account.checkInsufficientLimitViolation(event));
    }

    @Test
    public void shouldReturnFalseWhenCallingCheckInsufficientLimitViolationOnAccountWithSufficientAvailableLimit() {

        Account account = new Account(true, BigDecimal.TEN);
        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent("test",
                BigDecimal.ONE, Instant.now());

        Assert.assertFalse(account.checkInsufficientLimitViolation(event));
    }

    @Test
    public void shouldReturnFalseWhenCallingCheckHighFrequencySmallIntervalViolationOnNullList() {

        Account account = new Account(true, BigDecimal.TEN);
        Assert.assertFalse(account.checkHighFrequencySmallIntervalViolation(null));
    }

    @Test
    public void shouldReturnFalseWhenCallingCheckHighFrequencySmallIntervalViolationOnListWithLessThan3Elements() {

        Account account = new Account(true, BigDecimal.TEN);
        Assert.assertFalse(account.checkHighFrequencySmallIntervalViolation(new ArrayList<>()));
    }

    @Test
    public void shouldReturnTrueWhenCallingCheckHighFrequencySmallIntervalViolationOnListWithMoreThan3Element() {

        Account account = new Account(true, BigDecimal.TEN);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("teste1", BigDecimal.ONE, Instant.now()));
        transactions.add(new Transaction("teste2", BigDecimal.ONE, Instant.now()));
        transactions.add(new Transaction("teste3", BigDecimal.ONE, Instant.now()));
        transactions.add(new Transaction("teste4", BigDecimal.ONE, Instant.now()));

        Assert.assertTrue(account.checkHighFrequencySmallIntervalViolation(transactions));
    }

    @Test
    public void shouldReturnFalseWhenCallingCheckDoubledTransactionOnNullList() {

        Account account = new Account(true, BigDecimal.TEN);
        Assert.assertFalse(account.checkDoubledTransaction(null,
                new Transaction("teste1", BigDecimal.ONE, Instant.now())));
    }

    @Test
    public void shouldReturnFalseWhenCallingCheckDoubledTransactionWithNullInsertedTransaction() {

        Account account = new Account(true, BigDecimal.TEN);
        Assert.assertFalse(account.checkDoubledTransaction(new ArrayList<>(), null));
    }

    @Test
    public void shouldReturnFalseWhenCallingCheckDoubledTransactionWithListThatDoesntContainsInsertedTransaction() {

        Account account = new Account(true, BigDecimal.TEN);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("teste1", BigDecimal.ONE, Instant.now()));
        transactions.add(new Transaction("teste2", BigDecimal.ONE, Instant.now()));
        transactions.add(new Transaction("teste3", BigDecimal.ONE, Instant.now()));

        Transaction insertedTransaction = new Transaction("teste4", BigDecimal.ONE, Instant.now());
        transactions.add(insertedTransaction);

        Assert.assertFalse(account.checkDoubledTransaction(transactions, insertedTransaction));
    }

    @Test
    public void shouldReturnTrueWhenCallingCheckDoubledTransactionWithListThatDoesContainsDuplicatedInsertedTransaction() {

        Account account = new Account(true, BigDecimal.TEN);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("teste1", BigDecimal.ONE, Instant.now()));
        transactions.add(new Transaction("teste2", BigDecimal.ONE, Instant.now()));
        transactions.add(new Transaction("teste3", BigDecimal.ONE, Instant.now()));
        transactions.add(new Transaction("teste4", BigDecimal.ONE, Instant.now()));

        Transaction insertedTransaction = new Transaction("teste4", BigDecimal.ONE, Instant.now());
        transactions.add(insertedTransaction);

        Assert.assertTrue(account.checkDoubledTransaction(transactions, insertedTransaction));
    }

    @Test
    public void shouldReturnTransactionInstanceWhenCallingInsertTransactionEventOrderedByTime() {

        Account account = new Account(true, BigDecimal.TEN);
        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent("test",
                BigDecimal.ONE, Instant.now());

        Assert.assertThat(account.insertTransactionEventOrderedByTime(event), instanceOf(Transaction.class));
    }

    @Test
    public void shouldInsertTransactionsOrderedByTimeWhenCallingInsertTransactionEventOrderedByTime() {

        Account account = new Account(true, BigDecimal.TEN);
        TransactionAuthorizationEvent event1 = new TransactionAuthorizationEvent("test1",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:01:00.000Z"));
        TransactionAuthorizationEvent event2 = new TransactionAuthorizationEvent("test2",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:00:00.000Z"));

        Transaction firstInserted = account.insertTransactionEventOrderedByTime(event1);
        Transaction lastInserted = account.insertTransactionEventOrderedByTime(event2);

        Assert.assertEquals(lastInserted, account.getTransactions().first());
        Assert.assertEquals(firstInserted, account.getTransactions().last());
    }

    @Test
    public void shouldNotReduceAvailableLimitWhenCallingReduceAvailableLimitWithNullAmount() {

        Account account = new Account(true, BigDecimal.TEN);
        account.reduceAvailableLimit(null);
        Assert.assertEquals(BigDecimal.TEN, account.getAvailableLimit());
    }

    @Test
    public void shouldNotReduceAvailableLimitWhenCallingReduceAvailableLimitWithNullAvailableLimit() {

        Account account = new Account(true, null);
        account.reduceAvailableLimit(BigDecimal.ONE);
        Assert.assertNull(account.getAvailableLimit());
    }

    @Test
    public void shouldReduceAvailableLimitWhenCallingReduceAvailableLimitWithValidMoneyAndAvailableLimit() {

        Account account = new Account(true, BigDecimal.ONE);
        account.reduceAvailableLimit(BigDecimal.ONE);
        Assert.assertEquals(BigDecimal.ZERO, account.getAvailableLimit());
    }

    @Test
    public void shouldReturnEmptyListWhenCallingGet2MinuteTransactionWindowBeforeLastInsertedTransactionWithNullTransaction() {

        Account account = new Account(true, BigDecimal.TEN);
        Assert.assertTrue(account.get2MinuteInterval(null).isEmpty());
    }

    @Test
    public void shouldReturnEmptyListWhenCallingGet2MinuteTransactionWindowBeforeLastInsertedTransactionWithLessThan2MinutesInterval() {

        Account account = new Account(true, BigDecimal.TEN);
        TransactionAuthorizationEvent event1 = new TransactionAuthorizationEvent("test1",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:01:00.000Z"));
        TransactionAuthorizationEvent event2 = new TransactionAuthorizationEvent("test2",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:00:00.000Z"));

        account.insertTransactionEventOrderedByTime(event1);
        Transaction lastInserted = account.insertTransactionEventOrderedByTime(event2);

        Assert.assertTrue(account.get2MinuteInterval(lastInserted).isEmpty());
    }

    @Test
    public void shouldReturnListOfTransactionsWhenCallingGet2MinuteTransactionWindowBeforeLastInsertedTransactionWithValid2MinutesInterval() {

        Account account = new Account(true, BigDecimal.TEN);
        TransactionAuthorizationEvent event1 = new TransactionAuthorizationEvent("test1",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:01:00.000Z"));
        TransactionAuthorizationEvent event2 = new TransactionAuthorizationEvent("test2",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:00:00.000Z"));
        TransactionAuthorizationEvent event3 = new TransactionAuthorizationEvent("test3",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:02:00.000Z"));
        TransactionAuthorizationEvent event4 = new TransactionAuthorizationEvent("test4",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:03:00.000Z"));

        account.insertTransactionEventOrderedByTime(event1);
        account.insertTransactionEventOrderedByTime(event2);
        account.insertTransactionEventOrderedByTime(event3);
        Transaction lastInserted = account.insertTransactionEventOrderedByTime(event4);

        Assert.assertEquals(3, account.get2MinuteInterval(lastInserted).size());
    }

    @Test
    public void shouldReturnCardNotActiveViolationWhenTransactionViolatesThisBusinessRule(){

        Account account = new Account(false, BigDecimal.TEN);
        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent("test",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:01:00.000Z"));

        List<String> violations = account.handleEvent(event);
        Assert.assertTrue(violations.contains(ViolationEnum.CARD_NOT_ACTIVE.getDescription()));
    }

    @Test
    public void shouldReturnInsufficientLimitViolationWhenTransactionViolatesThisBusinessRule(){

        Account account = new Account(true, BigDecimal.ZERO);
        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent("test",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:01:00.000Z"));

        List<String> violations = account.handleEvent(event);
        Assert.assertTrue(violations.contains(ViolationEnum.INSUFFICIENT_LIMIT.getDescription()));
    }

    @Test
    public void shouldReturnHighFrequencySmallIntervalViolationWhenTransactionViolatesThisBusinessRule(){

        Account account = new Account(true, BigDecimal.TEN);

        TransactionAuthorizationEvent event1 = new TransactionAuthorizationEvent("test1",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:01:00.000Z"));
        TransactionAuthorizationEvent event2 = new TransactionAuthorizationEvent("test2",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:00:00.000Z"));
        TransactionAuthorizationEvent event3 = new TransactionAuthorizationEvent("test3",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:02:00.000Z"));
        TransactionAuthorizationEvent event4 = new TransactionAuthorizationEvent("test3",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:02:01.000Z"));
        TransactionAuthorizationEvent event5 = new TransactionAuthorizationEvent("test4",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:03:00.000Z"));

        account.insertTransactionEventOrderedByTime(event1);
        account.insertTransactionEventOrderedByTime(event2);
        account.insertTransactionEventOrderedByTime(event3);
        account.insertTransactionEventOrderedByTime(event4);

        List<String> violations = account.handleEvent(event5);
        Assert.assertTrue(violations.contains(ViolationEnum.HIGH_FREQUENCY_SMALL_INTERVAL.getDescription()));
    }

    @Test
    public void shouldReturnDoubledTransactionViolationWhenTransactionViolatesThisBusinessRule(){

        Account account = new Account(true, BigDecimal.TEN);

        TransactionAuthorizationEvent event1 = new TransactionAuthorizationEvent("test1",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:01:00.000Z"));
        TransactionAuthorizationEvent event2 = new TransactionAuthorizationEvent("test2",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:00:00.000Z"));
        TransactionAuthorizationEvent event3 = new TransactionAuthorizationEvent("test3",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:02:00.000Z"));
        TransactionAuthorizationEvent event4 = new TransactionAuthorizationEvent("test3",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:02:01.000Z"));
        TransactionAuthorizationEvent event5 = new TransactionAuthorizationEvent("test3",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:03:00.000Z"));

        account.insertTransactionEventOrderedByTime(event1);
        account.insertTransactionEventOrderedByTime(event2);
        account.insertTransactionEventOrderedByTime(event3);
        account.insertTransactionEventOrderedByTime(event4);

        List<String> violations = account.handleEvent(event5);
        Assert.assertTrue(violations.contains(ViolationEnum.DOUBLED_TRANSACTION.getDescription()));
    }

    @Test
    public void shouldReduceAccountAvailableLimitIfValidTransactionEvent(){

        Account account = new Account(true, BigDecimal.TEN);

        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent("test1",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:01:00.000Z"));

        account.handleEvent(event);
        Assert.assertEquals(new BigDecimal(9), account.getAvailableLimit());
    }

    @Test
    public void shouldReturnEmptyViolationsListIfValidTransactionEvent(){

        Account account = new Account(true, BigDecimal.TEN);

        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent("test1",
                BigDecimal.ONE, Instant.parse("2019-02-13T10:01:00.000Z"));

        List<String> violations = account.handleEvent(event);
        Assert.assertTrue(violations.isEmpty());
    }

}