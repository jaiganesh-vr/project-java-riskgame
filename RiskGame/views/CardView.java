package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;


import model.Game;
import helper.Card;
import helper.PrintConsoleAndUserInput;

// TODO: Auto-generated Javadoc
/**
 *
 * This class is used to chose the cards by the player during the Reinforcement phase of the game to obtain new armies.
 * 
 * @author Jaiganesh
 * @author naren
 * @version 1.0.0
 */

public  class CardView implements Observer, Serializable{
	
	/** The frame card exchange. */
	public static JFrame frameCardExchange = null;
	
	/** The panel card exchange. */
	private static JPanel panelCardExchange;
	
	/** The lab card exchange. */
	private static JLabel labelCardExchange;
	
	/** The lab for player turn. */
	private static JLabel labelPlayerTurn;
	
	/** The list cards owned by the player. */
	public static JList<String> listCardsOwnedByThePlayer;
	
	/** The lab total new armies. */
	private static JLabel labelTotalNewArmies;
	
	/** The button card exchange. */
	private static JButton buttonCardExchange = new JButton("Exchange Cards");
	
	/** The button exit. */
	private static JButton buttonExit = new JButton("Skip Exchange");
	
	/** The game. */
	//Instantiate game object
	 Game game;
	/**
	 * Instantiates a new card view.
	 *
	 * @param gameTemp the game temp
	 */
	public CardView(Game gameTemp){
		game = gameTemp;
	}
	
	/**
	 * ui for the card exchange.
	 */
	public  void Exchange() {
		frameCardExchange = new JFrame("Card Exchange View");
		panelCardExchange = new JPanel(null);
		frameCardExchange.setSize(800, 600);

		frameCardExchange.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		labelCardExchange = new JLabel();
		labelCardExchange
		.setBorder(BorderFactory.createTitledBorder(null, "Exchange Card", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("SansSerif", Font.PLAIN, 12), Color.BLACK));
		labelCardExchange.setBounds(100, 100, 600, 400);
		labelPlayerTurn = new JLabel(game.getCurrentPlayer().getPlayerName());
		Font font = new Font("Courier", Font.BOLD, 24);
		labelPlayerTurn.setFont(font);
		labelPlayerTurn.setForeground(PrintConsoleAndUserInput.getColor(game.getCurrentPlayer().getColor()));
		labelPlayerTurn.setBorder(new TitledBorder("Active Player"));
		labelPlayerTurn.setBounds(30, 45, 250, 150);
		labelPlayerTurn.setHorizontalAlignment(labelPlayerTurn.CENTER);
		labelPlayerTurn.setVerticalAlignment(labelPlayerTurn.CENTER);
		//getting the cards a player owns
		ArrayList<Card> typeOfCards = game.getCurrentPlayer().getCards();
		String cards[] = new String[typeOfCards.size()];
		//assigning the cards the player has in a string array
		for (int i = 0; i < typeOfCards.size(); i++) {
			cards[i] = typeOfCards.get(i).toString();
		}
		//putting it in a JList
		listCardsOwnedByThePlayer = new JList<>(cards);
		listCardsOwnedByThePlayer.setBorder(new TitledBorder("Cards Owned"));
		listCardsOwnedByThePlayer.setBounds(310, 45, 250, 150);
		labelTotalNewArmies = new JLabel("" + game.getCurrentPlayer().getNumberOfReinforcedArmies());
		labelTotalNewArmies.setBorder(new TitledBorder("Reinforced Number Army"));
		labelTotalNewArmies.setBounds(180, 200, 250, 70);
		buttonCardExchange.setBounds(120, 280, 160, 40);
		buttonExit.setBounds(310, 280, 160, 40);
		labelCardExchange.add(labelTotalNewArmies);
		labelCardExchange.add(listCardsOwnedByThePlayer);
		labelCardExchange.add(labelPlayerTurn);
		labelCardExchange.add(buttonCardExchange);
		labelCardExchange.add(buttonExit);
		panelCardExchange.add(labelCardExchange);
		frameCardExchange.add(panelCardExchange);
		frameCardExchange.setVisible(true);
		//default close button to not work
		frameCardExchange.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				
	}

	/**
	 * Exchange action listener.
	 * @param listener the listener
	 */
	public static void exchangeActionListener(ActionListener listener) {
		buttonCardExchange.addActionListener(listener);
	}
	
	/**
	 * Exit action listener.
	 * @param listener the listener
	 */
	public static void exitActionListener(ActionListener listener) {
		buttonExit.addActionListener(listener);
	}
	
	/**
	 * Close the window.
	 */
	public static void closeTheWindow() {
		frameCardExchange.dispose();
	}

	/**
	 * Game Object
	 */
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		Game game = ((Game)o);
	}
	
	/**
	 * Updates the CardView window
	 * @param game Game object
	 */
	public void updateCardView(Game game) {

		if (game.getCurrentPlayer() != null && labelTotalNewArmies != null) {
			labelTotalNewArmies.setText("" + game.getCurrentPlayer().getNumberOfReinforcedArmies());
			ArrayList<Card> typeOfCards = game.getCurrentPlayer().getCards();
			String cards[] = new String[typeOfCards.size()];
			for (int i = 0; i < typeOfCards.size(); i++) {
				cards[i] = typeOfCards.get(i).toString();
			}
			labelCardExchange.remove(listCardsOwnedByThePlayer);
			listCardsOwnedByThePlayer = null;
			listCardsOwnedByThePlayer = new JList<>(cards);
			listCardsOwnedByThePlayer.setBorder(new TitledBorder("Cards Owned"));
			listCardsOwnedByThePlayer.setBounds(310, 45, 250, 70);
			labelCardExchange.add(listCardsOwnedByThePlayer);	
			frameCardExchange.revalidate();
			frameCardExchange.repaint();

		
		}

	}
	
}
