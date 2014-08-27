package com.locate.rmds.statistic;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class to format strings such as date, time etc.
 */
public class OutputFormatter
{
	Date currentDateTime;
	
	StringBuffer stringBuffer; 
 
	FieldPosition fieldPosition;
	SimpleDateFormat dateFormat;
	
    /* constructor, initialize */
	
	public OutputFormatter()
	{
	}

	public void initializeDateFormat( String dateFormatStr, String timezoneStr )
	{
		stringBuffer = new StringBuffer(); 
		currentDateTime = new Date(); 
		fieldPosition = new FieldPosition(0);
		
		dateFormat = new SimpleDateFormat( dateFormatStr );
		dateFormat.setTimeZone(TimeZone.getTimeZone( timezoneStr ));
	}

    /* utilities */
	
	public String getDateTimeAsString()
	{
		currentDateTime.setTime(System.currentTimeMillis());     
		dateFormat.format(currentDateTime, stringBuffer, fieldPosition);     

		String dateTimeString = stringBuffer.toString();
		stringBuffer.delete(0, stringBuffer.length());
		
		return dateTimeString;
	}
 }
