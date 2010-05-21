package com.mindflakes.TeamRED.UCSBScrape;
import java.util.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.mindflakes.TeamRED.menuClasses.*;

/** A class that 'scrapes' data from a UCSB XML Menu, creating populated MealMenu objects which can be much more easily worked with.
 * Uses the Joda Time library for all time-based calculations and data, storing each time as the long returned by DateTime.getMillis().
 * Although there is not any error detection code implemented in the scraper, the scraper does try to estimate what each line represents
 * by creating 'columns' of similar values for word positioning and comparing each line against existing values to find its place.
 * The scraper also checks for some signals of irrelevant lines such as a line font size (determined by text height) that is too small.
 * @author Johan Henkens
 *
 */
public class UCSBJMenuScraper {
	private static final String[] mealNames = {"Breakfast","Lunch","Dinner","Brunch","Late Night"};
	@SuppressWarnings("unused")
	private UCSBMenuFile file;
	private ArrayList<MealMenu> menus = new ArrayList<MealMenu>();

	private static String[][] getMealTimes(String commonsName){
		if(commonsName.contains("Carrillo")){
			String[][] result = {{"0715", "1000"},
					{"1100", "1430"},
					{"1700","2000"},
					{"1030","1400"},
					null};
			return result;
		} else if (commonsName.contains("De La Guerra")){
			String[][] result = {null,
					{"1100", "1430"},
					{"1700","2000"},
					{"1030","1400"},
					{"2100","2300"}};
			return result;
		} else if (commonsName.contains("Ortega")){
			String[][] result = {{"0715", "1045"},
					{"1145", "1400"},
					{"1700","2000"},
					null,
					null};
			return result;
		} else if (commonsName.contains("Portola")){
			String[][] result = {{"0700","1030"},
					{"1200", "1400"},
					{"1700","2000"},
					{"1030","1400"},
					null};
			return result;
		}
		throw new NullPointerException();
	}

	private static String getRealCommonsName(String lineBody){
		lineBody = lineBody.toLowerCase();
		if(lineBody.contains("carrillo")){
			return "Carrillo Commons";
		} else if(lineBody.contains("guerra")){
			return "De La Guerra Commons";
		} else if(lineBody.contains("ortega")){
			return "Ortega Commons";
		} else if(lineBody.contains("portola")){
			return "Portola Commons";
		} else return lineBody;
	}
	
	/** Constructs a <code>UCSBJMenuScraper</code> object, using a <code>UCSBMenuFile</code> created from the specified filename or absolute URL, and mode. 
	 * The constructor parses the file and populates this objects' <code>MealMenu</code> collection.
	 * <p>
	 * The URL must be absolute.
	 * @param filename name or absolute URL to the XML file that is to be parsed.
	 * @param local <code>boolean</code> value specifying if the filename points to a local file (<code>true</code>) or a remote file (<code>false</code>).
	 */
	public UCSBJMenuScraper(String filename, boolean local) {
		this((local) ? new LocalUCSBMenuFile(filename) : new RemoteUCSBMenuFile(filename));
	}

