/*
 * 
 */
package model;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import helper.*;


import java.util.Collections;
import java.util.Date;

import views.BoardView;
import views.CardView;

import views.FinishView;
import java.util.Observable;

// TODO: Auto-generated Javadoc
/**
 * Game model contains the class to create a model for the game. 
 * It is bounded with the Game Controller and the Board View.
 * 
 * @author Jaiganesh
 * @author Md Hasibul Huq
 * @author Gargi sharma
 * @version 1.0.0
 */

public class Game extends Observable implements Serializable {

	private static final long serialVersionUID = 42L; 
	/** The map model. */
	private MapModel mapModel;

	/** The game phase. */
	private GamePhase gamePhase;

	/** The current player id. */
	private int currentPlayerId;

	/** The MINIMUM REINFORCEMEN plAYERS. */
	private int MINIMUM_REINFORCEMENT_PlAYERS = 3;

	/** The initial source country. */
	private String initialSourceCountry;

	/** The print. */
	PrintConsoleAndUserInput print = new PrintConsoleAndUserInput();

	/** The player list. */
	private ArrayList<Player> playerList = new ArrayList<Player>();

	/** The connected own countries. */
	private ArrayList<String> connectedOwnCountries = new ArrayList<String>();

	/** The player country. */
	HashMap<Player, ArrayList<Country>> playerCountry = new HashMap<>();

	/** The Risk Cards. */
	private ArrayList<Card> riskCards = new ArrayList<>();

	/** The boardview. */
	private BoardView boardview;

	/** The Game Mode. */
	private GameMode gameMode;

	/** The game phase details. */
	private ArrayList<String> gamePhaseDetails = new ArrayList<>();

	/** The exchange number. */
	private Integer armiesAfterExchange= 5;

	public boolean dominationViewOn = false;
	private int maxTurnsForTournament;
	
	/** The CardView*/
	CardView cardview = new CardView(this);

	public Player winner = null;
	public boolean draw = false;

	/**
	 * Instantiates a new game.(Initializes the map and the game phase of the game.)
	 * @param map the map
	 */
	public Game(MapModel map) {
		super();
		this.mapModel = map;
		this.setGamePhase(GamePhase.Startup);
	}

	// ---------------------- Functions called by the initializeGame() from the GameController.
	/**
	 * This method adds the player to the game's player list.
	 * @param player get the player
	 */
	public void addPlayer(Player player) {
		this.playerList.add(player);
	}

	/**
	 * This method initializes the Game.
	 * It assigns the initial armies to the player.
	 * It randomly assigns the countries to the players.
	 */
	public void startGame() {
		this.addObserver(cardview);
		
		//Assigning the Initial armies.
		for(int i=0; i<playerList.size(); i++){
			playerList.get(i).setNumberOfInitialArmies(InitialPlayerArmy.getInitialArmyCount(playerList.size()));
			System.out.println("Player ID: "+playerList.get(i).getPlayerId()+" Player Name: "+playerList.get(i).getPlayerName()+" Player's Army: "+playerList.get(i).getNumberOfInitialArmies()+" Player's Color"+playerList.get(i).getColor());
			playerList.get(i).setConcuredContinents(mapModel.getContinentList());
			playerList.get(i).addPlayerList(playerList);
			gamePhaseDetails.add("Player ID = " +playerList.get(i).getPlayerId());
			gamePhaseDetails.add("Player Name = " +playerList.get(i).getPlayerName());
			gamePhaseDetails.add("Player's Army =  " +playerList.get(i).getNumberOfInitialArmies());
			gamePhaseDetails.add("Player's Color = " +playerList.get(i).getColor());
		}

		int playersCount = playerList.size();
		System.out.println("Player Count:"+playersCount);
		int countriescount = mapModel.getCountryList().size();
		
		int playersid = 0;
		ArrayList<Integer> randomNumbers = new ArrayList<>();
		for(int i=0; i<countriescount; i++){
			randomNumbers.add(i);
		}
		Collections.shuffle(randomNumbers, new Random());

		for(int i=0; i<countriescount ; i++){
			if (playersid == playersCount){
				playersid = 0;
			}

			Country assignCountry = mapModel.getCountryList().get(randomNumbers.get(i));
			assignPlayerCountry(playerList.get(playersid),assignCountry);
			assignUnassigned(playerList.get(playersid),assignCountry);
			playerList.get(playersid).assignCountryToPlayer(assignCountry);
			gamePhaseDetails.add(assignCountry.getCountryName()+" added for the player: "+playersid);
			playersid++;
		}

		for (Map.Entry<Player, ArrayList<Country>> entry : playerCountry.entrySet()){
			Player key = entry.getKey();
			ArrayList<Country> value = entry.getValue();
			System.out.println("\n"+key.getPlayerName()+" countries: \n");
			for(Country aString : value){
				System.out.println(aString.getCountryName());
			}
		}
		notifyObserverslocal(this);
	}

	/**
	 * This method assigns the player to the corresponding country.
	 * @param player player
	 * @param country country
	 */
	public void assignPlayerCountry(Player player, Country country){
		if(playerCountry.containsKey(player)){
			playerCountry.get(player).add(country);
		}
		else{
			ArrayList<Country> assignCountry = new ArrayList<>();
			assignCountry.add(country);
			playerCountry.put(player, assignCountry);
		}
		country.setCountryColor(player.getColor());
		country.setPlayerId(player.getPlayerId());
	}

	// ---------------- Functions called by numberOfArmiesClickListener() from the GameController.
	/**
	 * This method adds armies to the country based on the startup or reinforcement game phase.
	 * @param countryName name of country
	 */
	public void addingCountryArmy(String countryName){
		if(gamePhase == gamePhase.Attack || gamePhase == gamePhase.Fortification){
			print.consoleOut("Armies can't be added during Attack or Fortification Phase.");
			return;
		}
		if(gamePhase == gamePhase.Startup) {
			Boolean added = addingStartupCountryArmy(countryName);
			if(added){
				setupNextPlayerTurn();
			}
		}else if (gamePhase == gamePhase.Reinforcement){
			addingReinforcementCountryArmy(countryName);
		}
		updateGame();
		notifyObserverslocal(this);
		if(gameMode == GameMode.SingleGameMode){
			initializeAutoSequence();
		}
	}

