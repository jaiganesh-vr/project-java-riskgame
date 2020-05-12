package strategies;

import java.io.Serializable;
import java.util.ArrayList;

import helper.Card;
import model.Country;
import model.Game;
import model.Player;

/**
 * This class is used for cheater computer player strategy whose reinforce() method doubles the number of armies on all its countries,
 * whose attack() method automatically conquers all the neighbors of all its countries, and whose fortify() method 
 * doubles the number of armies on its countries that have neighbors that belong to other players.
 * 
 * @author naren
 * @version 1.0.0
 *
 */
public class Cheater implements PlayerStrategy, Serializable  {

	public String strategyName = "Cheater";

	/**
	 * Returns the strategy name of the strategy
	 */
	public String getStrategyName(){
		return strategyName;
	}

	/**
	 * Return false for the non-human(Cheater) strategy
	 */
	@Override
	public boolean isHuman() {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * Method to execute reinforcement for the cheater strategy
	 * @param player palyer information
	 */
	public boolean reinforce(Player player) {
		for (Country country :  player.getAssignedListOfCountries()) {
			String countryName = country.getCountryName();
			int armies = country.getnoOfArmies();
			System.out.println(
					"Adding reinforcement army in " + countryName + "(" + armies + ")");
			player.getAttackGamePhaseDetails().add("Adding reinforcement army in " + country.getCountryName() + "(" + country.getnoOfArmies() + ")");
			player.setNumberOfReinforcedArmies(0);			
			country.setnoOfArmies(armies * 2);
			System.out.println(
					"Added reinforcement army in " + countryName + "(" + armies + ")");
			player.getAttackGamePhaseDetails().add("Added reinforcement army in " + countryName + "(" + armies + ")");
		}
		return true;

	}

	/**
	 * Method to execute attack for the cheater strategy
	 * @param player Player Information
	 */
	public boolean attack(Player player) {
		Object[] playersCountries = player.getAssignedListOfCountries().toArray();

		for(Object o : playersCountries) {
			Country country = (Country) o;
			ArrayList<Country> getNeighbouringCountries = player.getOthersNeighbouringCountriesOnlyObject(country);
			System.out.println("Cheater:\t"+player.getPlayerName()+"\tattacking\t"+getNeighbouringCountries.size()+"\tneighbours.");
			player.getAttackGamePhaseDetails().add("Cheater:\t"+player.getPlayerName()+"\t attacking\t"+getNeighbouringCountries.size()+"\tneighbours.");
			System.out.println("Is now attacking:\t"+country.getCountryName());
			player.getAttackGamePhaseDetails().add("Is now attacking:\t"+country.getCountryName());
			for(Country temp:getNeighbouringCountries) {
				Player defender=player.getPlayer(temp.getPlayerId());
				player.conquerCountryAutomate(defender,temp,country);
				temp.setnoOfArmies(1);
				country.increaseArmyCount(1);
				System.out.println("Defeated:\t"+temp.getCountryName());
				player.getAttackGamePhaseDetails().add("Defeated:\t"+temp.getCountryName());
				if(country.getnoOfArmies()<1) {
					country.setnoOfArmies(1);
				}
			}
		}
		return true;

	}



	/**
	 * Method to execute fortification for the cheater strategy
	 * @param player player information
	 */
	public boolean fortify(Player player) {
		int armiesCount;
		for (Country country : player.getattackPlayerCountry().get(player)) {
			System.out.println("Cheater player " + player.getPlayerName() + " is trying to fortify " + country.getCountryName()
			+ "(" + country.getnoOfArmies() + ")");

			ArrayList<Country> getNeighbouringCountries = player.getOthersNeighbouringCountriesOnlyObject(country);
			for(Country country1:getNeighbouringCountries) {
				armiesCount=country1.getnoOfArmies();
				country1.setnoOfArmies(armiesCount*2);
				System.out.println("-- Finished fortification with country " + country1.getCountryName() + " ("
						+ country1.getnoOfArmies() + ")");

				player.getAttackGamePhaseDetails().add("Cheater player " + player.getPlayerName() + " is trying to fortify " + country.getCountryName()
				+ "(" + country.getnoOfArmies() + ")");
				ArrayList<Country> neighbouringCountries = player.getNeighbouringCountries(country);
				if (neighbouringCountries != null || neighbouringCountries.size() == 0) {
					System.out.println("Cannot fortify as there is no neigbouring county found from other player");
					player.getAttackGamePhaseDetails().add("Cannot fortify as there is no neigbouring county found from other player");
				} else {
					armiesCount = country.getnoOfArmies() * 2;
					country.setnoOfArmies(armiesCount);
					System.out.println("Finished fortification with country " + country.getCountryName() + " ("
							+ country.getnoOfArmies() + ")");
					player.getAttackGamePhaseDetails().add("Finished fortification with country " + country.getCountryName() + " ("
							+ country.getnoOfArmies() + ")");			}
			}	

		}
		if(player.getIsConqured()){
			System.out.println("Conquered");
			player.getAttackGamePhaseDetails().add("Conquered");
			Card riskCard = player.getRiskCards();

			if(riskCard == null){
				System.out.println("No Cards Available Right Now.");
			} else {
				player.addCard(riskCard);
				player.getAttackGamePhaseDetails().add("Card added:"+ riskCard);

			}

			player.setIsConquered(false);

		}

		return true;
	}

	
}

