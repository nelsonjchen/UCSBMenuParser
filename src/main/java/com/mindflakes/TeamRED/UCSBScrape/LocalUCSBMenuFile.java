package com.mindflakes.TeamRED.UCSBScrape;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/** Local file implementation of the UCSBMenuFile abstract class. Used when the XML file is stored on the local machine
 * @author Johan Henkens
 *
 */
public class LocalUCSBMenuFile extends UCSBMenuFile {
	/** Constructs a <code>LocalUCSBMenuFile</code> with the specified filename as the relative or absolute path to the XML file.
	 * @param fileName relative or absolute path to XML file representing a menu.
	 */
	public LocalUCSBMenuFile(String fileName){
		try {
			sc=new Scanner(new InputStreamReader(new FileInputStream(new File(fileName))));
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
}