	/**
	 * This method add armies to the country during the startup phase and returns true when successful.
	 * @param countryName name of country
	 * @return true if valid phase otherwise false
	 */
	public boolean addingStartupCountryArmy(String countryName){
		if(this.gamePhase != gamePhase.Startup){
			print.consoleOut("This is not a valid Phase");
			return false;
		}

		// Get player information
		Player player = this.getCurrentPlayer();

		if(player == null){
			print.consoleOut("Player ID = " +currentPlayerId+"does not exist.");
			return false;
		}
		
		if(player.getNumberOfInitialArmies() == 0){
			print.consoleOut("Player Name = '" +player.getPlayerName()+"' It doesn't have any Armies.");
			this.setupNextPlayerTurn();
			return false;
		}
		Country country = playerCountry.get(player).stream()
				.filter(c -> c.getCountryName().equalsIgnoreCase(countryName)).findAny().orElse(null);
		if (country == null) {
			print.consoleOut("Country name -  " + countryName + " does not exist!");
			return false;
		}
		assignUnassigned(player,country);
		return true;
	}

	/**
	 * This method adds armies to the country during the reinforcement phase and returns when successful.
	 * @param countryName name of country
	 * @return true
	 */
	public boolean addingReinforcementCountryArmy(String countryName){

		if(this.gamePhase != gamePhase.Reinforcement){
			print.consoleOut("Not a Valid Phase");
			return false;
		}
		Player player = this.getCurrentPlayer();
		Country country = playerCountry.get(player).stream()
				.filter(c -> c.getCountryName().equalsIgnoreCase(countryName)).findAny().orElse(null);

		player.setReinforceCountry(country);
		boolean success = player.reinforcementPhase();

		if(success){
			gamePhaseDetails.add(player.getPlayerName()+ " added army to the country "+ country.getCountryName());
		}
		notifyObserverslocal(this);
		return true;
	}

	/**
	 * This method initializes the reinforcement phase for each player by adding corresponding number of armies.
	 */
	public void reinforcementPhaseSetup() {
		gamePhaseDetails.removeAll(gamePhaseDetails);
		Player player = getCurrentPlayer();
		if(player.getCards().size()>2) {

			cardview.Exchange();
			this.getBoardView().getFrameGameWindow().setEnabled(false);
		}
		gamePhaseDetails.add("card:"+player.getCards().size());
		System.out.println("card:"+player.getCards().size());
		int countriescount = player.calculationForNumberOfArmiesInReinforcement(playerCountry,mapModel.getContinentList());
		gamePhaseDetails.add("Calculating reinforcement");
		countriescount = countriescount < MINIMUM_REINFORCEMENT_PlAYERS ? MINIMUM_REINFORCEMENT_PlAYERS : countriescount;
		System.out.println("Countries Count:" + countriescount);
		gamePhaseDetails.add("Number of armies get from reinforcement:"+countriescount);
		player.setNumberOfReinforcedArmies(countriescount);
	}

	/**
	 * This method updates the game phase of the game during the end of every Startup, Reinforcement and Fortification phase.
	 */
	public void updateGame() {
		if (this.getGamePhase() == gamePhase.Startup) {
			long pendingPlayersCount = playerList.stream().filter(p -> p.getNumberOfInitialArmies() > 0).count();
			System.out.println(pendingPlayersCount);
			
			if (pendingPlayersCount == 0) {
				this.setGamePhase(gamePhase.Reinforcement);
				currentPlayerId = 0;
				reinforcementPhaseSetup();
			}
		}else if (this.getGamePhase() == gamePhase.Reinforcement) {
			if (getCurrentPlayer().getNumberOfReinforcedArmies() == 0) {
				gamePhaseDetails.removeAll(gamePhaseDetails);
				if(checkAttackPossible()) {
					this.setGamePhase(gamePhase.Attack);	
				}else {
					this.setGamePhase(gamePhase.Fortification);
				}
				notifyObserverslocal(this);
			}
		}
		else if (this.getGamePhase() ==  gamePhase.Attack) {
			if(getCurrentPlayer().getPlayerStrategy().isHuman()) {
				gamePhaseDetails.removeAll(gamePhaseDetails);
			}
			this.setGamePhase(gamePhase.Fortification);
			notifyObserverslocal(this);
			
		}else if (this.getGamePhase() == gamePhase.Fortification) {
			if(getCurrentPlayer().getPlayerStrategy().isHuman()) {
				gamePhaseDetails.removeAll(gamePhaseDetails);
			}
			this.setGamePhase(gamePhase.Reinforcement);
			notifyObserverslocal(this);
		}
	}

	//--------------- Functions called by addSourceCountriesListener() from the GameController.
	/**
	 * This method returns the neighboring connected countries of a specific country.
	 * @param source source countries
	 * @return finalCountries countries
	 */
	public ArrayList<String> getNeighbouringCountries(String source) {

		System.out.println("Source Country Name :" + source);
		gamePhaseDetails.add("Source Country Name :" + source);
		System.out.print(connectedOwnCountries.toString());
		Player currentPlayer = this.getCurrentPlayer();
		initialSourceCountry = source;

		ArrayList<String> countriesAssignedToPlayer = new ArrayList<String>();
		ArrayList<String> finalCOuntries = new ArrayList<String>();

		ArrayList<Country> countryList = playerCountry.get(currentPlayer);
		ArrayList<String> neighborCountriesName = new ArrayList<>();

		for (Country country : countryList) {
			String countryName = country.getCountryName();
			countriesAssignedToPlayer.add(countryName);
			if (country.getCountryName().equals(source)) {
				for( Country country1 :  country.getNeighboursOfCountry()){
					neighborCountriesName.add(country1.getCountryName());
				}
			}
		}

		Iterator<String> myIterator = neighborCountriesName.iterator();
		while (myIterator.hasNext()) {
			String country = myIterator.next();
			if (!countriesAssignedToPlayer.contains(country)){
				myIterator.remove();
			}
		}

		// check if there are neighbour countries
		if(neighborCountriesName != null) {
			neighborCountriesName.removeAll(connectedOwnCountries);
			connectedOwnCountries.addAll(neighborCountriesName);
		}

		Iterator<String> rec = neighborCountriesName.iterator();
		while (rec.hasNext()) {
			String country = rec.next();
			getConnectedCountries(country, countryList);
		}

		System.out.println("1. Neighbouring Countries: "+neighborCountriesName.toString());
		System.out.println("1. Player's Countries: "+countriesAssignedToPlayer.toString());
		finalCOuntries.addAll(connectedOwnCountries);
		connectedOwnCountries.clear();
		gamePhaseDetails.add("Connected Countries: "+ finalCOuntries.toString());
		return finalCOuntries;
	}


