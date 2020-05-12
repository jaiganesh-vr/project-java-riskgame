package strategies;

import model.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import helper.Card;
import helper.RandomNumber;
import model.Country;

/**
 * This class is used for aggressive computer player strategy that focuses on attack (reinforces its strongest country, 
 * then always attack with it until it cannot attack anymore, then fortifies in order to 
 * maximize aggregation of forces in one country).
 * 
 * @author Gargi sharma
 * @version 1.0.0
 *
 */
public class Aggressive implements PlayerStrategy, Serializable {
	public String strategyName = "Aggressive";
	private Country attackerCountry;

	public String getStrategyName(){
		return strategyName;
	}

	@Override
	public boolean isHuman() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean reinforce(Player reInforcedPlayer) {

		// Get assigned list of countries with its size
		ArrayList<Country> assignedListOfCountries = reInforcedPlayer.getattackPlayerCountry().get(reInforcedPlayer);
		int sizeOfAssignedCountries = assignedListOfCountries.size();
		if (sizeOfAssignedCountries == 0) {
			return true;
		}

		// pass the assigned countries and army count to the  strongest countries method
		int armyCount = 0;
		attackerCountry = getStrongestCountries(assignedListOfCountries, armyCount);				

		// check if the attacker country is null( has no countries)
		if (attackerCountry == null) {
			System.out.println("Cannot find any attacking country");
			
		} else {

			// get attacker country name and armies
			String attackerCountryName = attackerCountry.getCountryName();
			int attackerNumberOfArmies = attackerCountry.getnoOfArmies();			
			System.out.println("Reinforcement army adding in Country name: " + attackerCountryName + "with army numbers = "+ attackerNumberOfArmies);
			reInforcedPlayer.getAttackGamePhaseDetails().add("Reinforcement army adding in Country name: " + attackerCountryName + "with army numbers = "+ attackerNumberOfArmies);
			// get player reinforced armies and set it to 0 the increase army count
			int reinforcedNumberOfArmies = reInforcedPlayer.getNumberOfReinforcedArmies();
			reInforcedPlayer.setNumberOfReinforcedArmies(0);
			attackerCountry.increaseArmyCount(reinforcedNumberOfArmies);

			System.out.println("Reinforcement army has been added  in country name: " + attackerCountryName + "with armies count = "+ attackerNumberOfArmies);
			reInforcedPlayer.getAttackGamePhaseDetails().add("Reinforcement army has been added  in country name: " + attackerCountryName + "with armies count = "+ attackerNumberOfArmies);
		}
		return true;
	}

	@Override
	public boolean attack(Player playerToAttack) {
		// TODO Auto-generated method stub

		// Get the player, country and armies information
		String playerName = playerToAttack.getPlayerName();
		String attackerCountryName = attackerCountry.getCountryName();
		int attackerNumberOfArmies = attackerCountry.getnoOfArmies();

		// Check if there is no country to attack
		if (attackerCountry == null) {
			System.out.println("There is no country to attack" );
			return false;
		}		
		System.out.println("Aggressive player name "+ playerName +" - attack - attacking from "
				+ attackerCountryName+ " with attacker armies count = "+ attackerNumberOfArmies);
		playerToAttack.getAttackGamePhaseDetails().add("Aggressive player name "+ playerName +" - attack - attacking from "
				+ attackerCountryName+ " with attacker armies count = "+ attackerNumberOfArmies);
		
		ArrayList<Country> neighbourCountriesForAttack = playerToAttack.getOthersNeighbouringCountriesOnlyObject(attackerCountry);
		int sizeForneighbourCountriesForAttack = neighbourCountriesForAttack.size();

		if (neighbourCountriesForAttack == null || sizeForneighbourCountriesForAttack == 0) {
			System.out.println("*** Sorry !! Not able to find any neighbouting country to attack from this Country ***");
			return false;
		}

		for (Country sourceCountry: neighbourCountriesForAttack) {
			System.out.println(attackerCountry.getCountryName() + "(" + attackerCountry.getnoOfArmies()
			+ ") is attacking to " + sourceCountry.getCountryName() + "(" + sourceCountry.getnoOfArmies() + ")");
			
			playerToAttack.getAttackGamePhaseDetails().add(attackerCountry.getCountryName() + "(" + attackerCountry.getnoOfArmies()
			+ ") is attacking to " + sourceCountry.getCountryName() + "(" + sourceCountry.getnoOfArmies() + ")");
			
			// Perform attack until country is acquired or the attacking country is lost
			int attackerPlayerId = playerToAttack.getPlayerId();

			while (sourceCountry.getPlayerId() != attackerPlayerId && sourceCountry.getnoOfArmies() > 0) {
				if (attackerCountry.getnoOfArmies() == 1) {
					break;
				}
				attackDetails(attackerCountry, sourceCountry, playerToAttack);
			}

			if (attackerCountry.getnoOfArmies() == 1) {
				// Cannot perform attack now
				break;
			}
		}
		return true;	


	}



