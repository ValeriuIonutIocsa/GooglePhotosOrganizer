package com.utils.log;

import java.util.function.BiConsumer;

public class MessageConsumer {

	public static final BiConsumer<MessageLevel, String> DEFAULT_MESSAGE_CONSUMER = (
			messageLevel,
			message) -> {

		if (messageLevel == MessageLevel.INFO ||
				messageLevel == MessageLevel.PROGRESS ||
				messageLevel == MessageLevel.STATUS) {
			System.out.println(message);

		} else if (messageLevel == MessageLevel.WARNING ||
				messageLevel == MessageLevel.ERROR ||
				messageLevel == MessageLevel.EXCEPTION) {
			System.err.println(message);
		}
	};

	private BiConsumer<MessageLevel, String> messageConsumer = DEFAULT_MESSAGE_CONSUMER;

	void printMessage(
			final MessageLevel messageLevel,
			final String messageParam) {

		final boolean debugMode = Logger.isDebugMode();
		if (debugMode || messageLevel != MessageLevel.EXCEPTION) {

			String message = messageParam;
			if (message == null) {
				message = "NULL";
			}
			messageConsumer.accept(messageLevel, message);
		}
	}

	void setMessageConsumer(
			final BiConsumer<MessageLevel, String> messageConsumer) {
		this.messageConsumer = messageConsumer;
	}

	public BiConsumer<MessageLevel, String> getMessageConsumer() {
		return messageConsumer;
	}
}