    /** Constructs a <code>UCSBJMenuScraper</code> object, reading from the <code>UCSBMenuFile</code> specified. 
     * The constructor parses the file and populates this objects' <code>UCSBMenuFile</code> collection.
     * @param file the specified <code>UCSBMenuFile</code>
     * @throws can throw <code>LineErrorException</code> if an unknown parsing error occurs, in which case the file should be reported to administrators
     */
    public UCSBJMenuScraper(UCSBMenuFile file) {    	
        this.file = file;
        String currentLine = file.nextLine();
        ArrayList<ArrayList<String> > breakfasts = new ArrayList<ArrayList<String>>(7);
        ArrayList<ArrayList<String> > lunches = new ArrayList<ArrayList<String>>(7);
        ArrayList<ArrayList<String> > dinners = new ArrayList<ArrayList<String>>(7);
        ArrayList<ArrayList<String> > brunches = new ArrayList<ArrayList<String>>(7);
        ArrayList<ArrayList<String> > lateNites = new ArrayList<ArrayList<String>>(7);
        initializeALofALofString(breakfasts);
        initializeALofALofString(lunches);
        initializeALofALofString(dinners);
        initializeALofALofString(brunches);
        initializeALofALofString(lateNites);
        int[][] positions = new int[7][20];
        String[] dates = new String[7];
        String[][] mealTimes;
        
        ArrayList<ArrayList<String>> currentMealArrayList = breakfasts;
        
        long modDateMillis=0;
        String currentCommons = "";

        //Gets to line containing CreationDate for all menus
        while(!currentLine.contains("CreationDate")) {
        	currentLine = file.nextLine();
        }
        //Removes just the content from the CreationDate line.
        currentLine=currentLine.substring(currentLine.indexOf('\"',currentLine.indexOf("Date\"")+5)+1,currentLine.lastIndexOf('-'));
        modDateMillis=modDateStringToLongMillis(currentLine);
        
        while(!currentLine.contains("Commons")){
        	currentLine=file.nextLine();
        }
		if(!currentLine.contains("Commons")){
			throw new LineErrorException("Expected \"Commons\" in line" + currentLine);
		}
		currentCommons = getRealCommonsName(getLineBody(currentLine));
        mealTimes = getMealTimes(currentCommons);

//        System.out.println(DateTimeFormat.mediumDateTime().print(new DateTime(modDateMillis)));
        currentLine = file.nextLine();
        boolean firstPass= true;
        String lineBody;
        
        while(true){
        	if(currentLine.equals("EOF")) break;
        	if(currentLine.isEmpty() || currentLine.contains("<page ") || currentLine.equals("</page>")|| 
        			currentLine.contains("Weekly Menu")|| currentLine.equals("</pdf2xml>")||
        			currentLine.contains(" Commons") 
        			|| getLineHValue(currentLine)<8){
        		currentLine = file.nextLine();
        		continue;
        	}
        	lineBody = getLineBody(currentLine);
        	
        	
        	if(lineBodyHasMealName(lineBody)){      		
        			// Sets the 'currentMealArrayList' to the one appropriate for the current meal
        			switch(findMealNumberFromBodyOfMealNameLine(lineBody)){
        			case 0:
        				currentMealArrayList = breakfasts;
        				break;
        			case 1:
        				currentMealArrayList = lunches;
        				break;
        			case 2:
        				currentMealArrayList = dinners;
        				break;
        			case 3:
        				currentMealArrayList = brunches;
        				break;
        			case 4:
        				currentMealArrayList = lateNites;
        				break;
        			}
        		
        		currentLine=file.nextLine();
        		lineBody=getLineBody(currentLine);
        		{
        			int dayOfWeekNumber = dayOfWeekNumberFromLineBody(lineBody);
        			
        			// While loop for the day of week lines, eg: Monday
        			while(dayOfWeekNumber!=0){
        				if(!firstPass){
        					currentLine=file.nextLine();
        					lineBody= getLineBody(currentLine);
        					dayOfWeekNumber = dayOfWeekNumberFromLineBody(lineBody);
        					continue;
        				}
        				
        				int currentPos = getLineLValue(currentLine);
        				addPosnToArr(positions[dayOfWeekNumber-1],currentPos);
        				
        				currentLine = file.nextLine();  
        				lineBody = getLineBody(currentLine);
    					dayOfWeekNumber = dayOfWeekNumberFromLineBody(lineBody);
        			}
        		}
        		// While loop for the "Date" lines, eg: April 21
        		while(isLineBodyADate(lineBody)){
        			if(!firstPass){
        				currentLine=file.nextLine();
        				lineBody = getLineBody(currentLine);
        				continue;
        			}
        			int currentPos = getLineLValue(currentLine);
        			int dayNear = getNearestDayIndex(positions,currentPos);
        			addPosnToArr(positions[dayNear],currentPos);
        			
        			dates[dayNear]=lineBody;
        			
        			currentLine=file.nextLine();
        			lineBody = getLineBody(currentLine);
        		}
        		firstPass=false;
        		
        		while(!currentLine.equals("</page>")){
        			lineBody=getLineBody(currentLine);
        			if(getLineHValue(currentLine)<8) {
        				currentLine=file.nextLine();
        				continue;
        			}
        			//Checks if the line matches venue names and then adds to all lists if it is a venue.
        			if(isLineBodyAVenue(lineBody,currentCommons)){
        		    	if(lineBody.indexOf(" - ")!=-1){
        		    		lineBody = lineBody.substring(lineBody.indexOf(" - ")+3);
        		    		currentLine = setLineBody(currentLine,lineBody);
        		    	}
        				for(ArrayList<String> ar : currentMealArrayList){
        					ar.add(currentLine);
        				}
        				currentLine=file.nextLine();
        				continue;
        			}
        			
        			
        			boolean isTwoEntries = isTwoEntries(currentLine);
        			String secondLine = "";
        			
        			//Following block of code is used to handle if multiple entries exist on one line
        			if(isTwoEntries){
        				
        				int currentPos = getLineLValue(currentLine);
            			int dayNear = getNearestDayIndex(positions,currentPos);
            			
            			//If it is 6, that means the line starts at the sunday column, and nothing comes after that.
            			if(dayNear==6) isTwoEntries=false;
            			
            			else{
            				String[] twoLines = doubleLineHelper(currentLine,lineBody,positions,currentPos,dayNear);
            				currentLine = twoLines[0];
            				lineBody = getLineBody(currentLine);
            				secondLine = twoLines[1];
            			}
        			}
        			
        			//Standard adding code, also used with two entires
        			int currentPos = getLineLValue(currentLine);
        			int dayNear = getNearestDayIndex(positions,currentPos);
        			addPosnToArr(positions[dayNear],currentPos);
        			currentMealArrayList.get(dayNear).add(currentLine);

        			if(!isTwoEntries){
        				currentLine = file.nextLine();
        			}
        			else{
        				currentLine = secondLine;
        			}
        		}
        		
        	}
        }
        
    	convertDatesToMMDDYYYY(dates);
    	createMealMenusForMeal(breakfasts,currentCommons, dates, mealTimes[0],modDateMillis,mealNames[0]);
    	createMealMenusForMeal(lunches,currentCommons, dates, mealTimes[1],modDateMillis,mealNames[1]);
    	createMealMenusForMeal(dinners,currentCommons, dates, mealTimes[2],modDateMillis,mealNames[2]);
    	createMealMenusForMeal(brunches,currentCommons, dates, mealTimes[3],modDateMillis,mealNames[3]);
    	createMealMenusForMeal(lateNites,currentCommons, dates, mealTimes[4],modDateMillis,mealNames[4]);
    	checkMealMenus();
    }
    
