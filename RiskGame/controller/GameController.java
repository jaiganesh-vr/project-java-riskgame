package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import helper.GameMode;
import model.Country;
import model.Game;
import model.Player;
import strategies.*;

import strategies.Random;
import views.*;
import model.MapModel;
import helper.GamePhase;
import helper.PrintConsoleAndUserInput;


// TODO: Auto-generated Javadoc
/**
 * Game Controller initializes the game by calling the game model.
 * It controls the view by actively listing to the view elements and performing the respective actions. 
 * Class that achieves the action listener for the user input 
 * 
 * @author Jaiganesh
 * @version 1.0.0
 */
public class GameController {

	/** The game. */
	Game game;

	/** The board view. */
	BoardView boardView;

	/** The world domination view. */
	WorldDominationView worldDominationViewObserver;

	/** The player. */
	Player playerForGameMode;

	/** The map model. */
	MapModel mapModel = new MapModel();

	/** The card view. */
	CardView cardView;

	/** The print. */
	PrintConsoleAndUserInput print = new PrintConsoleAndUserInput();

	/** The userinput. */
	Scanner userinput = new Scanner(System.in);

	/** The att country. */
	Country attCountry;

	/** The def country. */
	Country defCountry;
	int playerStrategyName =0;
	
	/**
	 * This function is going to initializing the map by taking user input.
	 * @param mapPath path f the map directory
	 */
	public void initializeMap(String mapPath) {
		File tempFile = new File(mapPath);
		boolean exists = tempFile.exists();
		if (exists) {			
			mapModel.readMapFile(mapPath);
			mapModel.printMapValidOrNot();
			if (!mapModel.checkMapIsValid()){
				//print.consoleErr("****Error!! Invalid map name. Please try again with the valid name****");
			}
		}else {
			print.consoleErr("****File not found!!!. Please enter the correct name of map.****");
		}
	}

	public MapModel getInitializedMapModel() {
		mapModel = new MapModel();
		String mapPath = mapModel.getMapNameByUserInput();
		System.out.println(mapPath);
		initializeMap(mapPath);
		return mapModel;
	}