	/**
	 * This method recursively explores all the nodes connected to a country and returns the neighboring countries.
	 * @param source source countries
	 * @param countryList list of countries
	 *
	 */
	public void getConnectedCountries(String source, ArrayList<Country> countryList) {
		System.out.println("Source Country Name :" + source);

		ArrayList<String> countriesAssignedToPlayer = new ArrayList<String>();
		ArrayList<String> neighborCountriesName = new ArrayList<String>();

		for (Country country : countryList) {
			String countryName = country.getCountryName();
			countriesAssignedToPlayer.add(countryName);
			if (country.getCountryName().equals(source)) {
				for( Country country1 :  country.getNeighboursOfCountry()){
					neighborCountriesName.add(country1.getCountryName());
				}
			}
		}

		Iterator<String> myIterator = neighborCountriesName.iterator();
		while (myIterator.hasNext()) {
			String country = myIterator.next();
			if (!countriesAssignedToPlayer.contains(country)||country.equals(initialSourceCountry)){
				myIterator.remove();
			}
		}

		if(neighborCountriesName != null) {
			neighborCountriesName.removeAll(connectedOwnCountries);
			connectedOwnCountries.addAll(neighborCountriesName);
		}

		Iterator<String> rec = neighborCountriesName.iterator();
		while (rec.hasNext()) {
			String country = rec.next();
			getConnectedCountries(country, countryList);
		}

		System.out.println("1. Neighbouring Countries: "+neighborCountriesName.toString());
		System.out.println("1. Player's Countries: "+countriesAssignedToPlayer.toString());

	}

	/**
	 * This method returns the number of armies assigned to a specific country.
	 * @param sourceCountryName source country names
	 * @return noOfArmies number of armies
	 */
	public int getArmiesAssignedToCountry(String sourceCountryName) {
		Player currentPlayer = this.getCurrentPlayer();
		int numberOfArmies = 0;

		for (Country country : playerCountry.get(currentPlayer)) {
			if (country.getCountryName().equals(sourceCountryName)) {
				numberOfArmies = country.getnoOfArmies();
			}
		}
		return numberOfArmies;
	}

	// ----------------- Functions called by addMoveArmyButtonListener() from GameController.
	/**
	 * This method checks whether the source and destination countries belongs to the player and moves the armies from source to destination.
	 * @param source  source as string
	 * @param destination destination countries as string
	 * @param armies count of armies as int
	 * @return true
	 */
	public boolean fortificationPhase(String source, String destination, int armies){
		Player player = getCurrentPlayer();
		gamePhaseDetails.remove(gamePhaseDetails);
		Country sourceCountry = playerCountry.get(player).stream()
				.filter(c -> c.getCountryName().equalsIgnoreCase(source)).findAny().orElse(null);

		Country destinationCountry = playerCountry.get(player).stream()
				.filter(c -> c.getCountryName().equalsIgnoreCase(destination)).findAny().orElse(null);


		player.setFortifySourceCountry(sourceCountry);
		player.setFortifyDestinationCountry(destinationCountry);
		player.setFortifyArmies(armies);

		boolean success = player.fortificationPhase();

		if(success){
			gamePhaseDetails.add("Moving "+armies+" armies from " +  source+" to "+ destination);
		}
		if(player.getIsConqured()){
			System.out.println("Conquered");
			Card riskCard = getRiskCardFromDeck();

			if(riskCard == null){
				System.out.println("No Cards Available Right Now.");
			} else {
				player.addCard(riskCard);
			}

			player.setIsConquered(false);

		}
		notifyObserverslocal(this);
		this.setupNextPlayerTurn();
		setGamePhase(gamePhase.Reinforcement);
		reinforcementPhaseSetup();
		notifyObserverslocal(this);
		if(this.gameMode == GameMode.SingleGameMode){
			initializeAutoSequence();
		}
		return true;
	}

	/**
	 * This function skip the fortification phase.
	 */
	public void skipFortification() {
		Player player = getCurrentPlayer();
		if(player.getIsConqured()){
			System.out.println("Conqured");
			Card riskCard = getRiskCardFromDeck();

			if(riskCard == null){
				System.out.println("No Cards Available Right Now.");
			} else {
				player.addCard(riskCard);
			}
			player.setIsConquered(false);
		}

		this.setupNextPlayerTurn();
		setGamePhase(gamePhase.Reinforcement);
		reinforcementPhaseSetup();
		notifyObserverslocal(this);
		if(this.gameMode == GameMode.SingleGameMode){
			initializeAutoSequence();
		}
	}

	/**
	 * This function initialises the Risk Cards during the startup of the game.
	 */

	public void initializeRiskCards(){

		int t=0;
		riskCards.clear();
		int countriesCount = mapModel.getCountryList().size();
		for (int i = 0; i<countriesCount; i++) {
			if (t==0) {
				riskCards.add(Card.Infantry);
			} else if (t==1) {
				riskCards.add(Card.Cavalry);
			} else if (t==2) {
				riskCards.add(Card.Artillery);
			}
			t++;

			if (t == 3) {
				t=0;
			}
		}
		Collections.shuffle(riskCards, new Random());
	}

