package com.challenge.authorizer.app;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AuthorizerApplicationTest {

    @Test
    public void testingMainMethodAndAssuringCorrectIntegrationBetweenOthers() throws IOException {


        System.setIn(new ByteArrayInputStream(getTestOperationsBytes()));
        String outputEventAccountNotInitialized = "{\"account\":null,\"violations\":[\"account-not-initialized\"]}";
        String outputEventAccountCreated = "{\"account\":{\"active-card\":true,\"available-limit\":1000},\"violations\":[]}";
        String outputEventTransactionOK1 = "{\"account\":{\"active-card\":true,\"available-limit\":999},\"violations\":[]}";
        String outputEventAccountAlreadyInitialized = "{\"account\":{\"active-card\":true,\"available-limit\":999},\"violations\":[\"account-already-initialized\"]}";
        String outputEventTransactionOK2 = "{\"account\":{\"active-card\":true,\"available-limit\":998},\"violations\":[]}";
        String outputEventTransactionOK3 = "{\"account\":{\"active-card\":true,\"available-limit\":996},\"violations\":[]}";
        String outputEventDoubledTransaction = "{\"account\":{\"active-card\":true,\"available-limit\":996},\"violations\":[\"doubled-transaction\"]}";
        String outputEventHighSmallInterval = "{\"account\":{\"active-card\":true,\"available-limit\":996},\"violations\":[\"high-frequency-small-interval\",\"doubled-transaction\"]}";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);

        String[] args = null;
        AuthorizerApplication.main(args);

        String outputByteStream = baos.toString();
        Assert.assertTrue(outputByteStream.contains(outputEventAccountNotInitialized));
        Assert.assertTrue(outputByteStream.contains(outputEventAccountCreated));
        Assert.assertTrue(outputByteStream.contains(outputEventTransactionOK1));
        Assert.assertTrue(outputByteStream.contains(outputEventAccountAlreadyInitialized));
        Assert.assertTrue(outputByteStream.contains(outputEventTransactionOK2));
        Assert.assertTrue(outputByteStream.contains(outputEventTransactionOK3));
        Assert.assertTrue(outputByteStream.contains(outputEventDoubledTransaction));
        Assert.assertTrue(outputByteStream.contains(outputEventHighSmallInterval));
    }

    private byte[] getTestOperationsBytes() throws IOException {

        List<String> operations = new ArrayList<>();
        operations.add("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 1, \"time\": \"2019-02-13T10:00:00.000Z\"}}\n");
        operations.add("{\"account\": {\"active-card\": true, \"available-limit\": 1000}}\n");
        operations.add("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 1, \"time\": \"2019-02-13T10:00:00.000Z\"}}\n");
        operations.add("{\"account\": {\"active-card\": true, \"available-limit\": 1000}}\n");
        operations.add("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 1, \"time\": \"2019-02-13T10:01:00.000Z\"}}\n");
        operations.add("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 2, \"time\": \"2019-02-13T10:01:05.000Z\"}}\n");
        operations.add("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 2, \"time\": \"2019-02-13T10:02:05.000Z\"}}\n");
        operations.add("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 2, \"time\": \"2019-02-13T10:02:06.000Z\"}}\n");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        for (String element : operations) {
            out.writeBytes(element);
        }
        return baos.toByteArray();
    }
}