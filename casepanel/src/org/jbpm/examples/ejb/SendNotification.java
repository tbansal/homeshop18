package org.jbpm.examples.ejb;

public class SendNotification {

	
	public static SendNotification getInstance(){
		return new SendNotification();
	}
	public static void printMessage() {
		System.out.println("called me");
	}

}