	/**
	 * This function returns a risk card from the deck when called.
	 * @return riskCard
	 */
	public Card getRiskCardFromDeck(){
		System.out.println(riskCards.toString());
		if(riskCards.size() > 0){
			Card riskCard = riskCards.get(0);
			riskCards.remove(0);
			return riskCard;
		}
		return null;
	}

	/**
	 * This function adds the risk card back to the deck when called.
	 * @param riskCard Card
	 */
	public void addRiskCardToDeck(Card riskCard){
		if(riskCards.size()>0){
			riskCards.add(riskCard);
		}
	}


	/**
	 * This function performs the exchange operations for the risk cards by assigning armies to the player.
	 * @param selectedRiskCards arraylist which contains selected risk cards
	 * @return true if cards exchanges otherwise false
	 */
	public boolean exchangeRiskCards(ArrayList<String> selectedRiskCards){

		if(selectedRiskCards.size() == 3){

			Card firstCard = getCurrentPlayer().getCards().stream().filter( x -> x == Card.valueOf(selectedRiskCards.get(0))).findFirst().orElse(null);

			Card secondCard = getCurrentPlayer().getCards().stream().filter( x -> x == Card.valueOf(selectedRiskCards.get(1))).findFirst().orElse(null);

			Card thirdCard = getCurrentPlayer().getCards().stream().filter( x -> x == Card.valueOf(selectedRiskCards.get(2))).findFirst().orElse(null);

			if(firstCard == null || secondCard == null || thirdCard == null){
				System.out.println("Some Cards doesn't belong to the player.");
			}

			boolean sameRiskCards = (firstCard == secondCard) && (secondCard == thirdCard);
			boolean differentRiskCards = (firstCard != secondCard) && (secondCard != thirdCard) && (firstCard != thirdCard);
			boolean sameAndDifferent = (firstCard != secondCard) || (secondCard != thirdCard) || (firstCard != thirdCard);
			if(sameRiskCards || differentRiskCards || sameAndDifferent){

				getCurrentPlayer().getCards().remove(firstCard);
				getCurrentPlayer().getCards().remove(secondCard);
				getCurrentPlayer().getCards().remove(thirdCard);
				getCurrentPlayer().setInitialArmiesafterExchange(armiesAfterExchange);
				armiesAfterExchange= armiesAfterExchange + 5;
				System.out.println(armiesAfterExchange);
				addRiskCardToDeck(firstCard);
				addRiskCardToDeck(secondCard);
				addRiskCardToDeck(thirdCard);
				notifyObserverslocal(this);
				return true;
			} else { System.out.println("Choose the correct combination of the cards."); }
		} else { System.out.println("Choose at least three cards for the exchange."); }
		notifyObserverslocal(this);
		return false;
	}

	//Functions called by other functions within the Game model.

	//Getter and Setter functions of Map.

	/**
	 * This function is used to get map.
	 * @return mapModel
	 */
	public MapModel getMap() {
		return mapModel;
	}

	/**
	 *  This is used to set map.
	 * @param map map
	 */
	public void setMap(MapModel map) {
		this.mapModel = map;
	}

	/**
	 *This is used to get game phase.
	 * @return gamePhase getGamePhase
	 */
	public GamePhase getGamePhase() {
		return gamePhase;
	}


	/**
	 * This is used to set game phase.
	 * @param gamePhase Game phase
	 */
	public void setGamePhase(GamePhase gamePhase) {
		this.gamePhase = gamePhase;
	}


	/**
	 * Getter function for all the Player.
	 * @return playerList list of players
	 */
	public ArrayList<Player> getAllPlayers() {
		return playerList;
	}


	/**
	 * Getter function for Current Player Id.
	 * @return currentPlayerId, current id of player
	 */
	public int getCurrentPlayerId() {
		return currentPlayerId;
	}


	/**
	 * Getter function for Current Player.
	 * @return currentPlayer current player
	 */
	public Player getCurrentPlayer() {
		Player currentPlayer = playerList.get(currentPlayerId);
		return currentPlayer;
	}


	/**
	 * Getter function for getting all the Current Player Countries using current player's ID.
	 * @return playerCountry arraylist
	 */
	public ArrayList<Country> getCurrentPlayerCountries() {
		Player currentPlayer = playerList.get(currentPlayerId);
		return playerCountry.get(currentPlayer);
	}


	/**
	 * Getter function for getting all the Current Player Countries using current player's object.
	 * @param currentPlayer current player
	 * @return playerCountry player country
	 */
	public ArrayList<Country> getPlayersCountry(Player currentPlayer) {
		return playerCountry.get(currentPlayer);
	}


	/**
	 * Function the sets the next player's turn.
	 */
	public void setupNextPlayerTurn(){
		print.consoleOut("Current Player ID:"+currentPlayerId);
		currentPlayerId++;
		if(currentPlayerId==playerList.size()){
			currentPlayerId = 0;
		}
		if(playerCountry.get(getCurrentPlayer()).size()==0) {
			setupNextPlayerTurn();
		}
	}


	/**
	 * Function the returns the armies of the country of the current player.	 *
	 * @param countryName anme of the country
	 */
	public void getCountryArmies(String countryName) {
		int armiesnumber = 0;
		Player player = this.getCurrentPlayer();
		for(Country country: playerCountry.get(player)){
			if(country.getCountryName().equals(countryName)){
				armiesnumber = country.getnoOfArmies();
			}
		}
	}


	/**
	 * Function that moves an army from the player's initial army to the country's army.
	 * @param player player
	 * @param country country
	 */
	public void assignUnassigned(Player player, Country country){
		player.decreasenumberOfInitialArmies();
		country.increaseArmyCount();
	}


	/**
	 * Function that moves an army from the player's reinforcement army to the country's army.
	 * @param player player
	 * @param country country
	 */
	public void assignReinforcement(Player player, Country country){
		player.decreaseReinforcementArmy();
		country.increaseArmyCount();
	}


	/**
	 * This is the method that notifies all the observers connected to the observable.
	 * @param game game view
	 */
	public void notifyObserverslocal(Game game){
		setChanged();
		notifyObservers(this);
	}

