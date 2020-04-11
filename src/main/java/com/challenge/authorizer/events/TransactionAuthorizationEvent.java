package com.challenge.authorizer.events;

import com.challenge.authorizer.domain.Account;
import com.challenge.authorizer.enums.ViolationEnum;
import com.challenge.authorizer.models.AccountModel;
import com.challenge.authorizer.models.InnerAccountModel;
import com.challenge.authorizer.models.OutputModel;
import com.challenge.authorizer.models.TransactionModel;
import com.challenge.authorizer.state.AccountDataStore;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TransactionAuthorizationEvent extends Event {

    public final String merchant;
    public final BigDecimal amount;
    public final Instant time;

    public TransactionAuthorizationEvent(String merchant, BigDecimal amount, Instant time){

        this.merchant = merchant;
        this.amount = amount;
        this.time = time;
    }

    public String getMerchant() {
        return merchant;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getTime() {
        return time;
    }

    @Override
    public OutputModel process() {

        Account account = AccountDataStore.getInstance().getAccount();
        List<String> violations = new ArrayList<>();
        InnerAccountModel accountModel = null;

        if (account == null) {

            violations.add(ViolationEnum.ACCOUNT_NOT_INITIALIZED.getDescription());
        }
        else {

            violations.addAll(account.handleEvent(this));
            accountModel = new InnerAccountModel(account.getActiveCard(), account.getAvailableLimit());
        }

        return new OutputModel(accountModel, violations);
    }
}