    private void checkMealMenus(){
    	for(int i = 0; i <menus.size();i++){
    		if(menus.get(i)==null||
    				menus.get(i).getVenues()==null
    				|| menus.get(i).getVenues().size()==0){
    			menus.remove(i);
    			i--;
    		}
    	}
    }
    
    
    /** retrieves the <code>ArrayList</code> of <code>MenuMenu</code> objects that was populated by the constructor of this object and returns the list.
     * @return the populated list of <code>MenuMenu</code> objects
     */
    public ArrayList<MealMenu> getMenus() {
		return menus;
	}
    
   
    private String[] doubleLineHelper(String workingLine, String lineBody, int[][] positions, int currentPos, int dayNear){
    	String[] result = new String[2];

    	int[] pArr = getLinePValues(workingLine);
    	int pos = -1;
    	for(int i = 2;i<pArr.length;i+=2){
    		for(int o = 0; o<positions[dayNear+1].length;o++){
    			if(positions[dayNear+1][o]==pArr[i]){
    				pos=i;
    				i=pArr.length;
    				break;
    			} else if(positions[dayNear+1][o]==0) break;
    		}
    	}
    	if(pos==-1){
    		int diff = 50;
    		for(int i = 2;i<pArr.length;i+=2){
   				for(int o = 0; o<positions[dayNear+1].length;o++){
   					if(Math.abs(positions[dayNear+1][o]-pArr[i])<diff){
   						diff = Math.abs(positions[dayNear+1][o]-pArr[i]);
   						pos=i;
   					}
   				}
   			}
      	}

    	String[] contentAr = lineBody.split(" ");
    	String currentBody = "";
    	String secondBody = "";

    	for(int o = 0; o<contentAr.length;o++){
    		if(o<pos/2) currentBody+=(contentAr[o]+" ");
    		else secondBody+=(contentAr[o]+" ");
    	}

    	currentBody=currentBody.trim();
    	secondBody=secondBody.trim();
    	
    	result[0]=setLineBody(workingLine,currentBody);
    	
    	result[1]=setLineLValue(workingLine,pArr[pos]);
    	result[1]=setLineWValue(result[1],120);
    	result[1]=setLineBody(result[1],secondBody);
    	return result;
    }
    