	/**
	 * This method is setting up the board and game model
	 * It is intializing the observer for the gui also
	 * It is taking the the input from the user for creating number of players.
	 */
	public void initializeGame(){

		print.consoleOut("Enter the Game mode you want to play.");
		print.consoleOut("1 -> Single Game Mode. \n2 -> Tournament Mode.");
		int gameMode = PrintConsoleAndUserInput.userIntInput();

		// Check the game mode
		if(gameMode == 1){
			int j=1;
			print.listofMapsinDirectory();
			
			print.consoleOut("\nEnter Map Name to load Map file:\n");
			mapModel = new MapModel();
			String mapPath = mapModel.getMapNameByUserInput();
			initializeMap(mapPath);

			game = new Game(mapModel);

			game.setGameMode(GameMode.SingleGameMode);
			boardView=new BoardView();
			game.addObserver(boardView);
			worldDominationViewObserver = new WorldDominationView();
			game.addObserver(worldDominationViewObserver);

			print.consoleOut("\nEnter the number of Players between 3-5:");
			int playerCount = Integer.parseInt(userinput.nextLine());

			if(playerCount < 3 || playerCount > 5) {
				print.consoleErr("**** Error!!! Please enter the number of Players between 3-5. ****");
			} else {
				for (int i = 0; i < playerCount ; i++) {
					print.consoleOut("\nEnter the name of Player " + j);
					String name = userinput.nextLine();
					Player player = new Player(i,name);
					print.consoleOut("\nEnter The Strategy of playing for Player: " +name);
					print.consoleOut("\n1. Human \n2. Aggressive \n3. Benevolent \n4. Cheater \n5. Random");
					int strategy = Integer.parseInt(userinput.nextLine());

					if(strategy == 1){
						player.setPlayerStrategy(new Human());
					} else if (strategy == 2){
						player.setPlayerStrategy(new Aggressive());
					} else if (strategy == 3){
						player.setPlayerStrategy(new Benevolent());
					} else if (strategy == 4){
						player.setPlayerStrategy(new Cheater());
					} else if (strategy == 5){
						player.setPlayerStrategy(new Random());
					}

					game.addPlayer(player);
					j++;
				}
				game.startGame();
				game.initializeRiskCards();
				boardView.gameWindowLoad();
				game.initializeAutoSequence();
				callListenerOnView();
			}

		} else if (gameMode == 2){
			int M = 0, P = 0, G = 0, D = 0;
			ArrayList<MapModel> mapNamesForTournament = new ArrayList<>();
			ArrayList<PlayerStrategy> strategiesForTournament = new ArrayList<>();


			print.consoleOut("******* Welcome to Tournament Mode. Please Enter the required Fields. *******");
			while (true) {
				print.consoleOut("Enter The Number of Maps You want to play on (1-5): ");
				int numberOfMaps = PrintConsoleAndUserInput.userIntInput();
				if (numberOfMaps >= 1 && numberOfMaps <= 5) {
					M = numberOfMaps;
					break;
				}else{print.consoleErr("Please Enter the number of Maps between 3-5");}
			}

			print.consoleOut("Enter '" + M + "' Different Map Names from following list: ");
			print.listofMapsinDirectory();
			for (int i = 0; i < M; i++) {
				mapModel = new MapModel();
				mapNamesForTournament.add(getInitializedMapModel());
			}
			System.out.println(mapNamesForTournament.size());


			while (true) {
				print.consoleOut("Enter The Number of player strategies you want to play with(2-4): ");
				int numberOfStrategies = PrintConsoleAndUserInput.userIntInput();
				if (numberOfStrategies >= 2 && numberOfStrategies <= 4) {
					P = numberOfStrategies;
					break;
				}else{print.consoleErr("Please Enter the number of Strategies between 2-4. ");}
			}

			print.consoleOut("Enter '" + P + "' Different Strategies from following list:");
			print.consoleOut("1. Aggressive \n2. Benevolent \n3. Cheater \n4. Random");
			for (int i = 0; i < P; i++) {
					int playerStrategyName = PrintConsoleAndUserInput.userIntInput();
					if ((playerStrategyName >= 1 && playerStrategyName <= 4)) {
						if (playerStrategyName == 1) {
							strategiesForTournament.add(new Aggressive());
						} else if (playerStrategyName == 2) {
							strategiesForTournament.add(new Benevolent());
						} else if (playerStrategyName == 3) {
							strategiesForTournament.add(new Cheater());
						} else if (playerStrategyName == 4) {
							strategiesForTournament.add(new Random());
						} else {
							print.consoleErr("For Tournament Select the Strategies between 1-4");
						}
					}
			}

			while (true) {
				print.consoleOut("**** Enter Number of Games you want to play on Each Map (1-5)****** ");
				int numberOfGames = PrintConsoleAndUserInput.userIntInput();
				if (numberOfGames >= 1 && numberOfGames <= 5) {
					G = numberOfGames;
					break;
				}else{print.consoleErr("Please Enter the number of Games between 1-5. ");}
			}

			while (true) {
				print.consoleOut("Enter Maximum Number of Turns for Each Game (10 - 50): ");
				int maximumNumberOfTurns = PrintConsoleAndUserInput.userIntInput();
				if (maximumNumberOfTurns >= 10 && maximumNumberOfTurns <= 50) {
					D = maximumNumberOfTurns;
					break;
				}else{print.consoleErr("Please Enter the number of Maximum Turns between 10-50. ");}
			}

			HashMap<String, ArrayList<String>> tournamentResult = new HashMap<>();

			for (int i = 0; i < M; i++) {
				ArrayList<String> resultForOneMap = new ArrayList<>();
				for (int j = 0; j < G; j++) {
					game = new Game(mapNamesForTournament.get(i));
					game.setGameMode(GameMode.TournamentMode);
					game.setMaxTurnsForTournament(D);
					for (int playerStrategyAsPlayerName = 0; playerStrategyAsPlayerName < strategiesForTournament.size();
						 playerStrategyAsPlayerName++) {
						Player player = new Player(playerStrategyAsPlayerName,
								strategiesForTournament.get(playerStrategyAsPlayerName).getStrategyName());
						player.setPlayerStrategy(strategiesForTournament.get(playerStrategyAsPlayerName));
						game.addPlayer(player);
						System.out.println(player.getPlayerStrategy().getStrategyName());
					}

					boardView=new BoardView();
					game.addObserver(boardView);

					game.startGame();
					game.initializeRiskCards();
					boardView.gameWindowLoad();
					System.out.println(i +""+j);
					game.tournamentMode();

					// add result
					if (game.getGamePhase() == GamePhase.Draw) {
						resultForOneMap.add("DRAW");
					} else {
						resultForOneMap.add(game.getWinner().getPlayerStrategy().getStrategyName());
					}
				}
				tournamentResult.put(mapNamesForTournament.get(i).getMapName(), resultForOneMap);
				
			}
			System.out.println(tournamentResult.toString());
			TournamentModeResultView.callTournamentResult(M,G,D, tournamentResult,strategiesForTournament);


		}else {
			print.consoleErr("Please Enter a Valid Game Mode.");
		}
	}


