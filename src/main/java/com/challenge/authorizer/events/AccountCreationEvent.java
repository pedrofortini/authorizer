package com.challenge.authorizer.events;

import com.challenge.authorizer.domain.Account;
import com.challenge.authorizer.enums.ViolationEnum;
import com.challenge.authorizer.models.AccountModel;
import com.challenge.authorizer.models.InnerAccountModel;
import com.challenge.authorizer.models.OutputModel;
import com.challenge.authorizer.state.AccountDataStore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountCreationEvent extends Event {

    private final Boolean activeCard;
    private final BigDecimal availableLimit;

    public AccountCreationEvent(Boolean activeCard, BigDecimal availableLimit) {

        this.activeCard = activeCard;
        this.availableLimit = availableLimit;
    }

    @Override
    public OutputModel process() {

        Account account = AccountDataStore.getInstance().getAccount();
        List<String> violations = new ArrayList<>();

        if(account != null) {

            violations.add(ViolationEnum.ACCOUNT_ALREADY_INITIALIZED.getDescription());
        }
        else {

            account = new Account(this.activeCard, this.availableLimit);
            account.handleEvent(this);
        }

        InnerAccountModel model = new InnerAccountModel(account.getActiveCard(), account.getAvailableLimit());
        return new OutputModel(model, violations);
    }
}
