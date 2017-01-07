package com.society.classes;

public class Utilities 
{

	public static boolean validateName( String name )
	{
	      return name.matches( "[A-Z][a-zA-Z]*" );
	}
	
	public static boolean validateMobile(String mobnumber)
	{
		String phoneNumberPattern = "(\\d-)?(\\d{3}-)?\\d{3}-\\d{4}";
		return mobnumber.matches(phoneNumberPattern);
	}
	
}
