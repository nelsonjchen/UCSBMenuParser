package com.mindflakes.TeamRED.UCSBScrape;

import static org.junit.Assert.*;

import org.junit.Test;


public class MenuScraperTests {

	@Test
	public void checkCARRILLO_THIS_WEEK(){
		try{
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.CARRILLO_THIS_WEEK));
			assertTrue(true);
		}catch(Exception e){
			assertTrue(false);
		}
	}	
	@Test
	public void checkCARRILLO_NEXT_WEEK(){
		try{
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.CARRILLO_NEXT_WEEK));
			assertTrue(true);
		}catch(Exception e){
			assertTrue(false);
		}
	}
	@Test
	public void checkDLG_THIS_WEEK(){
		try{
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.DLG_THIS_WEEK));
			assertTrue(true);
		}catch(Exception e){
			assertTrue(false);
		}
	}
	@Test
	public void checkDLG_NEXT_WEEK(){
		try{
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.DLG_NEXT_WEEK));
			assertTrue(true);
		}catch(Exception e){
			assertTrue(false);
		}
	}
	@Test
	public void checkORTEGA_THIS_WEEK(){
		try{
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.ORTEGA_THIS_WEEK));
			assertTrue(true);
		}catch(Exception e){
			assertTrue(false);
		}
	}
	@Test
	public void checkORTEGA_NEXT_WEEK(){
		try{
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.ORTEGA_NEXT_WEEK));
			assertTrue(true);
		}catch(Exception e){
			assertTrue(false);
		}
	}
	@Test
	public void checkPORTOLA_THIS_WEEK(){
		try{
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.PORTOLA_THIS_WEEK));
			assertTrue(true);
		}catch(Exception e){
			assertTrue(false);
		}
	}
	@Test
	public void checkPORTOLA_NEXT_WEEK(){
		try{
			new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.PORTOLA_NEXT_WEEK));
			assertTrue(true);
		}catch(Exception e){
			assertTrue(false);
		}
	}
	
}