	private static long modDateStringToLongMillis(String modDate){
    	int[] result = new int[7];
    		result[0]=Integer.parseInt(modDate.substring(0,4));
    		result[1]=Integer.parseInt(modDate.substring(4,6));
    		result[2]=Integer.parseInt(modDate.substring(6,8));
    		result[3]=Integer.parseInt(modDate.substring(8,10));
    		result[4]=Integer.parseInt(modDate.substring(10,12));
    		result[5]=Integer.parseInt(modDate.substring(12,14));
    		result[6]=00;
    	return (new DateTime(result[0],result[1],result[2],result[3]
    	   ,result[4],result[5],result[6],DateTimeZone.forID("America/Los_Angeles"))).getMillis();
    }
    
    private static void initializeALofALofString(ArrayList<ArrayList<String>> arrList){
    	for(int i = 0; i<7;i++){
    		arrList.add(new ArrayList<String>());
    	}
    }
    
   
    private static boolean hasMoreThanVenues(ArrayList<String> arr, String commons){
    	for(String st : arr){
    		if(!isLineBodyAVenue(getLineBody(st), commons)) return true;
    	}
    	return false;
    }
    
    private static String fixFoodNameErrors(String in){
    	while(in.indexOf("&amp;") != -1){
    		in=in.substring(0,in.indexOf("&amp;"))+"&"+in.substring(in.indexOf("&amp;")+5);
    	}
    	if(in.indexOf(' ')!=-1){
    		String tmp = in.substring(0,in.indexOf(' '));
    		boolean safeToCutOffFront = true;
    		for(int i = 0; i<tmp.length();i++){
    			if(tmp.charAt(i)<'0'||tmp.charAt(i)>'9'){
    				safeToCutOffFront = false;
    				break;
    			}
    		}
    		if(safeToCutOffFront) in = in.substring(in.indexOf(' ')+1);
    	}
    	return in;
    }
    
    /**
     * @author Kenneth Hwang
     * @param in
     * @return
     */
    private static boolean isVegan(String in){
    	in = in.toLowerCase();
    	if (in.contains("vegan")) {
    		return true;
    	} else {
    		if (in.toLowerCase().contains("rice") && !(in.toLowerCase().contains("mexican"))) {
    			return true;
    		} else if (in.toLowerCase().equals("baked potato")) {
    			return true;
    		} else if (in.toLowerCase().equals("corn")) {
    			return true;
    		} else if (in.toLowerCase().equals("oatmeal")) {
    			return true;
    		}
    		return false;
    	}
    }
    /**
     * @author Kenneth Hwang
     * @param in
     * @return
     */
    private static boolean isVgt(String in){
    	in = in.toLowerCase();
    	if (in.contains("vgt")) {
    		return true;
    	} else if(isVegan(in)) { 
    		return true;
    	}	else {
    		//meats
    		if (in.contains("beef") || in.contains("chicken") || 
    				in.contains("pork") || in.contains("meat") || 
    				in.contains("bacon") || in.contains("beef") ||
    				in.contains("fish") || in.contains("turkey") || 
    				in.contains("ham") || in.contains("patstrami") || 
    				in.contains("chop suey") || in.contains("clam") ||
    				in.contains("salami") || in.contains("pepperoni") || 
    				in.contains("sloppy joes") || in.contains("charburger") || 
    				in.contains("ahi") || in.contains("tuna") || 
    				in.contains("sausage") || in.contains("shrimp") || in.contains("cheeseburger")||
    				in.contains("carnitas")) {
    			return false;
    				

    		} else {
    			return true;
    		}
    	}
    }
    
