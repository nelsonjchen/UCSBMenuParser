package com.mindflakes.TeamRED.UCSBScrape;
import java.util.Scanner;

/** An object that reads an XML file representing a UCSB Dining Commons Menu line by line. Uses a <code>Scanner</code> object to retrieve data line by line.
 * @author Johan Henkens
 * @see Scanner
 */
public abstract class UCSBMenuFile {
	protected Scanner sc;
	/**
	 * Advances the menu file one line further and returns the line that was just passed over. If the file end had been reached, "EOF" will be returned.
	 * @return the line that was just passed over if the end of the file had not been reached; otherwise "EOF".
	 */
	protected String nextLine(){
		if (sc.hasNextLine()) {
                    return sc.nextLine();
                } else {
                    return "EOF";
                }
	}

}
