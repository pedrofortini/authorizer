package com.challenge.authorizer.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionModel implements Serializable {

    @JsonProperty("transaction")
    public InnerTransaction transaction;

    public TransactionModel(){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class InnerTransaction implements Serializable {

        public String merchant;

        public BigDecimal amount;

        public Instant time;

        public InnerTransaction(){}

        @JsonSetter
        public void setTime(String time) {
            this.time = Instant.parse(time);
        }
    }
}
