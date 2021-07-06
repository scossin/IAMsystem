package fr.erias.IAMsystem.utils;

public class FilterNchars implements IFilterToken{

	
	private int nchar;
	
	public FilterNchars(int nchar) {
		this.nchar = nchar;
	}
	
	@Override
	public boolean isAtokenToIgnore(String token) {
		return(token.length() < nchar);
	}
}
