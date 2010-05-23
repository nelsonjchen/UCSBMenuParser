package com.mindflakes.TeamRED.MenuXML;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.*;

import com.mindflakes.TeamRED.UCSBScrape.*;
import com.mindflakes.TeamRED.menuClasses.MealMenu;


public class ReaderWriterTests {
	static ArrayList<MealMenu> fromWeb;
	ArrayList<MealMenu> before;
	ArrayList<MealMenu> after;
	static File file1 = new File("jUnitTestFile1.txt.tmp");
	static File file2 = new File("jUnitTestFile2.txt.tmp");


	
    @BeforeClass
    public static void oneTimeSetUp() {
    	UCSBJMenuScraper scraper = new UCSBJMenuScraper(
    			new RemoteUCSBMenuFile(RemoteUCSBMenuFile.CARRILLO_THIS_WEEK));
    	fromWeb = scraper.getMenus();
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }


    @Before
    public void setup() {
    	before = fromWeb;
    }

    @After
    public void tearDown() {
    }

    @Test
    public void verifyEqualMenus() {
    	try {
    		Writer.writeToStream(new PrintStream(new FileOutputStream(file1)), before);
    		after = Reader.readFile(new Scanner(file1));
    		assertTrue(equalMenus(before,after));
    		before = after;
    		Writer.writeToStream(new PrintStream(new FileOutputStream(file2)), before);
    		after = Reader.readFile(new Scanner(file2));
    		assertTrue(equalMenus(before,after));
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	}
    }
    
    public boolean equalMenus(ArrayList<MealMenu> menus1, ArrayList<MealMenu> menus2){
    	if(menus1.size()!=menus2.size()) return false;
    	for(int i = 0; i<menus1.size();i++){
    		if(!menus1.get(i).equals(menus2.get(i))) return false;
    	}
    	return true;
    }


}
