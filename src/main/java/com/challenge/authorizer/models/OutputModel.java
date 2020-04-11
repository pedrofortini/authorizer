package com.challenge.authorizer.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class OutputModel implements Serializable {

    public InnerAccountModel account;

    @JsonProperty("violations")
    public List<String> violations;

    public OutputModel(InnerAccountModel account, List<String> violations){

        this.account = account;
        this.violations = violations;
    }
}
