package com.mindflakes.TeamRED.UCSBScrape;

import static org.junit.Assert.*;

import org.junit.Test;


public class MenuScraperTests {

	@Test
	public void makeSureNoExceptions(){
		try{
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.CARRILLO_THIS_WEEK));
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.CARRILLO_NEXT_WEEK));
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.DLG_THIS_WEEK));
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.DLG_NEXT_WEEK));
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.ORTEGA_THIS_WEEK));
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.ORTEGA_NEXT_WEEK));
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.PORTOLA_THIS_WEEK));
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.PORTOLA_NEXT_WEEK));
			assertTrue(true);
		}catch(Exception e){
			assertTrue(false);
		}
	}
	
}
