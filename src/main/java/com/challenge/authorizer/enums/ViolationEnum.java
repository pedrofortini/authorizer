package com.challenge.authorizer.enums;

public enum ViolationEnum {

    DOUBLED_TRANSACTION("doubled-transaction"),
    HIGH_FREQUENCY_SMALL_INTERVAL("high-frequency-small-interval"),
    INSUFFICIENT_LIMIT("insufficient-limit"),
    CARD_NOT_ACTIVE("card-not-active"),
    ACCOUNT_NOT_INITIALIZED("account-not-initialized"),
    ACCOUNT_ALREADY_INITIALIZED("account-already-initialized");

    private String description;

    ViolationEnum(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
