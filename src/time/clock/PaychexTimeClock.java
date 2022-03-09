package time.clock;

import time.clock.data.TimeClock;

public class PaychexTimeClock {

	/**
	 * Paychex Time Clock application main program.
	 * 
	 * @param args program args not used.
	 */
	
	public static void main(String[] args) {
		//Create an instance of the TimeClock system and start the login CMI
		TimeClock system = new TimeClock();
		system.login();
	}
}//PaychexTimeClock
