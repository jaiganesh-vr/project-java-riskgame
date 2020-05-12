package strategies;

import helper.Card;
import model.Country;
import model.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * This class is used for human player that requires user interaction to make decisions.
 * 
 * @author Jaiganesh
 * @version 1.0.0
 *
 */
public class Human implements PlayerStrategy, Serializable {

    public String strategyName = "Human";

	/**
	 * Returns the strategy name of the strategy
	 */
    public String getStrategyName(){
        return strategyName;
    }

    /**
	 * Returns true for human strategy
	 */
    public boolean isHuman(){
        return true;
    }

    /**
	 * Method to execute reinforcement for the human strategy
	 * @param player Player Information
	 */
    public boolean reinforce(Player player){

        Country country = player.getReinforceCountry();

        if(player.getNumberOfReinforcedArmies() == 0){
            System.out.println("Player "+player.getPlayerName()+": Doesn't have any Armies.");
            player.getAttackGamePhaseDetails().add("Player "+player.getPlayerName()+": Doesn't have any Armies.");
            return false;
        }

        if(player == null){
            System.out.println("Player ID"+player.getPlayerId()+"does not exist.");
            return false;
        }

        if (country == null) {
            System.out.println("Country Name: " + country.getCountryName() + " does not exist!");
            return false;
        }

        assignReinforcement(player,country);
        return true;
    }

    /**
     * Method that performs decreasing reinforcement and increasing army count for human reinforcement
     * @param player player information
     * @param country country information
     */
    public void assignReinforcement(Player player, Country country){
        player.decreaseReinforcementArmy();
        country.increaseArmyCount();
    }

    /**
	 * Method to execute attack for the human strategy
	 * @param player Player Information
	 */
    public boolean attack(Player player){

        Player defenderPlayer = player.getAttackDefenderPlayer();

        Country attackerCountry = player.getAttackAttackerCountry();
        Country defenderCountry = player.getAttackDefenderCountry();

        int attackerDiceCount = player.getAttackAttackerDiceCount();
        int defenderDiceCount = player.getAttackDefenderDiceCount();

        ArrayList<String> gamePhaseDetails = player.getAttackGamePhaseDetails();

        player.diceRoller(attackerDiceCount);
        defenderPlayer.diceRoller(defenderDiceCount);

        ArrayList<Integer> diceResults1 = player.getDiceResults();
        ArrayList<Integer> diceResults2 = defenderPlayer.getDiceResults();

        ArrayList<Integer> attackingDices = diceResults1;
        ArrayList<Integer> defendingDices = diceResults2;

        int totalNumberOfDice = attackingDices.size() < defendingDices.size() ? attackingDices.size()
                : defendingDices.size();

        for (int i = 0; i < totalNumberOfDice; i++) {

            int attackerDice = attackingDices.get(i);
            int defencerDice = defendingDices.get(i);

            System.out.print("Attacker dice - " + attackerDice + "  to Defender dice - " + defencerDice);
            gamePhaseDetails.add("Attacker dice - " + attackerDice + "  to Defender dice - " + defencerDice);
            if (attackerDice > defencerDice) {
                System.out.println("Attacker wins for dice " + (i + 1));
                gamePhaseDetails.add("Attacker wins for dice " + (i + 1));
                defenderCountry.decreaseArmyCount(1);

            } else {
                System.out.println("Defender wins for dice " + (i + 1));
                gamePhaseDetails.add("Defender wins for dice " + (i + 1));
                attackerCountry.decreaseArmyCount(1);
            }

            if (attackerCountry.getnoOfArmies() == 1) {
                System.out.println("Attacker not able to Attack ");
                gamePhaseDetails.add("Attacker not able to Attack ");
                break;
            }
            if (defenderCountry.getnoOfArmies() == 0) {
                System.out.println("Defender lost all armies in " + (i + 1) + " dice roll");
                gamePhaseDetails.add("Defender lost all armies in " + (i + 1) + " dice roll");
                break;
            }

        }
        // Check if defending armies are 0 then acquire the country with cards
        if (defenderCountry.getnoOfArmies() == 0) {
        	player.conquerCountry(defenderPlayer);
        }
        return true;
    }

    /**
	 * Method to execute fortification for the human strategy
	 * @param thisPlayer player information
	 */
    public boolean fortify(Player thisPlayer){

        Player player = thisPlayer;
        Country sourceCountry = player.getFortifySourceCountry();
        Country destinationCountry = player.getFortifyDestinationCountry();
        int armies = player.getFortifyArmies();

        if(!checkFortificationCondition(sourceCountry, destinationCountry,armies)) {
            return false;
        }

        sourceCountry.decreaseArmyCount(armies);
        destinationCountry.increaseArmyCount(armies);
        return true;
    }

    /**
     * Method that checks the fortification condition
     * @param sourceCountry Source countries
     * @param destinationCountry destination countries
     * @param armies number of armies
     * @return TRUE if there are are armies otherwiese false
     */
    public boolean checkFortificationCondition(Country sourceCountry, Country destinationCountry, int armies) {
        if (sourceCountry == null || destinationCountry == null) {
            System.out.println("Source or destination country is invalid!");
            return false;
        }
        if (armies == 0) {
            System.out.println("No armies to move");
            return false;
        }
        return true;
    }
}
