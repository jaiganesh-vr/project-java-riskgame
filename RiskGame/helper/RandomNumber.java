package helper;


/**
 * This class generates a random number to be used for the Random Strategy
 * 
 * @author Jaiganesh
 * @version 1.0.0
 */
public class RandomNumber {

	/**
	 * Method to generate a random number 
	 * @param minimumValue minimum numbers
	 * @param maximumValue maximum numbers
	 * @return random value
	 */
    public static int getRandomNumberInRange(int minimumValue, int maximumValue) {
        if(minimumValue == maximumValue)
            return minimumValue;

        if (minimumValue > maximumValue) {
            throw new IllegalArgumentException("Maximum value must be greater than Minimum value!");
        }
        java.util.Random r = new java.util.Random();
        return r.nextInt((maximumValue - minimumValue) + 1) + minimumValue;
    }
}