	/**
	 * Returns number of dices for attacking / defending country.
	 * @param countryName the country name
	 * @param playerStatus the player status
	 * @return Integer
	 */
	public int getMaximumDices(String countryName, String playerStatus) {
		int allowableAttackingArmies = 0;
		if (this.gamePhase ==GamePhase.Attack) {
			// Will also add validation if the attacker is assigned to player or not

			Country nameOfCountry = mapModel.getCountryFromName(countryName);

			if (nameOfCountry != null) {
				allowableAttackingArmies = getCurrentPlayer().getNumberDices(nameOfCountry, playerStatus);
			}
		}
		return allowableAttackingArmies;
	}

	/**
	 * Returns allowable dices for attacking country.
	 * @param countryName the country name
	 * @return Integer
	 */
	public ArrayList<String> getOthersNeighbouringCountriesOnly(String countryName) {
		ArrayList<String> allowableAttackingArmies = new ArrayList<String>();

		Country nameOfCountry = mapModel.getCountryFromName(countryName);
		Player currentPlayer = this.getCurrentPlayer();
		ArrayList<Country> countryList = playerCountry.get(currentPlayer);

		if (nameOfCountry != null) {
			allowableAttackingArmies = nameOfCountry.getStringsOfNeighbours();
			for (Country country : countryList) {
				String countryName1 = country.getCountryName();
				allowableAttackingArmies.remove(countryName1);
			}

		}
		return allowableAttackingArmies;
	}



