package com.challenge.authorizer.events;

import com.challenge.authorizer.domain.Account;
import com.challenge.authorizer.enums.ViolationEnum;
import com.challenge.authorizer.models.OutputModel;
import com.challenge.authorizer.state.AccountDataStore;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionAuthorizationEventTest {

    @Test
    public void shouldReturnOutputModelWitAccountNotInitializedViolationIfAccountDoesntExists() {

        AccountDataStore.getInstance().setAccount(null);

        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent(
                "teste", BigDecimal.ONE, Instant.now());

        OutputModel output = event.process();
        Assert.assertTrue(output.violations.contains(ViolationEnum.ACCOUNT_NOT_INITIALIZED.getDescription()));
    }

    @Test
    public void shouldReturnOutputModelWitNoViolationsIfAccountDoesExistsAndTransactionEventOK() {

        Account account = new Account(true, BigDecimal.TEN);
        AccountDataStore.getInstance().setAccount(account);

        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent(
                "teste", BigDecimal.ONE, Instant.now());

        OutputModel output = event.process();
        Assert.assertTrue(output.violations.isEmpty());
    }

    @Test
    public void shouldReturnOutputModelWitActiveCardTrueIfAccountDoesExistsAndTransactionEventOK() {


        Account account = new Account(true, BigDecimal.TEN);
        AccountDataStore.getInstance().setAccount(account);

        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent(
                "teste", BigDecimal.ONE, Instant.now());

        OutputModel output = event.process();
        Assert.assertEquals(true, output.account.activeCard);
    }

    @Test
    public void shouldReturnOutputModelWitCorrectAvailableLimitIfAccountDoesExistsAndTransactionEventOK() {

        Account account = new Account(true, BigDecimal.TEN);
        AccountDataStore.getInstance().setAccount(account);

        TransactionAuthorizationEvent event = new TransactionAuthorizationEvent(
                "teste", BigDecimal.ONE, Instant.now());

        OutputModel output = event.process();
        Assert.assertEquals(new BigDecimal(9), output.account.availableLimit);
    }

}