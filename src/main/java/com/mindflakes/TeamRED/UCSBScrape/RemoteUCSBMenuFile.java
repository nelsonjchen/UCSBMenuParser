package com.mindflakes.TeamRED.UCSBScrape;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;

/** Remote file implementation of the UCSBMenuFile abstract class. Used when the XML file is stored on a website
 * @author Johan Henkens
 *
 */
public class RemoteUCSBMenuFile extends UCSBMenuFile {
	public static final String CARRILLO_THIS_WEEK = "https://docs.google.com/viewer?url=http://www.housing.ucsb.edu/dining/menu/carrillo/ThisWeekMenu.pdf&a=gt";
	public static final String CARRILLO_NEXT_WEEK = "https://docs.google.com/viewer?url=http://www.housing.ucsb.edu/dining/menu/carrillo/NextWeekMenu.pdf&a=gt";
	public static final String DLG_THIS_WEEK = "https://docs.google.com/viewer?url=http://www.housing.ucsb.edu/dining/menu/dlg/ThisWeekMenu.pdf&a=gt";
	public static final String DLG_NEXT_WEEK = "https://docs.google.com/viewer?url=http://www.housing.ucsb.edu/dining/menu/dlg/NextWeekMenu.pdf&a=gt";
	public static final String PORTOLA_THIS_WEEK = "https://docs.google.com/viewer?url=http://www.housing.ucsb.edu/dining/menu/portola/ThisWeekMenu.pdf&a=gt";
	public static final String PORTOLA_NEXT_WEEK = "https://docs.google.com/viewer?url=http://www.housing.ucsb.edu/dining/menu/portola/NextWeekMenu.pdf&a=gt";
	public static final String ORTEGA_THIS_WEEK = "https://docs.google.com/viewer?url=http://www.housing.ucsb.edu/dining/menu/ortega/ThisWeekMenu.pdf&a=gt";
	public static final String ORTEGA_NEXT_WEEK = "https://docs.google.com/viewer?url=http://www.housing.ucsb.edu/dining/menu/ortega/NextWeekMenu.pdf&a=gt";


	/** Constructs a <code>RemoteUCSBMenuFile</code> with the specified URL as the location to
	 * the XML file representing a menu. The URL must be absolute.
	 * @param URLPath Absolute URL to the XML file hosted on a website
	 */
	public RemoteUCSBMenuFile(String URLPath){
		try{
			sc = new Scanner(new InputStreamReader((new URL(URLPath).openStream())));
		}catch(IOException e){
			e.printStackTrace();

		}
	}

	public RemoteUCSBMenuFile(URL url){
		try{
			sc = new Scanner(new InputStreamReader(url.openStream()));
		}catch(IOException e){
			e.printStackTrace();

		}
	}

}