    private static void convertDatesToMMDDYYYY(String[] dates){

    	int currentYear = (new DateTime()).getYear();
    	int currentMonth = (new DateTime()).getMonthOfYear();
    	for(int i = 0; i<dates.length;i++){
    		if(dates[i].indexOf(" ") == -1)continue;
    		String year = ""+currentYear;
    		String cur = dates[i].substring(0,dates[i].indexOf(" ")).toLowerCase();
    		if(cur.equals("january")){
    			cur="01";
    			if(currentMonth==12){
    				currentYear++;
    				year=""+currentYear;
    			}
    		} else if(cur.equals("february")){
    			cur="02";
    		} else if(cur.equals("march")){
    			cur="03";
    		} else if(cur.equals("april")){
    			cur="04";
    		} else if(cur.equals("may")){
    			cur="05";
    		} else if(cur.equals("june")){
    			cur="06";
    		} else if(cur.equals("july")){
    			cur="07";
    		} else if(cur.equals("august")){
    			cur="08";
    		} else if(cur.equals("september")){
    			cur="09";
    		} else if(cur.equals("october")){
    			cur="10";
    		} else if(cur.equals("november")){
    			cur="11";
    		} else if(cur.equals("december")){
    			cur="12";
    		}
    		dates[i]=cur+dates[i].substring(dates[i].indexOf(" ")+1)+year;
    	}
    }


    
    private static long combineAndConvertToMillis(String dates, String time){
    	long result =(new DateTime(
        			Integer.parseInt(dates.substring(4)),
        			Integer.parseInt(dates.substring(0,2)),
        			Integer.parseInt(dates.substring(2,4)),
        			Integer.parseInt(time.substring(0,2)),
        			Integer.parseInt(time.substring(2))
    				,00,00,DateTimeZone.forID("America/Los_Angeles"))).getMillis();

    	return result;
    }

    private  void createMealMenusForMeal(ArrayList<ArrayList<String>> arrOfArr, String commons, String[] dates, String[] mealTimes, long modDateMillis, String mealName){
    	String commonsShort = commons.substring(0,commons.indexOf(" Commons"));
    	for(int i = 0; i<arrOfArr.size();i++){
    		if(!hasMoreThanVenues(arrOfArr.get(i),commons)) continue;
    		ArrayList<Venue> venues = new ArrayList<Venue>();
    		ArrayList<FoodItem> foodItems = null;
    		for(String st : arrOfArr.get(i)){
    			st = getLineBody(st);
    			if(isLineBodyAVenue(st,commons)){
    				venues.add(new Venue(st,new ArrayList<FoodItem>()));
    				foodItems = venues.get(venues.size()-1).getFoodItems();
    				continue;
    			}
    			try{
    				foodItems.add(new FoodItem(fixFoodNameErrors(st),isVegan(st), isVgt(st)));
    			} catch(NullPointerException e){
    				System.out.println((foodItems==null)+"::"+st);
    				throw e;
    			}
    		}
    		long startMillis = combineAndConvertToMillis(dates[i], mealTimes[0]);
    		long endMillis  = combineAndConvertToMillis(dates[i], mealTimes[1]);
    		for(int o = 0; o < venues.size();o++){
    			venues.get(o).fixFoodItems();
    			if(venues.get(o).getFoodItems()==null||venues.get(o).getFoodItems().size()==0){
    				venues.remove(o);
    				o--;
    			}
    		}
    		if(venues!=null&&venues.size()!=0){
    			menus.add(new MealMenu(commonsShort,
    					startMillis,endMillis,modDateMillis, venues, mealName));
    		}
    	}
    }
    
    

    
    private static boolean isTwoEntries(String line){
    	return (getLineWValue(line)>135);
    }
    
    private static boolean isLineBodyAVenue(String line, String commonsName){
    	line=line.toLowerCase();
    	if(line.indexOf(" - ")!=-1){
    		line = line.substring(line.indexOf(" - ")+3);
    	}
    	//For Carrillo venues...
    	if(commonsName.equals("Carrillo Commons") && (line.equals("grill (cafe)") || line.equals("bakery") ||
    			line.equals("salads") || line.equals("deli")||
    			line.equals("mongolian grill") || line.equals("euro")||
    			line.equals("pizza") || line.equals("pasta"))) return true;
    	//For DLG venues...
    	if(commonsName.equals("De La Guerra Commons") && (
    			line.equals("blue plate special") || line.equals("taqueria (east side)") ||
    			line.equals("pizza") || line.equals("to order")||line.equals("grill (cafe)") ||
    			line.equals("salads/deli (west side)") || line.equals("bakery"))) return true;
    	//For ortega venues...
    	if(commonsName.equals("Ortega Commons") && (line.equals("hot foods") || line.equals("bakery") ||
    			line.equals("salads"))) return true;
    	//For portola venues...
    	if(commonsName.equals("Portola Commons") && (line.equals("hot foods") || line.equals("specialty line") ||
    			line.equals("bakery") || line.equals("hot foods") || line.equals("salads"))) return true;
    	return false;
    }
    

