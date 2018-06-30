package fr.erias.IAMsystem.exceptions;

import org.slf4j.Logger;

public class InvalidSentenceLength extends MyExceptions {
	public InvalidSentenceLength(Logger logger, String msg) {
		super(logger,msg);
	}
}