	private boolean attackDetails(Country fromCountry, Country sourceCountry, Player player) {

		int attackerDiceCount = player.getNumberDices(fromCountry, "Attacker");

		int defenderDiceCount = player.getNumberDices(sourceCountry, "Defender");


		player.diceRoller(attackerDiceCount);
		Player defenderPlayer = player.getPlayer(sourceCountry.getPlayerId());
		defenderPlayer.diceRoller(defenderDiceCount);

		ArrayList<Integer> diceResults1 = player.getDiceResults();
		ArrayList<Integer> diceResults2 = defenderPlayer.getDiceResults();

		ArrayList<Integer> attackingDices = diceResults1;
		ArrayList<Integer> defendingDices = diceResults2;

		int totalNumberOfDice = attackingDices.size() < defendingDices.size() ? attackingDices.size()
				: defendingDices.size();

		for (int i = 0; i < totalNumberOfDice; i++) {

			int attackerDice = attackingDices.get(i);
			int defenderDice = defendingDices.get(i);

			System.out.print("Attacker dice - " + attackerDice + "  to Defender dice - " + defenderDice);
			player.getAttackGamePhaseDetails().add("Attacker dice - " + attackerDice + "  to Defender dice - " + defenderDice);
			if (attackerDice > defenderDice) {
				System.out.println("Attacker wins for dice " + (i + 1));
				player.getAttackGamePhaseDetails().add("Attacker wins for dice " + (i + 1));
				sourceCountry.decreaseArmyCount(1);
	
			} else {
				System.out.println("Defender wins for dice " + (i + 1));
				player.getAttackGamePhaseDetails().add("Defender wins for dice " + (i + 1));
				fromCountry.decreaseArmyCount(1);
			}

			if (fromCountry.getnoOfArmies() == 1) {
				System.out.println("Attacker not able to Attack ");
				player.getAttackGamePhaseDetails().add("Attacker not able to Attack");
				break;
			}
			if (sourceCountry.getnoOfArmies() == 0) {
				System.out.println("Defender lost all armies in " + (i + 1) + " dice roll");
				player.getAttackGamePhaseDetails().add("Defender lost all armies in " + (i + 1) + " dice roll");
				break;
			}

		}
		// Check if defending armies are 0 then acquire the country with cards
		if (sourceCountry.getnoOfArmies() == 0) {
			player.conquerCountryAutomate(defenderPlayer,sourceCountry,fromCountry);
			return true;
		}
		return false;
	}


