package fr.erias.IAMsystem.exceptions;

import org.slf4j.Logger;

public class UnfoundTokenInSentence extends MyExceptions{

	public UnfoundTokenInSentence(Logger logger, String token, String sentence) {
		super(logger,"Unfound token : " + token + "in sentence : \n" + sentence);
	}

}
