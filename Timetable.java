
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
    final String CONFIGDIR="configs/";
    final String CONFIGSUF=".timetable";
    final String NEWLINE=System.getProperty("line.separator");

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

} // Timetable class