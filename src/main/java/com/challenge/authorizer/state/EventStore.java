package com.challenge.authorizer.state;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EventStore {

    private static EventStore instance;

    private Queue events;

    private EventStore(){
        this.events = new LinkedList();
    }

    public void write(String event){

        if(event != null){

            this.events.add(event);
        }
    }

    public static synchronized EventStore getInstance() {

        if(instance == null){
            instance = new EventStore();
        }
        return instance;
    }

    public String nextEvent(){

        if(!events.isEmpty()) {
            return (String) events.remove();
        }
        return null;
    }

    public void emptyEventStore(){
        this.events = new LinkedList();
    }
}
