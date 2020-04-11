package com.challenge.authorizer.state;

import com.challenge.authorizer.domain.Account;

public class AccountDataStore {

    private static AccountDataStore instance;

    private Account account;

    private AccountDataStore(){

        this.account = null;
    }

    public static synchronized AccountDataStore getInstance() {
        
        if(instance == null){
            instance = new AccountDataStore();
        }
        return instance;
    }

    public Account getAccount() {

        if(this.account != null) {
            return this.account.copyAccount();
        }
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void resetState() {

        setAccount(null);
    }
}