	/**
	 * This function is used to call the listener functions.
	 */
	private void callListenerOnView(){

		numberOfArmiesClickListener();
		addSourceCountriesListener();
		addMoveArmyButtonListener();
		addAttackerCountryListener();
		addDefenderCountryListener();
		addActionListenerForWorldDominationView();
		addActionListenerForloadAndSaveGame();
		addAttackButtonListener();
		addAllOutButtonListener();
		addEndAttackButtonListener();
		addAttackMoveArmyButtonListener();
		addSkipButtonListener();
		skipExchangeListener();
		exchangeButtonListener();
		setBoardView();

	}

	/**
	 * This method is going to assign armies to the specific countries in initial phase and in reinforcement phase.
	 */
	public void numberOfArmiesClickListener(){
		boardView.addMapLabelsListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JLabel jLabel=	(JLabel) e.getSource();
				String country=jLabel.getToolTipText();
				if (game.getGamePhase()==GamePhase.Startup || game.getGamePhase() == GamePhase.Reinforcement){
					game.addingCountryArmy(country);
				}
			}
		});
	}

	/**
	 * This method is going to populate destination combo box and the number of army combobox.
	 */
	public void addSourceCountriesListener(){
		boardView.addActionListenToSourceCountryList(new ActionListener() {
			@Override			
			public void actionPerformed(ActionEvent  e) {
				String countryName = boardView.getSourceCountry();
				System.out.println("Game controller class"+countryName);
				if(countryName!=null) {
					ArrayList<String> neighborCountries = game.getNeighbouringCountries(countryName);
					int armyCount = game.getArmiesAssignedToCountry(countryName);
					boardView.comboboxFillDestinationCountry(neighborCountries);
					boardView.comboboxFillArmyToMove(armyCount);
				}
			}		
		});
	}

	/**
	 * This method is used to add listeners on the attacker Country List.
	 */
	public void addAttackerCountryListener() {
		boardView.addActionListenToAttackerCountryList(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String countryName = boardView.getAttackerCountry();
				if (countryName != null) {
					ArrayList<String> neighborCountries = game.getOthersNeighbouringCountriesOnly(countryName);
					boardView.comboboxFillDefendersCountry(neighborCountries);
					int diceCount = game.getMaximumDices(countryName, "Attacker");
					boardView.setAttackerDiceComboBox(diceCount);
				}
			}
		});
	}

	/**
	 * to add listeners on the defender Country List.
	 */
	public void addDefenderCountryListener() {
		boardView.addActionListenToDefenderCountryList(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String countryName = boardView.getDefenderCountry();
				if (countryName != null) {
					int diceCount = game.getMaximumDices(countryName, "Defender");
					boardView.setDefenderDiceComboBox(diceCount);
				}
			}
		});
	}

	/**
	 * This method is used to add listener on the Attack Button.
	 */
	public void addAttackButtonListener() {
		boardView.addActionListenToAttackButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String attackerCountry = boardView.getAttackerCountry();
				String defenderCountry = boardView.getDefenderCountry();
				attCountry = mapModel.getCountryFromName(attackerCountry);
				defCountry = mapModel.getCountryFromName(defenderCountry);
				boardView.setVisibalityOfMoveAfterMove();
				if (attackerCountry != null && defenderCountry != null) {
					if (game.getGamePhase() == GamePhase.Attack) {
						Integer attackerDiceCount = Integer.parseInt(boardView.getAttackerDiceNumber());
						Integer defenderDiceCount = Integer.parseInt(boardView.getDefenderDiceNumber());
						boolean state =game.attackPhaseActions(attackerCountry, defenderCountry, attackerDiceCount, defenderDiceCount);
						if(state) {
							attCountry = mapModel.getCountryFromName(attackerCountry);
							defCountry = mapModel.getCountryFromName(defenderCountry);
							boardView.setVisibalityOfMoveAfterConcure();
							boardView.setMoveComboBox(attCountry.getnoOfArmies());
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "Selecting attacking and defending countries");
				}
			}
		});
	}

	/**
	 * This method is used to add listener on the END Attack Button.
	 */
	public void addEndAttackButtonListener() {
		boardView.addActionListenToEndAttackButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (game.getGamePhase() == GamePhase.Attack) {
					game.updateGame();
					boardView.setVisibalityOfMoveAfterMove();
				}
			}
		});
	}

	/**
	 * This method is used to add listener on the END Attack Button.
	 */
	public void addAllOutButtonListener() {
		boardView.addActionListenToAllOutButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (game.getGamePhase() == GamePhase.Attack) {
					String attackerCountry = boardView.getAttackerCountry();
					String defenderCountry = boardView.getDefenderCountry();
					if (attackerCountry != null && defenderCountry != null) {
						boolean state = game.attackAllOutPhase(attackerCountry, defenderCountry);
						if(state) {
							attCountry = mapModel.getCountryFromName(attackerCountry);
							defCountry = mapModel.getCountryFromName(defenderCountry);
							boardView.setVisibalityOfMoveAfterConcure();
							boardView.setMoveComboBox(attCountry.getnoOfArmies());
						}
					} else {
						JOptionPane.showMessageDialog(null, "Selecting attacking and defending countries");
					}
				}
			}
		});
	}

	/**
	 * This method is used to add listener on the move army Button.
	 */
	public void addAttackMoveArmyButtonListener() {
		boardView.addActionListenToMoveButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (game.getGamePhase() == GamePhase.Attack) {
					Integer attackerMoveArmies = Integer.parseInt(boardView.getMoveComboBox());
					game.moveArmies(attCountry,defCountry,attackerMoveArmies);
					attCountry = null;
					defCountry = null;
					boardView.setVisibalityOfMoveAfterMove();
				}
			}
		});
	}

	/**
	 * This method is to update the board view.
	 */
	public void addMoveArmyButtonListener(){
		boardView.moveArmyButtonListener(new ActionListener() {
			public void actionPerformed(ActionEvent  e) {
				if (game.getGamePhase()==GamePhase.Fortification) {
					game.fortificationPhase(boardView.getSourceCountry(),boardView.getDestinationCountry(),boardView.comboboxGetArmyToMove());
				}
			}
		});
	}

	/**
	 * Add action listener for the world domination view
	 */
	public void addActionListenerForWorldDominationView() {
		boardView.worldDominationViewListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				game.dominationViewOn =true;
				DecimalFormat countryPercentFormat = new DecimalFormat(".####");
				ArrayList<Player> playerList = game.getAllPlayers();

				// Get players from the above arraylist and add in the other arraylist.
				int x = 0;
				ArrayList<String> newPlayerNameList = new ArrayList<>();
				for (Player playerData : playerList) {
					String name = playerData.getPlayerName();
					newPlayerNameList.add(name);
					x++;
				}

				// print Player name in tabular columns(Ist row heading)				
				String[] playerNamesInTableColumns = new String[newPlayerNameList.size()];
				int y=0;				
				for ( String nameOfPlayer : newPlayerNameList ) {				

					playerNamesInTableColumns[y] = "Player name : "+nameOfPlayer;
					y++;
				}
				int size = newPlayerNameList.size();

				// Get the Percentage of the map controlled by every player
				Float[] mapPercentage = new Float[size];
				HashMap<Integer,Float> findPercentageOfMap =  game.getPercentageOfMapControlledByEveryPlayer();
				int z=0;
				for (Map.Entry<Integer, Float> entry : findPercentageOfMap.entrySet()) {
					//   System.out.println(entry.getKey()+" : "+entry.getValue());
					float value = entry.getValue();
					mapPercentage[z] = value;
					z++;
				}

				//Get the continents controlled by every player
				String[] continentsAcquired = new String[size];
				HashMap<Integer,String> findContinentsAcquired =  game.getContinentsControlledByEachPlayer();
				int l=0;
				for (Map.Entry<Integer, String> entry : findContinentsAcquired.entrySet()) {
					String value = entry.getValue();
					continentsAcquired[l] = value;
					l++;
				}

				int[] numberOfArmies = new int[size];
				HashMap<Integer, Integer> armiesMap = game.getNumberOfArmiesForEachPlayer();
				int i=0;
				for (Map.Entry<Integer, Integer> entry : armiesMap.entrySet()) {
					int value = entry.getValue();
					numberOfArmies[i] = value;
					i++;
				}

				// To print data in a table
				String[][] dataInTableRows = new String[3][size];
				for (int percentColumn = 0; percentColumn < dataInTableRows[0].length; percentColumn++) {
					String formattedPercent = countryPercentFormat.format(mapPercentage[percentColumn]);
					dataInTableRows[0][percentColumn] = formattedPercent + " %";
				}
				for (int continentColumn = 0; continentColumn < dataInTableRows[0].length; continentColumn++) {
					dataInTableRows[1][continentColumn] = continentsAcquired[continentColumn];
				}
				for (int armyColumn = 0; armyColumn < dataInTableRows[0].length ; armyColumn++) {
					dataInTableRows[2][armyColumn] = Integer.toString(numberOfArmies[armyColumn]);
				}

				worldDominationViewObserver.createJframeForWorldDominationView(dataInTableRows,playerNamesInTableColumns);
			}
		});
	}

	/**
	 * Add action listener for the Load and save Game
	 */
	private void addActionListenerForloadAndSaveGame() {
		// TODO Auto-generated method stub
		boardView.saveGameButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				game.writeObjectToSaveMyGame();
				boardView.closeFrameWindow();
			}			
		});

	}

	/**
	 * Adds the skip button listener.
	 */
	public void addSkipButtonListener() {
		boardView.skipFortificationActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.print("A");
				if(game.getGamePhase()==GamePhase.Fortification) {
					game.skipFortification();
				}
			}
		});
	}

	/**
	 * This function is used to exchange button listener.
	 */
	public void exchangeButtonListener() {
		CardView.exchangeActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (CardView.listCardsOwnedByThePlayer.getSelectedValuesList() != null &&  CardView.listCardsOwnedByThePlayer.getSelectedValuesList().size() > 0) {
					// This list holds the cards selected by the user
					ArrayList<String> selectedCards = (ArrayList<String>) CardView.listCardsOwnedByThePlayer.getSelectedValuesList();
					boolean success = game.exchangeRiskCards(selectedCards);
					if(success) {
						CardView.closeTheWindow();
						boardView.getFrameGameWindow().setEnabled(true);
						game.updateReinforcementValue();						
					}
				}
			}
		});
	}

	/**
	 * This function is going to close/skip if number of card is less than 5.
	 */
	public void skipExchangeListener() {
		CardView.exitActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int tempForNumberOfCardsPlayerHolds = (game.getCurrentPlayer().getCards()).size();
				if(tempForNumberOfCardsPlayerHolds >= 5) {
					JOptionPane.showMessageDialog(null, "Cannot skip Exchange. Perform the Exchange operation!");
				}else {
					boardView.getFrameGameWindow().setEnabled(true);
					CardView.closeTheWindow();
				}
			}
		});
	}

	/**
	 * This function is going to set the BoardView in the game model.
	 */
	public void setBoardView() {
		game.setBoardView(boardView);
	}

	/**
	 * This method is used to load the saved game entered by user.
	 * It shows all the parameters which has been saved by user during game play.
	 * All objects are called by this method to get the saved results in the board view.
	 */
	public void loadSavedGame() {
		// print the saved game files in the directory
		print.listofSavedGamesinDirectory();
		print.consoleOut("Enter the name of Game which you want to load:");		
		String selectFileToLoadGame = userinput.nextLine();
		String gamesFilePath = ".\\src\\savedGames\\" + selectFileToLoadGame+ ".txt";
		
		// check if the saved game file exists or not
		File tempFile = new File(gamesFilePath);
		boolean exists = tempFile.exists();
		if (exists) {
			game = Game.readSavedObjectToloadGame(selectFileToLoadGame);
			MapModel map = game.getMap();
			cardView = new CardView(game);
			boardView=new BoardView();		
			game.addObserver(boardView);		
			game.addObserver(cardView);
			game.notifyObserverslocal(game);
			boardView.mapPath = game.getMap().getMapDir()+game.getMap().getMapName()+ ".bmp";
			boardView.gameWindowLoad();
			callListenerOnView();
			game.notifyObserverslocal(game);

		} else {
			print.consoleErr("File not found!!!. Please enter the coreect name of game file.");
			MainController mainMenu = new MainController();
			mainMenu.displaymainMenu();
		}
	}
}