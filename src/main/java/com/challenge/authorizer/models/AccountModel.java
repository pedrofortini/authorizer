package com.challenge.authorizer.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountModel implements Serializable {

    @JsonProperty("account")
    public InnerAccountModel account;

    public AccountModel(){}
}
