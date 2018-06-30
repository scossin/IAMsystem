package fr.erias.IAMsystem.exceptions;

import org.slf4j.Logger;

public class ProcessSentenceException extends MyExceptions {

	public ProcessSentenceException(Logger logger, String sentence) {
		super(logger,"Something went wrong when analysing sentence : " + sentence);
	}

}