    private static boolean isLineBodyADate(String line){
    	try{
    		if(line.indexOf(" ")==-1 || line.indexOf(" ")+1==line.length()) return false;
    		Integer.parseInt(line.substring(line.indexOf(" ")+1));
    	} catch(NumberFormatException e){
    		return false;
    	}
    	line=line.substring(0,line.indexOf(" ")).toLowerCase();
    	return (line.equals("januarary")||line.equals("february")||line.equals("march")||line.equals("april")||line.equals("may")||line.equals("june")||line.equals("july")
    			||line.equals("august")||line.equals("september")||line.equals("october")||line.equals("november")||line.equals("december"));
    }
    
    private static void addPosnToArr(int[] arr, int posn){
		int openIndex = 0;
		while(arr[openIndex]!=0){
			if(arr[openIndex]==posn || arr.length==(openIndex+1)){
				openIndex=-1;
				break;
			}
			openIndex++;
		}
		if(openIndex!=-1) arr[openIndex]=posn;
    }
    
    private int getNearestDayIndex(int[][] positions, int posn){
    	int closest = 0;
    	int closestDiff = Math.abs(positions[0][0]-posn);
    	for(int i = 0; i< positions.length; i++){
    		for(int o = 0; o<positions[i].length;o++){
    			if(positions[i][o]==0) break;
    			if(positions[i][o]==posn) return i;
    		}
    	}
    	for(int i = 1; i<positions.length;i++){
    		if(Math.abs(positions[i][0]-posn)<closestDiff){
    			closest = i;
    			closestDiff = Math.abs(positions[i][0]-posn);
    		}
    	}
    	return closest;
    }
    
    private static int dayOfWeekNumberFromLineBody(String cL){  	
    	if(cL.equals("Monday")) return 1;
    	if(cL.equals("Tuesday")) return 2;
    	if(cL.equals("Wednesday")) return 3;
    	if(cL.equals("Thursday")) return 4;
    	if(cL.equals("Friday")) return 5;
    	if(cL.equals("Saturday")) return 6;
    	if(cL.equals("Sunday")) return 7;
    	return 0;
    }
    
    private static boolean lineBodyHasMealName(String currentLine){
    	return ((currentLine.startsWith("Breakfast") ||
    			currentLine.startsWith("Lunch")||
    			currentLine.startsWith("Dinner")||
    			currentLine.startsWith("Brunch")||
    			currentLine.startsWith("Late Night")));
    }
    
    private static int findMealNumberFromBodyOfMealNameLine(String currentLine){
    	if(currentLine.startsWith("Breakfast")) return 0;
    	if(currentLine.startsWith("Lunch")) return 1;
    	if(currentLine.startsWith("Dinner")) return 2;
    	if(currentLine.startsWith("Brunch")) return 3;
    	if(currentLine.startsWith("Late Night")) return 4;
    	return -1;
    }
    
    private static String fixBodyOfMealTimeLineForAMPM(String inputTime){
		if(Integer.parseInt(inputTime.substring(0,inputTime.indexOf(":")))==12){
	    	if(inputTime.substring(inputTime.length()-2).toLowerCase().equals("pm")){
				inputTime="12"+inputTime.substring(inputTime.indexOf(":")+1,inputTime.length()-2);
	    	} else{
				inputTime="00"+inputTime.substring(inputTime.indexOf(":")+1,inputTime.length()-2);
	    	}
		}
		else if(inputTime.substring(inputTime.length()-2).toLowerCase().equals("pm")){
			inputTime=""+(Integer.parseInt(inputTime.substring(0,inputTime.indexOf(":")))+12)+inputTime.substring(inputTime.indexOf(":")+1,inputTime.length()-2);
		} else{
			inputTime=inputTime.substring(0,inputTime.indexOf(':'))+inputTime.substring(inputTime.indexOf(":")+1, inputTime.indexOf(":")+3);
			if(inputTime.length()==3) inputTime = "0"+inputTime;
		}
    	return inputTime;
    }
    
    //Getters and setters for the line values
    
    private static String getLineBody(String inputLine){
    	return inputLine.substring(inputLine.indexOf('>')+1,inputLine.lastIndexOf('<'));
    }
    
