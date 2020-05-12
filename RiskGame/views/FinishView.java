package views;
import java.awt.Font;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;


// TODO: Auto-generated Javadoc
/**
 *
 *This class is used to display the Congratulation message to the player who wins the game.
 *
 * @author Jaiganesh
 */

public class FinishView  {

	/** The frame congratulation. */
	private static JFrame frameCongratulation = null;

	/** The panel congratulation. */
	private static JPanel panelCongratulation;

	/** The lab congratulation. */
	private static JLabel labCongratulation;

	/**
	 * UI for printing the winner of the game
	 * @param playerName the player name
	 */
	public void callWinner(String playerName) {
		frameCongratulation = new JFrame("Congratulations");
		panelCongratulation = new JPanel();
		frameCongratulation.setSize(800, 300);
		frameCongratulation.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		labCongratulation = new JLabel("Congratulation! Player "+ playerName + " wins the game");
		Font font = new Font("Courier", Font.BOLD, 30);
		labCongratulation.setFont(font);
		labCongratulation.setBounds(100, 100, 220, 40);
		panelCongratulation.add(labCongratulation);
		frameCongratulation.add(panelCongratulation);
		frameCongratulation.setVisible(true);

	}
}
