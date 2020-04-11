package com.challenge.authorizer.events;


import com.challenge.authorizer.domain.Account;
import com.challenge.authorizer.enums.ViolationEnum;
import com.challenge.authorizer.models.OutputModel;
import com.challenge.authorizer.state.AccountDataStore;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class AccountCreationEventTest {

    @Test
    public void shouldReturnOutputModelWithAccountAlreadyInitialedViolationIfAccountAlreadyExists() {

        Account account = new Account(true, BigDecimal.TEN);
        AccountDataStore.getInstance().setAccount(account);

        AccountCreationEvent event = new AccountCreationEvent(true, new BigDecimal(20));

        OutputModel output = event.process();
        Assert.assertTrue(output.violations.contains(ViolationEnum.ACCOUNT_ALREADY_INITIALIZED.getDescription()));
    }

    @Test
    public void shouldReturnOutputModelWitNoViolationsIfAccountDoesntExists() {

        AccountDataStore.getInstance().setAccount(null);

        AccountCreationEvent event = new AccountCreationEvent(true, BigDecimal.TEN);

        OutputModel output = event.process();
        Assert.assertTrue(output.violations.isEmpty());
    }

    @Test
    public void shouldReturnOutputModelWitActiveCardTrueIfAccountDoesntExists() {

        AccountDataStore.getInstance().setAccount(null);

        AccountCreationEvent event = new AccountCreationEvent(true, BigDecimal.TEN);

        OutputModel output = event.process();
        Assert.assertEquals(true, output.account.activeCard);
    }

    @Test
    public void shouldReturnOutputModelWitCorrectAvailableLimitIfAccountDoesntExists() {

        AccountDataStore.getInstance().setAccount(null);

        AccountCreationEvent event = new AccountCreationEvent(true, BigDecimal.TEN);

        OutputModel output = event.process();
        Assert.assertEquals(BigDecimal.TEN, output.account.availableLimit);
    }
}