    private static String setLineBody(String inputLine, String bodyVal){
    	return inputLine.substring(0,inputLine.indexOf('>')+1)+bodyVal+inputLine.substring(inputLine.lastIndexOf("</"));
    }
    
    private static int getLineLValue(String currentLine){
    	return Integer.parseInt(getLineLString(currentLine));
    }
    
    private static String getLineLString(String currentLine){
    	return currentLine.substring(currentLine.indexOf("l=\"")+3,currentLine.indexOf("\"", currentLine.indexOf("l=\"")+3));
    }
    
    private static String setLineLValue(String line, String lVal){
    	return line = line.substring(0,line.indexOf("l=\"")+3)+lVal+
    	line.substring(line.indexOf("\"",line.indexOf("l=\"")+3));
    }
    
    private static String setLineLValue(String line, int lVal){
    	return line = line.substring(0,line.indexOf("l=\"")+3)+lVal+
    	line.substring(line.indexOf("\"",line.indexOf("l=\"")+3));
    }
    
    private static int getLineTValue(String currentLine){
    	return Integer.parseInt(getLineTString(currentLine));
    }
    
    private static String getLineTString(String currentLine){
    	return currentLine.substring(currentLine.indexOf("t=\"")+3,currentLine.indexOf("\"", currentLine.indexOf("t=\"")+3));
    }
    
    private static String setLineTValue(String line, String tVal){
    	return line = line.substring(0,line.indexOf("t=\"")+3)+tVal+
    	line.substring(line.indexOf("\"",line.indexOf("t=\"")+3));
    }
    
    private static String setLineTValue(String line, int tVal){
    	return line = line.substring(0,line.indexOf("t=\"")+3)+tVal+
    	line.substring(line.indexOf("\"",line.indexOf("t=\"")+3));
    }
    
    private static int getLineWValue(String currentLine){
    	return Integer.parseInt(getLineWString(currentLine));
    }
    
    private static String getLineWString(String currentLine){
    	return currentLine.substring(currentLine.indexOf("w=\"")+3,currentLine.indexOf("\"", currentLine.indexOf("w=\"")+3));
    }
    
    private static String setLineWValue(String line, String wVal){
    	return line = line.substring(0,line.indexOf("w=\"")+3)+wVal+
    	line.substring(line.indexOf("\"",line.indexOf("w=\"")+3));
    }

    private static String setLineWValue(String line, int wVal){
    	return line = line.substring(0,line.indexOf("w=\"")+3)+wVal+
    	line.substring(line.indexOf("\"",line.indexOf("w=\"")+3));
    }
    
    private static int getLineHValue(String currentLine){
    	return Integer.parseInt(getLineHString(currentLine));
    }
    
    private static String getLineHString(String currentLine){
    	return currentLine.substring(currentLine.indexOf("h=\"")+3,currentLine.indexOf("\"", currentLine.indexOf("h=\"")+3));
    }
    
    private static String setLineHValue(String line, String hVal){
    	return line = line.substring(0,line.indexOf("h=\"")+3)+hVal+
    	line.substring(line.indexOf("\"",line.indexOf("h=\"")+3));
    }
    
    private static String setLineHValue(String line, int hVal){
    	return line = line.substring(0,line.indexOf("h=\"")+3)+hVal+
    	line.substring(line.indexOf("\"",line.indexOf("h=\"")+3));
    }
    
    private static int[] getLinePValues(String in){
    	in=getLinePString(in);
    	String[] ar = in.split(",");
    	int[] result = new int[ar.length];
    	for(int i = 0;i<ar.length;i++){
    		result[i]=Integer.parseInt(ar[i]);
    	}
    	return result;
    }
    
    private static String getLinePString(String currentLine){
    	return currentLine.substring(currentLine.indexOf("p=\"")+3,currentLine.indexOf("\"", currentLine.indexOf("p=\"")+3));
    }
    
    private static String setLinePValue(String line, String pVal){
    	return line = line.substring(0,line.indexOf("p=\"")+3)+pVal+
    	line.substring(line.indexOf("\"",line.indexOf("p=\"")+3));
    }
    
    private static String setLinePValue(String line, int[] pVals){
    	String pVal = "";
    	for(int a : pVals){
    		pVal = pVal + a + ",";
    	}
    	if(pVal.charAt(pVal.length()-1)==',') pVal = pVal.substring(0,pVal.length()-1);
    	return setLinePValue(line,pVal);
    }
    
}