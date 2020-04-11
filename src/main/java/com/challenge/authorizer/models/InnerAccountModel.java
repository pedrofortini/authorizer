package com.challenge.authorizer.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InnerAccountModel implements Serializable {

    @JsonProperty("active-card")
    public Boolean activeCard;

    @JsonProperty("available-limit")
    public BigDecimal availableLimit;

    public InnerAccountModel(){}

    public InnerAccountModel(Boolean activeCard, BigDecimal availableLimit){

        this.activeCard = activeCard;
        this.availableLimit = availableLimit;
    }
}
