package com.challenge.authorizer.processor;

import com.challenge.authorizer.domain.Account;
import com.challenge.authorizer.domain.Transaction;
import com.challenge.authorizer.events.AccountCreationEvent;
import com.challenge.authorizer.events.TransactionAuthorizationEvent;
import com.challenge.authorizer.models.AccountModel;
import com.challenge.authorizer.models.OutputModel;
import com.challenge.authorizer.state.AccountDataStore;
import com.challenge.authorizer.state.EventStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class EventProcessorTest {

    private EventProcessor eventProcessor;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {

        AccountDataStore.getInstance().resetState();
        EventStore.getInstance().emptyEventStore();
        this.eventProcessor = new EventProcessor();
        this.mapper = PowerMockito.mock(ObjectMapper.class);
    }

    @Test
    public void shouldReturnAccountCreationEventIfEventStringContainsAccountObject() {

        String eventString = "{\"account\": {\"active-card\": true, \"available-limit\": 100}}";
        Assert.assertThat(this.eventProcessor.parseEventString(eventString), instanceOf(AccountCreationEvent.class));
    }

    @Test
    public void shouldReturnTransactionAuthorizationEventIfEventStringContainsTransactionObject() {

        String eventString = "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}";
        Assert.assertThat(this.eventProcessor.parseEventString(eventString), instanceOf(TransactionAuthorizationEvent.class));
    }

    @Test
    public void shouldReturnNullIfEventStringCantBeParsedToAccountNorTransactionEvent() {

        String eventString = "{}";
        Assert.assertNull(this.eventProcessor.parseEventString(eventString));
    }

    @Test
    public void shouldWriteMessageOnConsoleIfExceptionOccursWhenParsingEventString() throws IOException {

        String eventString = "{\"account\": {\"active-card\": true, \"available-limit\": 100}}";

        PowerMockito.doThrow(new IOException()).when(this.mapper).readValue(eventString, AccountModel.class);
        Whitebox.setInternalState(eventProcessor, "mapper", mapper);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setErr(ps);

        this.eventProcessor.parseEventString(eventString);

        String exceptionMessage = "Error while trying to parse JSON string from stream. Error: java.io.IOException\n";
        Assert.assertEquals(exceptionMessage, baos.toString());
    }

    @Test
    public void shouldNotWriteOnConsoleIfProcessingInvalidEvent() {

        String eventString = "{}";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);

        this.eventProcessor.process(eventString, false);

        String output = "";
        Assert.assertEquals(output, baos.toString());
    }

    @Test
    public void shouldWriteCorrectOutputStringOnConsoleIfProcessingEventWithNoViolations() {

        String eventString = "{\"account\": {\"active-card\": true, \"available-limit\": 100}}";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);

        this.eventProcessor.process(eventString, false);

        String output = "{\"account\":{\"active-card\":true,\"available-limit\":100},\"violations\":[]}\n";
        Assert.assertEquals(output, baos.toString());
    }

    @Test
    public void shouldWriteMessageOnConsoleIfExceptionOccursWhenConvertingOutputEventToString() throws IOException {

        String eventString = "{\"account\": {\"active-card\": true, \"available-limit\": 100}}";

        this.mapper = Mockito.spy(new ObjectMapper());
        Mockito.when( this.mapper.writeValueAsString(Mockito.any())).thenThrow(new JsonProcessingException("") {});
        Whitebox.setInternalState(eventProcessor, "mapper", mapper);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setErr(ps);

        this.eventProcessor.process(eventString, false);

        String exceptionMessage = "Error while trying to write JSON output string.";
        Assert.assertTrue(baos.toString().contains(exceptionMessage));
    }

    /* Checks that if in case of failure,
     * reprocessing of events on EventStore retores Account to correct state
     */
    @Test
    public void testingEventSourcingReprocessingMechanism(){


        String eventCreateAccount = "{\"account\": {\"active-card\": true, \"available-limit\": 100}}";
        String eventTransactionOK = "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}";
        String eventTransactionViolation = "{\"transaction\": {\"merchant\": \"Habbib's\", \"amount\": 90, \"time\": \"2019-02-13T11:00:00.000Z\"}}";

        this.eventProcessor.process(eventCreateAccount, false);
        this.eventProcessor.process(eventTransactionOK, false);
        this.eventProcessor.process(eventTransactionViolation, false);

        Account accountBeforeReset = AccountDataStore.getInstance().getAccount();

        AccountDataStore.getInstance().resetState();
        this.eventProcessor.reprocess();

        Account reprocessedAccount = AccountDataStore.getInstance().getAccount();

        assertEquals(accountBeforeReset.getActiveCard(), reprocessedAccount.getActiveCard());
        assertEquals(accountBeforeReset.getAvailableLimit(), reprocessedAccount.getAvailableLimit());
        assertEquals(accountBeforeReset.getTransactions(), reprocessedAccount.getTransactions());
    }

}