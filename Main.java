/*

Copyright Jasper Verberk, 2001-2013, http://www.warfields.net/

This file is part of Warfields Bot

*/


import java.util.*;
import java.lang.*;


public class Main {

	protected MysqlControl mysqlControl;
	protected BotControl botControl;

	public static void main(String[] args) {
		
		//Start Main
		new Main();
		
	}

	public Main() {
		
		//Start MysqlControl
		mysqlControl = new MysqlControl("91.229.232.131", "warfields", "sdleifraw", "warbot");

		//Start botControl
		botControl = new BotControl(mysqlControl);
		botControl.connect();
        
	}
    
}
