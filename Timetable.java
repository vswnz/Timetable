
/**
 * Timetable:
 * Top level class that handles timetabling
 * It needs a timetable name.  
 * Timetable name defines the config file
 * The config file then lists other dependencies for timetabling
 *
 * @author Bill
 * @version 26-Nov-22
 */

import java.util.Scanner;  // keyboard and file input
import java.io.File;       // use files.
import java.io.FileWriter; // Write files
import java.io.IOException;  // Handle errors

public class Timetable
{
    Scanner keyboard = new Scanner(System.in);
    final String CONFIGDIR="configs/";  // directory that configuration files are kept in
    final String CONFIGSUF=".timetable";  // suffix used by all timetable configuration files
    final String NEWLINE=System.getProperty("line.separator");

    final int MAXCATS=10;   // Maximum number of different categories we can schedule around.
    int activeCats=0;       // number of categories that are active
    String[] catNames= new String[MAXCATS];  //Names of each of the categories.
    
    final int MAXSLOTS=100; // maximum number of scheduling slots.  
    int activeSlots=0;   // global state variable that records how many slots the timetable has.

    public Timetable()
    {
        File TTFile; // file holding the timetable.
        // first lets make sure we have a valid time table to work with
        String TTName;
        do { 
            System.out.println("Enter timetable name.");
            TTName=keyboard.nextLine().toLowerCase();
            TTFile= new File (CONFIGDIR+TTName+CONFIGSUF);
            if (!TTFile.exists()) makeNewTT(TTName,TTFile);
        } while (!TTFile.exists());

        workWith(TTFile, TTName);

    } // Timetable constructor

    // Make a new timetable file, after confirmation.
    void makeNewTT(String name, File fd){
        System.out.println(name+" is not a known timetable.  Make it as a new one?");
        char yesNo=keyboard.nextLine().toLowerCase().charAt(0);
        if (yesNo=='y'){ // lower case first letter of input
            try {
                FileWriter writer = new FileWriter(fd);
                writer.flush();
                writer.close();
            } catch (IOException e){
                System.out.println("Error creating "+name);
                e.printStackTrace();
            } // error handling for creating blank file
        } // confirm new file wanted.
    } // makeNewTT

    // main loop
    void workWith(File TTFile, String TTName){
        boolean stillWorking=true;
        do{
            System.out.println("\n==================================");
            System.out.println("Working with timetable:" + TTName);
            printMenu();

            System.out.println("Enter command: ");
            String command=keyboard.nextLine().toLowerCase();
            switch(command){
                case "add" : addCategory(TTFile, TTName);
                    break;
                case "display" : displayCats(TTFile);
                    break;
                case "edit" : editCat(TTFile);
                    break;
                case "populate" : populateTimeTable(TTFile, TTName);
                    break;
                case "quit" : stillWorking=false;
                    break;

                default: System.out.println("Command not recognised.");
            } // switch

        } while (stillWorking) ; // finish the program

    }

    // little method to display all menu commands
    void printMenu(){
        System.out.println("Add category");
        System.out.println("Display");
        System.out.println("Edit category");
        System.out.println("Populate a timetable");
        System.out.println("Quit");
    } // print_menu

    //Display categories in config file.
    void displayCats(File TTFile){
        System.out.println("Categories for scheduling");
        boolean foundAny=false;
        try{
            Scanner configReader = new Scanner(TTFile);
            while (configReader.hasNextLine()){
                String theLine=configReader.nextLine();
                String item[]= theLine.split(" ");
                if (item[0].equals("category"))  {
                    System.out.println(item[1]);
                    foundAny=true;
                } // found a category line
            } // while there is a next line

        } catch (IOException e) {
            System.out.println("Error trying to display time table categories");
            e.printStackTrace();
        } // catch-try

        if (!foundAny) System.out.println("No categories defined yet");
    } // displayCats 

    // Method to edit a category file.  You can add lines and attributes
    void editCat(File TTFile){
        System.out.println("Nothing implemented here.");
    } // editCats

