package fr.erias.IAMsystem.exceptions;

import org.slf4j.Logger;

public class InvalidArraysLength extends IAMsystemExceptions{

	private static final long serialVersionUID = -6197517989489572382L;

	public InvalidArraysLength(Logger logger, int array1Length, int array2Length) {
		super("Arrays are expected to be the same size : \n " + 
				" first array : " + array1Length + " \n " + 
				"second array : " + array2Length);
	}

}
