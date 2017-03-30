/*

Copyright Jasper Verberk, 2001-2013, http://www.warfields.net/

This file is part of Warfields Bot

*/


import java.util.*;
import java.io.*;


public class ExceptionHandler {

    public ExceptionHandler(Exception exception) {
		try {
	    	PrintWriter printWriter = new PrintWriter(new FileWriter("Error/ErrorLog.txt", true));
		    Calendar calendar = new GregorianCalendar();
		    Date date = calendar.getTime();
		    StackTraceElement stackTraceElement[] = exception.getStackTrace();
			System.out.println(date.toString() + " Error found in " + stackTraceElement[0].getFileName() + " " + stackTraceElement[0].getMethodName()  + " " + exception.getMessage());
		    printWriter.println(date.toString() + " Error found in " + stackTraceElement[0].getFileName() + " " + stackTraceElement[0].getMethodName()  + " " + exception.getMessage());
		    printWriter.flush();
		    printWriter.close();
		}
		catch(Exception e) {
	    	System.out.println(e.getMessage());
		}
    }
    
    public ExceptionHandler(String exception) {
		try{
		    PrintWriter printWriter = new PrintWriter(new FileWriter("Error/ErrorLog.txt", true));
		    Calendar calendar = new GregorianCalendar();
		    Date date = calendar.getTime();
		    System.out.println(date.toString() + " Message: " + exception);
		    printWriter.println(date.toString() + " Message: " + exception);
		    printWriter.flush();
		    printWriter.close();
		}
		catch(Exception e) {
		    System.out.println(e.getMessage());
		}
    }

}