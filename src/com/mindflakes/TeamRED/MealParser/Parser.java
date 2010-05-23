package com.mindflakes.TeamRED.MealParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import com.mindflakes.TeamRED.MenuXML.Writer;
import com.mindflakes.TeamRED.UCSBScrape.RemoteUCSBMenuFile;
import com.mindflakes.TeamRED.UCSBScrape.UCSBJMenuScraper;

public class Parser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File config = new File("config.cfg");
		String  savePath = "";
		try {
			Scanner sc = new Scanner(config);
			while(sc.hasNext()){
				savePath=sc.nextLine();
				if(savePath.startsWith("savePath=")){
					savePath=savePath.substring(savePath.indexOf("=")+1);
					break;
				}
			}
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
		File path = new File(savePath);
		if(!path.exists()){
			if(!path.mkdirs()) System.exit(1);
		}
		parseAllMenus(path);

	}
	
	private static void parseAllMenus(File path){
		parseMenu(path,"CarrilloThisWeek");
		parseMenu(path,"CarrilloNextWeek");
		parseMenu(path,"DLGThisWeek");
		parseMenu(path,"DLGNextWeek");
		parseMenu(path,"PortolaThisWeek");
		parseMenu(path,"PortolaNextWeek");
		parseMenu(path,"OrtegaThisWeek");
		parseMenu(path,"OrtegaNextWeek");
	}
	
	private static void parseMenu(File path,String menu){
		UCSBJMenuScraper scrape = null;
		if(menu.equals("CarrilloThisWeek")){
			scrape = new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.CARRILLO_THIS_WEEK));
		} else if(menu.equals("CarrilloNextWeek")){
			scrape = new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.CARRILLO_NEXT_WEEK));
		} else if(menu.equals("DLGThisWeek")){
			scrape = new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.DLG_THIS_WEEK));
		} else if(menu.equals("DLGNextWeek")){
			scrape = new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.DLG_NEXT_WEEK));
		} else if(menu.equals("PortolaThisWeek")){
			scrape = new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.PORTOLA_THIS_WEEK));
		} else if(menu.equals("PortolaNextWeek")){
			scrape = new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.PORTOLA_NEXT_WEEK));
		} else if(menu.equals("OrtegaThisWeek")){
			scrape = new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.ORTEGA_THIS_WEEK));
		} else if(menu.equals("OrtegaNextWeek")){
			scrape = new UCSBJMenuScraper(new RemoteUCSBMenuFile(RemoteUCSBMenuFile.ORTEGA_NEXT_WEEK));
		} else{
			throw new IllegalArgumentException();
		}
		File out = new File(path,menu+".xml");
		try {
			Writer.writeToStream(new PrintStream(new FileOutputStream(out)), scrape.getMenus());
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