	@Override
	public boolean fortify(Player playerToFortify) {

		// Get the player, country and armies information
		String playerName = playerToFortify.getPlayerName();

		// Get assigned list of countries with its size
		ArrayList<Country> assignedListOfCountries = playerToFortify.getattackPlayerCountry().get(playerToFortify);		

		Country destinationCountry = null;	
		Country sourceCountry = getStrongestCountries(assignedListOfCountries, 0);
		System.out.println("Source Country:\t"+sourceCountry.getCountryName());
		playerToFortify.getAttackGamePhaseDetails().add("Source Country:\t"+sourceCountry.getCountryName());
		if(sourceCountry==null) {
			return true;
		}

		ArrayList<Country> neighborCountries = playerToFortify.getNeighbouringCountries(sourceCountry);
		System.out.println(neighborCountries.size());
		if (neighborCountries != null && neighborCountries.size() > 0) {
			int destinationRandomIndex = RandomNumber.getRandomNumberInRange(0, neighborCountries.size() - 1);
			
			destinationCountry = neighborCountries.get(destinationRandomIndex);
			System.out.println(sourceCountry.getCountryName());
			System.out.println(destinationCountry.getCountryName());
			if (sourceCountry != null && destinationCountry != null) {
				if(sourceCountry.getnoOfArmies()>1) {
					System.out.println(sourceCountry.getCountryName());
					System.out.println(destinationCountry.getCountryName());
					playerToFortify.getAttackGamePhaseDetails().add("Destination Country:\t"+destinationCountry.getCountryName());
					playerToFortify.getAttackGamePhaseDetails().add("Performing fortification from "+sourceCountry.getCountryName()+" with armies "
							+sourceCountry.getnoOfArmies() +" to "+destinationCountry.getCountryName()+" with armies "+destinationCountry.getnoOfArmies());
					int armies = sourceCountry.getnoOfArmies()-1;
					sourceCountry.setnoOfArmies(sourceCountry.getnoOfArmies() - armies);
					destinationCountry.setnoOfArmies(destinationCountry.getnoOfArmies()+armies);
					playerToFortify.getAttackGamePhaseDetails().add("Finished fortification with destination country "
					+destinationCountry.getCountryName()+" ("	+destinationCountry.getnoOfArmies()+")"	);
				}

				return true;
			} else {
				System.out.println("Aggressive player " + playerName + " cannot find any country for fortification.");
				return true;

			}	
		}	
		
		if(playerToFortify.getIsConqured()){
			System.out.println("Conquered");
			playerToFortify.getAttackGamePhaseDetails().add("Conquered");
			Card riskCard = playerToFortify.getRiskCards();
			
			if(riskCard == null){
				System.out.println("No Cards Available Right Now.");
				playerToFortify.getAttackGamePhaseDetails().add("No Cards Available Right Now.");
			} else {
				playerToFortify.addCard(riskCard);
				playerToFortify.getAttackGamePhaseDetails().add("Card added"+ riskCard);

			}

			playerToFortify.setIsConquered(false);

		}
		return true;
	}




	/**
	 * This method is used to get the strongest countries from the list.
	 * @param assignedListOfCountries  ArrayList of assigned countries
	 * @param armyCount lower limit count of armies
	 * @return strongestCountry strongest country 
	 */
	public Country getStrongestCountries(ArrayList<Country> assignedListOfCountries, int armyCount) {
		Country strongestCountry = null;
		//int armiesCount = thresholdArmyCount;
		for (Country list : assignedListOfCountries) {
			if (list.getnoOfArmies() > armyCount) {
				armyCount = list.getnoOfArmies();
				strongestCountry = list;
			}
		}
		return strongestCountry;
	}


	private Country getCountriesToAttackneighbourCountries(Player playerToAttack, ArrayList<Country> CountriesToAttack) {
		Country sourceCountry = null;
		String playerName = playerToAttack.getPlayerName();
		int armies = Integer.MAX_VALUE;
		for (Country neighbourCountries : CountriesToAttack) {
			if (neighbourCountries.getnoOfArmies() < armies) {
				armies = neighbourCountries.getnoOfArmies();
				sourceCountry = neighbourCountries;
			}
		}
		return sourceCountry;
	}


}
