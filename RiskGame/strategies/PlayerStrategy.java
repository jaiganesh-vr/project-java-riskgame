package strategies;
import model.Player;

/**
 * This class is used for the declaration of the reinforce, attack and fortify methods.
 * 
 * @author Jaiganesh
 * @version 1.0.0
 *
 */
public interface PlayerStrategy {


	/**
	 * This string gets the name of the strategy.
	 * @return name of the Strategy 
	 */
	public String getStrategyName();


	/**
	 * This is used to get if the player is a human or non-human.
	 * @return true if player is human otherwise false
	 */
	public boolean isHuman();


	/**
	 * This is used to get a boolean value for if to perform reinforcement operation of a particular strategy
	 * @param player  Player Object information
	 * @return true if yes otherwise false
	 */
	public boolean reinforce(Player player);


	/**
	 * This is used to get boolean value for if to perform attack operation of a particular strategy
	 * @param player Player Object information
	 * @return true if yes otherwise false
	 */
	public boolean attack(Player player);


	/**
	 * This is used to get a boolean value for if to perform fortification operation of a particular strategy
	 * @param player  Player Object information
	 * @return true if yes otherwise false
	 */
	public boolean fortify(Player player);

}