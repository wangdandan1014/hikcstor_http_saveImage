package com.hikcstor.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CommonProcess {
	
	public static String getGmtTime() {
		Calendar cd = Calendar.getInstance();  
		SimpleDateFormat sdf = new SimpleDateFormat("EEE,d MMM yyyy HH:mm:ss 'GMT'",Locale.ENGLISH);  
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	    String str = sdf.format(cd.getTime());  
		return str;
	}

}