	/**
	 * Method for  attack phase where attack will handled.
	 * @param attackerCountry the attacker country
	 * @param defenderCountry the defender country
	 * @param attackerDiceCount the attacker dice count
	 * @param defendergDiceCount the defender dice count
	 * @return true, if attack done
	 */
	public Boolean attackPhaseActions(String attackerCountry, String defenderCountry, int attackerDiceCount, int defendergDiceCount) {

		Country attCountry = mapModel.getCountryFromName(attackerCountry);
		Country defCountry = mapModel.getCountryFromName(defenderCountry);
		gamePhaseDetails.add(attackerCountry+" is attacking the "+ defenderCountry);
		if (attCountry == null || defCountry == null) {
			return false;
		}

		if (defCountry.getnoOfArmies() < defendergDiceCount) {
			gamePhaseDetails.add("Defender doesn't have sufficiant armies");
			return false;
		}
		Player defenderPlayer = playerList.stream().filter(p -> p.getPlayerId()==defCountry.getPlayerId())
				.findAny().orElse(null);

		if (defenderPlayer == null) {
			return false;
		}

		Player player = getCurrentPlayer();

		player.setAttackDefenderPlayer(defenderPlayer);
		player.setAttackAttackerCountry(attCountry);
		player.setAttackDefenderCountry(defCountry);
		player.setAttackAttackerDiceCount(attackerDiceCount);
		player.setAttackDefenderDiceCount(defendergDiceCount);


		player.setAttackPlayerCountry(playerCountry);
		player.setAttackGamePhaseDetails(gamePhaseDetails);

		boolean success = player.attackPhase();

		if(success){
			System.out.println("Success");
		}

		//playerCountry;
		if (isMapConcured()) {
			FinishView finish = new FinishView();
			finish.callWinner(getCurrentPlayer().getPlayerName());
			System.out.println("Congratulation!"+this.getCurrentPlayer().getPlayerName() + ": You Win.");
			gamePhaseDetails.add("Congratulation!"+this.getCurrentPlayer().getPlayerName() + ": You Win.");
		} else if (!checkAttackPossible()) {
			gamePhaseDetails.add("Attack not possible.");
			System.out.println("Attack");
			updateGame();
		}
		getCurrentPlayer().setConcuredContinents(mapModel.getContinentList());
		notifyObserverslocal(this);
		if(defCountry.getPlayerId()==attCountry.getPlayerId()&& attCountry.getnoOfArmies()>1) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if is map concured.
	 * @return true, if is map concured
	 */
	public boolean isMapConcured() {
		if(mapModel.getCountryList().size() == playerCountry.get(this.getCurrentPlayer()).size()) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * method to get countries from the attackers country where number of armies are getter than 1.
	 * @return attackerCountry arraylist of attacker country
	 */
	public ArrayList<String> getAttackPossibleCountries() {
		ArrayList<String> attackerCountry = new ArrayList<String>();
		ArrayList<Country> countryList = this.getCurrentPlayerCountries();
		for (int i = 0; i < countryList.size(); i++) {
			Country tempcname = countryList.get(i);
			if (tempcname.getnoOfArmies()>1) {
				attackerCountry.add(tempcname.getCountryName());
			}
		}
		return attackerCountry;

	}

	/**
	 * This method is to check current user can attack or not.
	 * @return boolean
	 */
	public boolean checkAttackPossible() {
		ArrayList<String> attackerPossibleCountries = getAttackPossibleCountries();
		if (attackerPossibleCountries.size() < 1) {
			return false;
		}else {
			for (String countryName : attackerPossibleCountries) {
				ArrayList<String> neighborCountries = getOthersNeighbouringCountriesOnly(countryName);

				if (neighborCountries.size() > 0) {
					return true;
				}
			}
			return false;
		}
	}


	/**
	 * Method for performing All out attack phase.
	 * @param attackerCountry the attacker country
	 * @param defenderCountry the defender country
	 * @return true, if attack phase out
	 */
	public Boolean attackAllOutPhase(String attackerCountry, String defenderCountry) {

		Country attCountry = mapModel.getCountryFromName(attackerCountry);
		Country defCountry = mapModel.getCountryFromName(defenderCountry);


		if (attCountry == null || defCountry == null) {
			return false;
		}

		while ((attCountry.getPlayerId()!=defCountry.getPlayerId()) && attCountry.getnoOfArmies() > 1) {
			int attackerDiceCount = this.getMaximumDices(attackerCountry, "Attacker");
			int defenderDiceCount = this.getMaximumDices(defenderCountry, "Defender");

			attackPhaseActions(attackerCountry, defenderCountry, attackerDiceCount, defenderDiceCount);
		}
		notifyObserverslocal(this);
		if(defCountry.getPlayerId()==attCountry.getPlayerId()&& attCountry.getnoOfArmies()>1) {
			return true;
		}
		return false;
	}

	/**
	 * move Armies after attack.
	 * @param attackersCountry Attacker country
	 * @param atteckersNewCountry Attacker new country
	 * @param attackerMoveArmies Attacker move armies
	 */
	public void moveArmies(Country attackersCountry, Country atteckersNewCountry, int attackerMoveArmies) {
		if(attackersCountry.getnoOfArmies()>attackerMoveArmies) {
			attackersCountry.decreaseArmyCount(attackerMoveArmies);
			atteckersNewCountry.increaseArmyCount(attackerMoveArmies);
		}else {
			System.out.println("Move Armies is not possible");
			gamePhaseDetails.add("Move Armies is not possible. No sufficient armies");
		}
		notifyObserverslocal(this);
	}



	/**
	 * Gets the percentage of map controlled by every player.
	 * @return the percentage of map controlled by every player
	 */
	public HashMap<Integer, Float> getPercentageOfMapControlledByEveryPlayer() {
		float totalNumberOfCountries = 0;
		ArrayList<Continent> allContinents = mapModel.getContinentList();
		for (Continent continent : allContinents) {
			ArrayList<Country> country = continent.getCountryList();
			totalNumberOfCountries = totalNumberOfCountries + country.size();
		}

		// store the percentage in a hashmap with the player id.
		HashMap<Integer, Float> mapPercentageStoredInMap = new HashMap<Integer, Float>();
		for (Player player : playerList) {

			// get size of player country list
			int playerCountries = playerCountry.get(player).size();

			// find percentage
			float percentage = (playerCountries / totalNumberOfCountries) * 100;

			// get player id and percentage and then put in map
			int playerId = player.getPlayerId();
			mapPercentageStoredInMap.put(playerId, percentage);
		}
		return mapPercentageStoredInMap;
	}

	/**
	 * Get the number of continents and their name by each player
	 * @return hashMap for a player and continent
	 */
	public HashMap<Integer, String> getContinentsControlledByEachPlayer() {
		HashMap<Integer, String> continentsOfPlayer = new HashMap<Integer, String>();
		ArrayList<String> nameOfTheContinent = new ArrayList<>();
		String numberAndName= null;

		for (Player player : this.playerList) {
			int numberOfContinents = player.getConquerdContinents().size();
			for (Continent continentName: player.getConquerdContinents()) {
				nameOfTheContinent.add(continentName.getContinentName());
				numberAndName = "(" + numberOfContinents + "): "+nameOfTheContinent;
			}
			continentsOfPlayer.put(player.getPlayerId(), numberAndName);
			numberAndName="";
			nameOfTheContinent.clear();
		}
		return continentsOfPlayer;
	}

	/**
	 * Gets list of players.
	 * @param countriesListOfPlayer Countries list of players
	 * @return countriesListString Countries list
	 */
	public ArrayList<String> countryListStringOfPlayer(ArrayList<Country> countriesListOfPlayer) {
		ArrayList<String> countriesListString = new ArrayList<>();
		for(Country countryForAdding : countriesListOfPlayer){
			countriesListString.add(countryForAdding.getCountryName());
		}
		return countriesListString;
	}

	/**
	 * This method is used to get the number of armies for each player.
	 * @return numberOfArmies Number of armies
	 */
	public HashMap<Integer, Integer> getNumberOfArmiesForEachPlayer() {
		HashMap<Integer, Integer> numberOfArmies = new HashMap<Integer, Integer>();
		for (Player player : this.playerList) {
			if(player.getAssignedListOfCountries().size()==0) {
				numberOfArmies.put(player.getPlayerId(), 0);
			}
			for (Country country : player.getAssignedListOfCountries()) {
				int totalArmies = country.getnoOfArmies();
				if(numberOfArmies.containsKey(player.getPlayerId()))
				{
					totalArmies += numberOfArmies.get(player.getPlayerId());
				}
				numberOfArmies.put(player.getPlayerId(), totalArmies);
			}
		}
		return numberOfArmies;
	}

	/**
	 * Get all the players and countries.
	 * @return playerCountry Player country
	 */
	public HashMap<Player, ArrayList<Country>> playerandCountries(){
		return playerCountry;
	}

	/**
	 * get the board view.
	 * @return boardView
	 */
	public BoardView getBoardView() {
		return boardview;
	}

	/**
	 * the method is used to get the updated phase while the game is being played
	 * @return gamePhaseDetail
	 */
	public ArrayList<String> getGamePhaseDetails() {
		return gamePhaseDetails;
	}


	/**
	 * Get the board view of a game.
	 * @param viewOfBoard the board view
	 */
	public void setBoardView(BoardView viewOfBoard) {
		boardview  = viewOfBoard;
	}

	/**
	 * update the reinforcement value.
	 */
	public void updateReinforcementValue() {
		reinforcementPhaseSetup();
		notifyObserverslocal(this);
	}

	/**
	 * Checks if the Attacker and Defender operation is valid 
	 * @param attCountry attacker country
	 * @param defCountry defender country
	 * @param defendergDiceCount  dice count for defender country
	 * @return true otherwise false
	 */
	public boolean isAttackerDefenderValid(Country attCountry,Country  defCountry,int defendergDiceCount) {
		if (attCountry == null || defCountry == null) {
			return false;
		}

		if (defCountry.getnoOfArmies() < defendergDiceCount) {
			gamePhaseDetails.add("Defender doesn't have sufficiant armies");
			return false;
		}
		Player defenderPlayer = playerList.stream().filter(p -> p.getPlayerId()==defCountry.getPlayerId())
				.findAny().orElse(null);

		if (defenderPlayer == null) {
			return false;
		}
		return true;
	}

	/**
	 * Sets the game mode
	 * @param gameMode game mode
	 */
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	/**
	 * Automates the Current Phase
	 */
	public void automateCurrentPhase(){
		if(this.gamePhase == GamePhase.Startup){
			ArrayList<Country> countryList = getCurrentPlayer().getAssignedListOfCountries();
			int random = 0;
			if(countryList.isEmpty()){
				return;
			} else if (countryList.size() > 1){
				random = RandomNumber.getRandomNumberInRange(0, countryList.size()-1);
			}
			Country country = countryList.get(random);
			System.out.println("\n\n ***************Assigning the initial army to the player*************** \n\n");
			boolean success = addingStartupCountryArmy(country.getCountryName());

			//			notifyObserverslocal(this);
			if(success){
				setupNextPlayerTurn();
			}
			System.out.println("\n\n *************** Finish Assigning the initial army to the player*************** \n\n");

		} else if (this.gamePhase == GamePhase.Reinforcement) {
			System.out.println("\n\n ***************Performing Reinforcement for the player*************** \n\n");
			this.getCurrentPlayer().setAttackGamePhaseDetails(gamePhaseDetails);
			//this.getCurrentPlayer().setReinforceContinent(mapModel.getContinentList());
			this.getCurrentPlayer().setAttackPlayerCountry(playerCountry);
			boolean success = this.getCurrentPlayer().reinforcementPhase();
			gamePhaseDetails= this.getCurrentPlayer().getAttackGamePhaseDetails();
			if(success){
			}
			notifyObserverslocal(this);
			System.out.println("\n\n ***************Finish Performing Reinforcement for the player*************** \n\n");

		} else if (this.gamePhase == GamePhase.Attack){
			System.out.println("\n\n ***************Performing Attacking for the player*************** \n\n");
			getCurrentPlayer().setAttackPlayerCountry(playerCountry);
			getCurrentPlayer().setAttackGamePhaseDetails(gamePhaseDetails);
			boolean success = this.getCurrentPlayer().attackPhase();
			gamePhaseDetails= this.getCurrentPlayer().getAttackGamePhaseDetails();
			if(isMapConcured()){
				notifyObserverslocal(this);
				System.out.println("You Win");
				JOptionPane.showMessageDialog(null, "You Win!");
			}
			if(success){
			}
			notifyObserverslocal(this);
			System.out.println("\n\n ***************Finish Performing Attacking for the player*************** \n\n");

		} else if (this.gamePhase == GamePhase.Fortification){
			System.out.println("\n\n ***************Performing Fortification for the player*************** \n\n");
			this.getCurrentPlayer().setAttackGamePhaseDetails(gamePhaseDetails);
			this.getCurrentPlayer().setAttackPlayerCountry(playerCountry);
			this.getCurrentPlayer().setRiskCards(getRiskCardFromDeck());
			boolean success = this.getCurrentPlayer().fortificationPhase();
			gamePhaseDetails= this.getCurrentPlayer().getAttackGamePhaseDetails();
			notifyObserverslocal(this);
			if(success){
				setupNextPlayerTurn();
			}
			System.out.println("\n\n *************** Finish Performing Fortification for the player*************** \n\n");
			System.out.println("\n\n *************** Starting Reinforcemant calculations Performing Fortification for the player*************** \n\n");
			if(!getCurrentPlayer().getPlayerStrategy().isHuman()) {
				automateExchange();
				int reinforcementCal = this.getCurrentPlayer().calculationForNumberOfArmiesInReinforcement(playerCountry, mapModel.getContinentList());
				reinforcementCal = reinforcementCal < MINIMUM_REINFORCEMENT_PlAYERS ? MINIMUM_REINFORCEMENT_PlAYERS : reinforcementCal;		
				this.getCurrentPlayer().setNumberOfReinforcedArmies(reinforcementCal);
				System.out.println("\n\n *************** Finishing Reinforcemant calculations Performing Fortification for the player*************** \n\n");
			}else {
				reinforcementPhaseSetup();
			}
		}
	}


	/**
	 * This method is used to save game in a text file while playing
	 * @return filename of saved Game
	 */
	public String writeObjectToSaveMyGame() {
		
		// Get the current time and date
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy_hhmm");
		String saveGameFileWithTime = dateFormat.format(cal.getTime());

		// Folder path to save game
		String filePath = ".\\src\\savedGames\\" + saveGameFileWithTime+ ".txt";
		try {
			FileOutputStream fileOut = new FileOutputStream(filePath);			
			ObjectOutputStream ObjectOut = new ObjectOutputStream(fileOut);
			ObjectOut.writeObject(this);
			ObjectOut.close();
			fileOut.close();
			System.out.println("Game has been saved at this location: " +filePath);
		} catch (IOException ex) {
			System.out.println("IOException: " + ex.getMessage());
		}
		return saveGameFileWithTime;
	}

	/**
	 * This method is used to load game with reading the object
	 * @param gameNameEnteredByUser Get the games name from user to load the saved game
	 * @return filename of saved Game
	 */
	public static Game readSavedObjectToloadGame(String gameNameEnteredByUser) {	
		Game gameObj = null;
		String filePath = ".\\src\\savedGames\\" + gameNameEnteredByUser+ ".txt";
		try {			
			FileInputStream fileIn = new FileInputStream(filePath);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			gameObj = (Game) objectIn.readObject();
			objectIn.close();
			System.out.println("Game has been loaded. ");
			fileIn.close();
		} catch (IOException ex) {
			System.out.println("IOException: " + ex.getMessage());
		} catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException: " + ex.getMessage());
		}
		return gameObj;
	}

	/**
	 * This is used to initialize sequence
	 */
	public void initializeAutoSequence(){
		while (!getCurrentPlayer().getPlayerStrategy().isHuman() && !this.isMapConcured()){
			automateCurrentPhase();
			updateGame();
			notifyObserverslocal(this);
			try{Thread.sleep(500);} catch(Exception e){}
		}

	}


	/**
	 * This is used to exchange the cards automatically.
	 */
	public void automateExchange() {
		gamePhaseDetails.add(
				"Player: "+
						getCurrentPlayer().getPlayerName()+"Card:"+
						getCurrentPlayer().getCards().size());
		if(getCurrentPlayer().getCards().size()>2) {
			Card firstCard = getCurrentPlayer().getCards().get(0);
			Card secondCard= getCurrentPlayer().getCards().get(1);
			Card thirdCard= getCurrentPlayer().getCards().get(2);
			Map<Card, Integer> counts = new HashMap<Card, Integer>();

			for (Card str : getCurrentPlayer().getCards()) {
				if (counts.containsKey(str)) {
					counts.put(str, counts.get(str) + 1);
				} else {
					counts.put(str, 1);
				}
			}
			ArrayList<Card> diffCard = getCurrentPlayer().getCards().stream().distinct().collect(Collectors.toCollection(ArrayList::new));
			if(diffCard.size()>2) {
				firstCard = diffCard.get(0);
				secondCard = diffCard.get(1);
				thirdCard = diffCard.get(2);
			}
			else {
				for(Map.Entry<Card, Integer> entry : counts.entrySet()) {
					Card key = entry.getKey();
					int value = entry.getValue();
					if(value>2) {
						firstCard = key;
						secondCard = key;
						thirdCard = key;
						break;
					}
				}
			}


			boolean sameRiskCards = (firstCard == secondCard) && (secondCard == thirdCard);
			boolean differentRiskCards = (firstCard != secondCard) && (secondCard != thirdCard) && (firstCard != thirdCard);
			if(sameRiskCards || differentRiskCards){

				getCurrentPlayer().getCards().remove(firstCard);
				getCurrentPlayer().getCards().remove(secondCard);
				getCurrentPlayer().getCards().remove(thirdCard);
				getCurrentPlayer().setInitialArmiesafterExchange(armiesAfterExchange);
				armiesAfterExchange= armiesAfterExchange + 5;
				addRiskCardToDeck(firstCard);
				addRiskCardToDeck(secondCard);
				addRiskCardToDeck(thirdCard);
				notifyObserverslocal(this);

			}
		}
	}

	/**
	 *  Working of tournament mode displayed in console
	 */
	public void tournamentMode(){
		int turnsCounts = 0;

		print.consoleOut("******* The Tournament Mode Started To Play *******");

		// Loop until all armies are assigned for all players
		while (this.gamePhase == GamePhase.Startup) {
			// Randomly increase army for the country of player
			ArrayList<Country> countryList = getCurrentPlayer().getAssignedListOfCountries();
			int random = 0;
			if(countryList.isEmpty()){
				return;
			} else if (countryList.size() > 1){
				random = RandomNumber.getRandomNumberInRange(0, countryList.size()-1);
			}
			Country country = countryList.get(random);
			addingCountryArmy(country.getCountryName());

		}

		// Print status of players
		while (true) {
			System.out.println("\n\n ***************Performing Reinforcement for the player*************** \n\n");
			this.getCurrentPlayer().setAttackGamePhaseDetails(gamePhaseDetails);
			//this.getCurrentPlayer().setReinforceContinent(mapModel.getContinentList());
			this.getCurrentPlayer().setAttackPlayerCountry(playerCountry);
			boolean reinforcementSuccess = this.getCurrentPlayer().reinforcementPhase();
			gamePhaseDetails= this.getCurrentPlayer().getAttackGamePhaseDetails();
			if(reinforcementSuccess){
			}
			notifyObserverslocal(this);
			System.out.println("\n\n ***************Finish Performing Reinforcement for the player*************** \n\n");

			this.updateGame();

			System.out.println("\n\n ***************Performing Attacking for the player*************** \n\n");
			getCurrentPlayer().setAttackPlayerCountry(playerCountry);
			getCurrentPlayer().setAttackGamePhaseDetails(gamePhaseDetails);
			boolean attackSuccess = this.getCurrentPlayer().attackPhase();
			gamePhaseDetails= this.getCurrentPlayer().getAttackGamePhaseDetails();
			if(isMapConcured()){
				winner = this.getCurrentPlayer();
				notifyObserverslocal(this);
				System.out.println("You Win");
				break;
			}
			if(attackSuccess){
			}
			notifyObserverslocal(this);
			System.out.println("\n\n ***************Finish Performing Attacking for the player*************** \n\n");

			this.updateGame();

			this.getCurrentPlayer().setAttackGamePhaseDetails(gamePhaseDetails);
			this.getCurrentPlayer().setAttackPlayerCountry(playerCountry);
			boolean fortificationSuccess = this.getCurrentPlayer().fortificationPhase();
			if(fortificationSuccess){
				setupNextPlayerTurn();
			}
			System.out.println("\n\n *************** Finish Performing Fortification for the player*************** \n\n");
			System.out.println("\n\n *************** Starting Reinforcemant calculations Performing Fortification for the player*************** \n\n");
			automateExchange();
			int reinforcementCal = this.getCurrentPlayer().calculationForNumberOfArmiesInReinforcement(playerCountry, mapModel.getContinentList());
			reinforcementCal = reinforcementCal < MINIMUM_REINFORCEMENT_PlAYERS ? MINIMUM_REINFORCEMENT_PlAYERS : reinforcementCal;
			this.getCurrentPlayer().setNumberOfReinforcedArmies(reinforcementCal);
			this.updateGame();

			// Print status of players
			turnsCounts++;
			if (turnsCounts >= getMaxTurnsForTournament()) {

				this.setGamePhase(GamePhase.Draw);
				print.consoleOut("Tournament draw after " + turnsCounts + " turns");

				break;
			}
		}
		print.consoleOut("************************************************************************************");
		notifyObserverslocal(this);
	}

	/**
	 * Gets the winner.
	 * @return the winner
	 */
	public Player getWinner() {
		return winner;
	}


	/**
	 * Gets the max turns for tournament.
	 *
	 * @return the max turns for tournament
	 */
	public int getMaxTurnsForTournament() {
		return maxTurnsForTournament;
	}

	/**
	 * Sets the max turns for tournament.
	 *
	 * @param maxTurnsForTournament the new max turns for tournament
	 */
	public void setMaxTurnsForTournament(int maxTurnsForTournament) {
		this.maxTurnsForTournament = maxTurnsForTournament;
	}
}
