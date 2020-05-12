package strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import helper.RandomNumber;
import model.Country;
import model.Player;


import java.io.Serializable;

/**
 * This class is used for benevolent computer player strategy that focuses on protecting its weak countries 
 * (reinforces its weakest countries, never attacks, then fortifies in order to move armies to weaker countries).
 *
 * @author naren
 * @version 1.0.0
 *
 */
public class Benevolent implements PlayerStrategy,Serializable{
		
	public String strategyName = "Benevolent";

	/**
	 * Returns the strategy name of the strategy
	 */
    public String getStrategyName(){
        return strategyName;
    }

    /**
	 * Return false for the non-human(Benevolent) strategy
	 */
	public boolean isHuman() {
		return false;
	}

	/**
	 * Gets the weakest country of the benevolent
	 * @param countries
	 * @return country
	 */
	private Country getWeakestCountry(ArrayList<Country> countries) {
		Country country = null;
		int armiesCount = Integer.MAX_VALUE;
		for (Country c : countries) {
			if (c.getnoOfArmies() < armiesCount) {
				armiesCount = c.getnoOfArmies();
				country = c;
			}
		}
		return country;
		
		
	}
	
	/**
	 * Gets the Minimum number of armies of the weakest country that belongs to benevolent
	 * @param player Player Information
	 * @return returnVal value of minimum armies
	 */
	public int getMinimumArmies(Player player) {
		int returnVal = Integer.MAX_VALUE;
		ArrayList<Country> assignedCountryList = player.getAssignedListOfCountries();
		for (Country country : assignedCountryList) {
			if (country.getnoOfArmies() < returnVal)
				returnVal = country.getnoOfArmies();
		}
		return returnVal;
	}

	/**
	 * Method to execute reinforcement for the benevolent strategy
	 * @param player Player object information
	 */
	@Override
	public boolean reinforce(Player player) {
		// TODO Auto-generated method stub
		int minArmies = getMinimumArmies(player);
		List<Country> weakestCountries =  player.getattackPlayerCountry().get(player).stream()
				.filter(x -> x.getnoOfArmies() == minArmies).collect(Collectors.toList());

		System.out.println("Found " + weakestCountries.size() + " weakest countries. And now assigning "
				+ player.getNumberOfReinforcedArmies() + " armies");
		player.getAttackGamePhaseDetails().add("Found " + weakestCountries.size() + " weakest countries. And now assigning "
				+ player.getNumberOfReinforcedArmies() + " armies");
		if (weakestCountries != null && weakestCountries.size() > 0) {
			
			int index = 0;
			while (player.getNumberOfReinforcedArmies() > 0) {
				Country c = weakestCountries.get(index);
				int armies = player.getNumberOfReinforcedArmies();
				player.decreaseReinforcementArmy();
				c.increaseArmyCount(1);
				System.out.println("Added reinforcement army in " + c.getCountryName() + "(" + c.getnoOfArmies() + ")");
				player.getAttackGamePhaseDetails().add("Added reinforcement army in " + c.getCountryName() + "(" + c.getnoOfArmies() + ")");
				index++;
				if (index == weakestCountries.size())
					index = 0;
			}
		} else {
				System.out.println("Cannot find any weakest country");
				player.getAttackGamePhaseDetails().add("Cannot find any weakest country");
		}
		
		return true;
	}
	
	/**
	 * Method to execute attack for the benevolent strategy
	 * @param player Player Information
	 */
	@Override
	public boolean attack(Player player) {
		// TODO Auto-generated method stub
		System.out.println("No attack phase for Benevolent Strategy");
		return false;
	}
	
	/**
	 * Method to execute fortification for the benevolent strategy
	 * @param player Player object information
	 */
	@Override
	public boolean fortify(Player player) {
		// TODO Auto-generated method stub
		ArrayList<Country> countryList = player.getattackPlayerCountry().get(player);
		for (Country fromCountry : countryList) {

			if (fromCountry == null)
				break;
			System.out.println("Found strongest country " + fromCountry.getCountryName() + ". Now finding the weakest link...");
			player.getAttackGamePhaseDetails().add("Found strongest country " + fromCountry.getCountryName() + ". Now finding the weakest link...");
			ArrayList<Country> neighborCountries = player.getNeighbouringCountries(fromCountry);
			if (neighborCountries != null && neighborCountries.size() > 0) {
				Country toCountry = getWeakestCountry(neighborCountries);
				if (fromCountry != null && toCountry != null
						&& toCountry.getnoOfArmies() < fromCountry.getnoOfArmies()) {
					// fortify weakest country
					int armies = RandomNumber.getRandomNumberInRange(0, fromCountry.getnoOfArmies()  - 1);
					System.out.println("Benevolent player " + player.getPlayerName() + " - fortification from "
							+ fromCountry.getCountryName() + "(" + fromCountry.getnoOfArmies() + ") to "
							+ toCountry.getCountryName() + "(" + toCountry.getnoOfArmies() + ") with " + armies
							+ " armies");
					player.getAttackGamePhaseDetails().add("Benevolent player " + player.getPlayerName() + " - fortification from "
							+ fromCountry.getCountryName() + "(" + fromCountry.getnoOfArmies() + ") to "
							+ toCountry.getCountryName() + "(" + toCountry.getnoOfArmies() + ") with " + armies
							+ " armies");

					fromCountry.decreaseArmyCount(armies);
					toCountry.increaseArmyCount(armies);
					System.out.println("Finished fortifying "+armies+" armies to the destination country " + toCountry.getCountryName()
					+ " (" + toCountry.getnoOfArmies() + ")");
					player.getAttackGamePhaseDetails().add("Finished fortification with destination country " + toCountry.getCountryName()
					+ " (" + toCountry.getnoOfArmies() + ")");
					break;
				}
			}
			System.out.println("Cannot find any neighbouring weaker country");
			player.getAttackGamePhaseDetails().add("Cannot find any neighbouring weaker country");

		}

		return true;
	}
}