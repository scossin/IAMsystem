package fr.erias.IAMsystem.exceptions;

public class UnfoundTokenInSentence extends IAMsystemExceptions{

	private static final long serialVersionUID = -5750726384502837323L;

	public UnfoundTokenInSentence(String token, String sentence) {
		super("Unfound token : " + token + "in sentence : \n" + sentence);
	}

}
