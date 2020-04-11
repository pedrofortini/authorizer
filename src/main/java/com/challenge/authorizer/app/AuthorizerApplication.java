package com.challenge.authorizer.app;

import com.challenge.authorizer.processor.EventProcessor;
import com.challenge.authorizer.state.AccountDataStore;
import com.challenge.authorizer.state.EventStore;

import java.io.InputStream;
import java.util.Scanner;

public class AuthorizerApplication {

	public static void main(String[] args) {

		EventProcessor processor = new EventProcessor();
		AccountDataStore.getInstance().resetState();
		EventStore.getInstance().emptyEventStore();

		InputStream source = System.in;
		Scanner in = new Scanner(source);
		while(in.hasNext()) {

			String input = in.nextLine();
			processor.process(input, false);
		}
	}
}
