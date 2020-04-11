package com.challenge.authorizer.events;

import com.challenge.authorizer.models.OutputModel;

import java.io.Serializable;

public abstract class Event implements Serializable {

    public abstract OutputModel process();
}
