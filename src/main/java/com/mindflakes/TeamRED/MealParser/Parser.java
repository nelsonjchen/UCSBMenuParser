package com.mindflakes.TeamRED.MealParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import com.mindflakes.TeamRED.MenuXML.Writer;
import com.mindflakes.TeamRED.UCSBScrape.RemoteUCSBMenuFile;
import com.mindflakes.TeamRED.UCSBScrape.UCSBJMenuScraper;
import com.mindflakes.TeamRED.menuClasses.MealMenu;

/**
 * Main source file for the UCSBMenuParser application. It pulls
 * live UCSB menus from online, parses them, and then saves them to the filenames below, in the savepath
 * specified in the config file in the same directory as the executable.
 * <br> The savepath must not have any special escapes or quotations or anything to that effect, 
 * only the absolute path. For example, :"savepath=C:/Users/Team RED/parser/output" where output is a directory.
 * @author Johan Henkens
 *
 */
public class Parser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		  if(args.length!=1) throw new IllegalArgumentException("Must be run as parser.jar \"PathnameToOutfilesDirectory\". Can be relative or absolute.");
		  File path = new File(args[0]);
		  if(!path.exists()){
		   if(!path.mkdirs()) System.exit(1);
		  }
		  parseAllMenus(path);
	}
	
	private static void parseAllMenus(File path){
		ArrayList<MealMenu> menus;
		menus = parseMenu(path,"CarrilloThisWeek");
		menus.addAll(parseMenu(path,"CarrilloNextWeek"));
		menus.addAll(parseMenu(path,"DLGThisWeek"));
		menus.addAll(parseMenu(path,"DLGNextWeek"));
		menus.addAll(parseMenu(path,"PortolaThisWeek"));
		menus.addAll(parseMenu(path,"PortolaNextWeek"));
		menus.addAll(parseMenu(path,"OrtegaThisWeek"));
		menus.addAll(parseMenu(path,"OrtegaNextWeek"));
		combineMenus(path,menus);
	}
	
	private static ArrayList<MealMenu> parseMenu(File path,String menu){
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
			return scrape.getMenus();
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private static void combineMenus(File path,ArrayList<MealMenu> menus){
		File out = new File(path,"CombinedNextTwoWeeks.xml");
		try {
			Writer.writeToStream(new PrintStream(new FileOutputStream(out)),menus);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
