package com.challenge.authorizer.processor;

import com.challenge.authorizer.events.AccountCreationEvent;
import com.challenge.authorizer.events.Event;
import com.challenge.authorizer.events.TransactionAuthorizationEvent;
import com.challenge.authorizer.models.AccountModel;
import com.challenge.authorizer.models.OutputModel;
import com.challenge.authorizer.models.TransactionModel;
import com.challenge.authorizer.state.EventStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.IOException;

public class EventProcessor {

    private ObjectMapper mapper;

    public EventProcessor(){

        this.mapper = new ObjectMapper();
    }

    public Event parseEventString(String eventString){

        try {

            JSONObject jsonFromString = new JSONObject(eventString);

            // Assuming that input parsing errors will not happen, one of the nodes will be present
            if(jsonFromString.has("account")) {

                AccountModel accountModel = mapper.readValue(eventString, AccountModel.class);
                return new AccountCreationEvent(accountModel.account.activeCard, accountModel.account.availableLimit);
            }
            else if(jsonFromString.has("transaction")) {

                TransactionModel transactionModel = mapper.readValue(eventString, TransactionModel.class);
                return new TransactionAuthorizationEvent(transactionModel.transaction.merchant,
                        transactionModel.transaction.amount, transactionModel.transaction.time);
            }
        }
        catch (IOException e){

            System.err.println("Error while trying to parse JSON string from stream. Error: " + e);
        }
        return null;
    }

    public void process(String eventString, boolean reprocessing) {

        // Persist event on Event Store for future reprocessing
        if(!reprocessing) {
            EventStore.getInstance().write(eventString);
        }
        Event event = parseEventString(eventString);

        if(event != null) {

            OutputModel output = event.process();

            try {

                System.out.println(mapper.writeValueAsString(output));
            } catch (JsonProcessingException e) {

                System.err.println("Error while trying to write JSON output string. Error: " + e);
            }
        }
    }

    public void reprocess(){

        String eventString = EventStore.getInstance().nextEvent();

        while (eventString != null){

            this.process(eventString, true);
            eventString = EventStore.getInstance().nextEvent();
        }
    }
}
