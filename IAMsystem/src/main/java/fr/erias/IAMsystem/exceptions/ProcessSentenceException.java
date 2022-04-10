package fr.erias.IAMsystem.exceptions;

public class ProcessSentenceException extends IAMsystemExceptions {

	private static final long serialVersionUID = -1198776754946398049L;

	public ProcessSentenceException(String sentence) {
		super("Something went wrong when analysing sentence : " + sentence);
	}

}