    // Add an extra category to the config file.  Make the blank category file.
    void addCategory(File TTFile,  String TTName){
        System.out.println("What category do you wish to add to the timetable?");
        String category=keyboard.nextLine();

        if (categoryExists(TTFile, category)){
            System.out.println(category+" already exists!");
        } else  // category doesn't exist, so lets add it
        {
            // First update the master catalogue
            try {
                FileWriter writer = new FileWriter(TTFile,true);
                writer.write("category "+ category+NEWLINE);
                writer.flush();
                writer.close();
            } catch (IOException e){
                System.out.println("Error adding category to master config file.");
                e.printStackTrace();
            } // error handling for appending to master config file

            // Now create the blank category file.
            // TBD: Should really try to make sure it doesn't exist first.
            try {
                File catFile = new File(CONFIGDIR+TTName+"-"+category+CONFIGSUF);
                if (catFile.exists()){
                    System.out.println("Unexpected error.  The underlying category file already exists.  Not recreating it.");
                } else { // so create it.
                    FileWriter writer = new FileWriter(catFile);
                    writer.flush();
                    writer.close();
                } // file didn't already exist
            } catch (IOException e){
                System.out.println("Error creating blank category config file.");
                e.printStackTrace();
            } // error handling for appending to master config file
        } // add the new catgory
    } // addCategory

    // Returns true if the category already exists, false otherwise.
    boolean categoryExists(File TTFile, String category){
        try{
            Scanner configReader = new Scanner(TTFile);
            while (configReader.hasNextLine()){ // look though config file for a category line that matches.
                String theLine=configReader.nextLine();
                String item[]= theLine.split(" ");
                if (item[0].equals("category") && item[1].equals(category)) return true;
            } // while there is a next line

        } catch (IOException e) {
            System.out.println("Error trying to check whether "+category+ " already existed as a category");
            e.printStackTrace();
        } // catch-try

        return false   ;     
    } // categoryExists

    String[] getSlots(File TTFile){
        String[] slotsFound = new String[MAXSLOTS];
        int found=0;

        try{
            Scanner slotReader = new Scanner(TTFile);
            while (slotReader.hasNextLine()){ // look though config file for a line that describes a timetable slot
                String theLine=slotReader.nextLine();
                String item[]= theLine.split(" ");
                if (item[0].equals("slot") ) {  // We have a slot line
                    if (found==MAXSLOTS){
                        found--;
                        System.out.println("Error: Too many slots found in config file.  Increase MAXSLOTS and recompile!");
                        System.out.println("Discarding slot : "+item[1]);
                    } else {
                        slotsFound[found]=item[1];
                        found++;
                    } // not run off end of array.
                } // if a slot line has been found
            } // while there is a next line

        } catch (IOException e) {
            System.out.println("Error trying to read configuration file for slot information.");
            e.printStackTrace();
        } // catch-try
        activeSlots=found;
        return slotsFound;        
    }

    // Populate a timetable
    // As all timetables must have slots in them, the slots information is kept in the master timetable configuration file.
    void populateTimeTable(File TTFile, String TTname){  
    String[] theSlots=getSlots(TTFile);
    if (activeSlots <1)
        {
            System.out.println("You must have slots defined if your config.  This describes the time structure you");
            System.out.println("want everything timetabled into.  One line per slot, with a name for each.");
            System.out.println("e.g. If you run a two week timetable (A/B) with 3 classes each day you would have lines");
            System.out.println("MondayA1 MondayB1 MondayA2 MondayB2 ModayA3 MondayB3 TuesdayA1...FridayB3\n");
            System.out.println("But if you run a time table with six slots rotating through the week you might just have");
            System.out.println("slot1 slot2 slot3 slot4 slot5 slot5\n");
            System.out.println("Remember, one slot to each line of the file!");
        } else { // go ahead... make my timetable.
            
        } // made the timetable.
    showSlots(theSlots);
    
    } // populateTimeTable

    
    //Display what timetable slots are being used
    void showSlots(String[] slots){
        System.out.println("Timetable slots set up:");
        for (int i=0; i<activeSlots;i++)
            System.out.println(slots[i]);
        
    }

} // Timetable class