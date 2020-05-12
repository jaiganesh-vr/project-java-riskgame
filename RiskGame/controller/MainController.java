package controller;


import controller.MapController;
import helper.PrintConsoleAndUserInput;
import model.Game;
import views.CardView;
import views.CardView;
import views.CardView;
/**
 * This is a main class to run the game.
 *
 * @author Gargi Sharma
 * @version 1.0.0
 */
public class MainController {

	/**
	 * This is a main method to run the game.
	 * This function is used to enter the user input and call the functions to create or edit the map, start, load the game
	 * and user can exit if he wants to exit the game.
	 * This function also displays the error message to select valid user input.
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		startMenu();	
	}

	/**
	 * Start menu 
	 */
	public static void startMenu1() {
		MapController mapController = new MapController();
		PrintConsoleAndUserInput print = new PrintConsoleAndUserInput();
		GameController gameController = new GameController();


		int swValue;

		// Display menu graphics

		swValue = displaymainMenu();

		// Switch construct
		switch (swValue) {
		case 1:
			mapController.generateMap();
			break;
		case 2:
			gameController.initializeGame();
			break;
		case 3:
			gameController.loadSavedGame();			
			break;

		default:
			System.out.println("Invalid selection");
			break; // This break is not really necessary
		}
	}


	/**
	 * This method is used to choose an option for the start menu.
	 */
	public static void startMenu() {
		MapController mapController = new MapController();
		PrintConsoleAndUserInput print = new PrintConsoleAndUserInput();
		GameController gameController = new GameController();

		int selectMainMenuOption = 0;
		boolean checkMapStatus = false;
		do {
			selectMainMenuOption = displaymainMenu();
			switch (selectMainMenuOption)
			{
			case 1:
				mapController.generateMap();
				break;
			case 2:
				gameController.initializeGame();
				break;

			case 3:
				gameController.loadSavedGame();
				break;

			case 4:
				print.consoleErr("Thanks for playing this Game.");
				System.exit(0);

			default :
				System.err.println("\n\t Error! Select option from the menu list (1 to 5):");
				break;
			}

		}
		while (selectMainMenuOption != 5);
		System.exit(0);		
	}

	/**
	 * This is the method for Displaying main menu for game. 
	 * This function is used to show the user input to create or edit the map, start, load the game
	 * and user can exit if he wants to exit the game.
	 * @return userIntInput
	 */
	public static int displaymainMenu() {
		PrintConsoleAndUserInput print = new PrintConsoleAndUserInput();
		print.consoleOut("\n*********************************");
		print.consoleOut("\t Risk Game\t");
		print.consoleOut("1.Map Generator");
		print.consoleOut("2.Start Game");
		print.consoleOut("3.Load Saved Game");
		print.consoleOut("4.Exit Game");
		print.consoleOut("\n*********************************");
		print.consoleOut("Please Enter Your Choice from the list: ");
		return print.userIntInput();
	}
}
