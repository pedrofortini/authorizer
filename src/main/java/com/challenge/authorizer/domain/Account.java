package com.challenge.authorizer.domain;

import com.challenge.authorizer.enums.ViolationEnum;
import com.challenge.authorizer.events.AccountCreationEvent;
import com.challenge.authorizer.events.TransactionAuthorizationEvent;
import com.challenge.authorizer.state.AccountDataStore;

import java.math.BigDecimal;
import java.util.*;

public class Account {

    private final Boolean activeCard;
    private BigDecimal availableLimit;

    /* Keep Transactions ordered by time */
    private TreeSet<Transaction> transactions;

    public Account(Boolean activeCard, BigDecimal availableLimit) {

        this.activeCard = activeCard;
        this.availableLimit = availableLimit;
        this.transactions = new TreeSet<>();
    }

    public Boolean getActiveCard() {
        return activeCard;
    }

    public BigDecimal getAvailableLimit() {
        return availableLimit;
    }

    public TreeSet<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return activeCard.equals(account.activeCard) &&
                availableLimit.equals(account.availableLimit);
    }

    /* Ensures Immutability of Account objects */
    public Account copyAccount() {

        Account copy = new Account(this.activeCard, this.availableLimit);
        copy.transactions = this.transactions;
        return copy;
    }

    /**** EVENT HANDLING ****/

    public void handleEvent(AccountCreationEvent accountCreationEvent) {

        AccountDataStore.getInstance().setAccount(this);
    }

    public List<String> handleEvent(TransactionAuthorizationEvent transactionAuthorizationEvent){

        Transaction insertedTransaction = this.insertTransactionEventOrderedByTime(transactionAuthorizationEvent);

        List<Transaction> twoMinuteWindow = this.get2MinuteInterval(insertedTransaction);

        List<String> violations = new ArrayList<>();

        boolean cardNotActive = checkCardNotActiveViolation();
        if(cardNotActive) violations.add(ViolationEnum.CARD_NOT_ACTIVE.getDescription());

        boolean checkInsufficientLimit = checkInsufficientLimitViolation(transactionAuthorizationEvent);
        if(checkInsufficientLimit) violations.add(ViolationEnum.INSUFFICIENT_LIMIT.getDescription());

        boolean checkHighFrequencySmallInterval = checkHighFrequencySmallIntervalViolation(twoMinuteWindow);
        if(checkHighFrequencySmallInterval) violations.add(ViolationEnum.HIGH_FREQUENCY_SMALL_INTERVAL.getDescription());

        boolean checkDoubledTransaction = checkDoubledTransaction(twoMinuteWindow, insertedTransaction);
        if(checkDoubledTransaction) violations.add(ViolationEnum.DOUBLED_TRANSACTION.getDescription());

        /* Only persist data if no violation occurs */
        if(violations.isEmpty()){

            reduceAvailableLimit(transactionAuthorizationEvent.getAmount());
            AccountDataStore.getInstance().setAccount(this);
        }

        return violations;
    }

    /**** BUSINESS RULES VALIDATIONS ****/

    public boolean checkCardNotActiveViolation() {
        return !activeCard;
    }

    public boolean checkInsufficientLimitViolation(TransactionAuthorizationEvent event) {

        if(event != null && event.getAmount() != null) {

            BigDecimal amount = event.getAmount();
            if (amount.compareTo(BigDecimal.ZERO) > 0
                    || amount.compareTo(BigDecimal.ZERO) == 0) {

                return this.availableLimit.compareTo(event.getAmount()) < 0;
            }
        }
        return false;
    }

    public boolean checkHighFrequencySmallIntervalViolation(List<Transaction> twoMinuteWindow){

        /* There shouldn't be no more than 3 transactions on a 2 minute interval */
        if(twoMinuteWindow != null) {

            return twoMinuteWindow.size() > 3;
        }
        return false;
    }

    public boolean checkDoubledTransaction(List<Transaction> twoMinuteWindow, Transaction inserted){


        if(twoMinuteWindow != null && inserted != null) {

            /* Remove the inserted Transaction before comparision */
            twoMinuteWindow.remove(inserted);
            return twoMinuteWindow.contains(inserted);
        }
        return false;
    }

    /**** INTERNAL STRUCTURES HANDLING ****/

    public Transaction insertTransactionEventOrderedByTime(TransactionAuthorizationEvent event){

        Transaction transaction = new Transaction(event.getMerchant(), event.getAmount(), event.getTime());
        this.transactions.add(transaction);
        return transaction;
    }

    public void reduceAvailableLimit(BigDecimal money) {

        if (this.availableLimit != null && money != null) {

            this.availableLimit = this.availableLimit.subtract(money);
        }
    }

    public List<Transaction> get2MinuteInterval(Transaction lastInsertedTransaction){

        List<Transaction> twoMinuteWindow = new ArrayList<>();

        if(lastInsertedTransaction != null) {

            /* Get all the transactions that happened before the one just inserted */
            TreeSet<Transaction> transactionsBeforeLastInserted = (TreeSet<Transaction>)
                    this.transactions.headSet(lastInsertedTransaction, true);

            /* Iterates on the transactions in descending order (from the most recent) */
            Iterator iterator = transactionsBeforeLastInserted.descendingIterator();

            /* Get the transactions that happened 2 minutes before the one just inserted */
            double minutesCounter = 0;
            Transaction currentTransaction = (Transaction) iterator.next();
            twoMinuteWindow.add(currentTransaction);
            while (iterator.hasNext()) {

                Transaction pastTransaction = (Transaction) iterator.next();
                minutesCounter += currentTransaction.minutesBetweenTransactions(pastTransaction);
                if (minutesCounter > 2) {
                    return twoMinuteWindow;
                }
                twoMinuteWindow.add(pastTransaction);
                currentTransaction = pastTransaction;
            }

            /* Return empty window if after iterating throw all the past transactions, still don't have 2 minutes interval */
            if (minutesCounter < 2) {
                twoMinuteWindow = new ArrayList<>();
            }
        }
        return twoMinuteWindow;
    }
}
