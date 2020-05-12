package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

import model.MapModel;
import model.Player;
import model.Game;
import helper.Colors;
import helper.PrintConsoleAndUserInput;
import model.Country;
import model.CountryViewModel;
import helper.GamePhase;
// TODO: Auto-generated Javadoc

/**
 * This class implements the Risk game view designed in GUI.
 *
 * @author naren
 * @version 2.0.0
 */
public class BoardView implements Observer,Serializable {

	/** The frame game window. */
	// Board Initialization
	private static JFrame frameGameWindow ;
	
	/** The panel game action. */
	private static JPanel panelGameAction;

	/** The lab map. */
	// Map variables
	private static JLabel labelMap;
	
	/** The pane map scroll pane. */
	private static JScrollPane mapScrollPane = null;
	
	/** The map hash map. */
	private static HashMap<String, Component> hashMapCountryID = new HashMap<>();

	/** The lab game phase. */
	// Phase variables
	private static JLabel labelGamePhase;
	
	/** The lab name of phase. */
	private static JLabel labelNameOfPhase;

	// Phase View Actions Components
	private static JComponent componentGamePhaseActions;
	private static JScrollPane gameScrollPanePhaseView;
	private static JLabel labelCurrentAction;

	/** The lab initialization. */
	// Initialization variables
	private static JLabel labelInitialisation;
	
	/** The lab players turn. */
	private static JLabel labelPlayersTurn;
	
	/** The lab armies left. */
	private static JLabel labelArmiesLeft;

	/** The lab reinforcement. */
	// Reinforcement variables
	private static JLabel labelReinforcement;
	
	/** The lab unassigned reinforcement. */
	private static JLabel labelUnassignedReinforcement;

	/** The lab attack. */
	// Attack Label
	private static JLabel labelAttack;
	
	/** The combo attacker country. */
	private static JComboBox<String> comboboxAttackerCountry;
	
	/** The combo defender country. */
	private static JComboBox<String> comboboxDefenderCountry;
	
	/** The combo attacker no of dice. */
	private static JComboBox<String> comboboxAttackerNoOfDice;
	
	/** The combo defender no of dice. */
	private static JComboBox<String> comboboxDefenderNoOfDice;
	
	/** The combo attack move armies. */
	private static JComboBox<String> comboboxAttackMoveArmies;
	
	/** The button move armies. */
	private static JButton buttonMoveArmies = new JButton("Move");
	
	/** The button attack. */
	private static JButton buttonAttack = new JButton("Attack");
	
	/** The button all out. */
	private static JButton buttonAllOut = new JButton("All Out");
	
	/** The button end attack. */
	private static JButton buttonEndAttack = new JButton("End Attack");

	/** The game save button*/
	private static JButton saveGameButton = new JButton("Save Game");
	


	/** The lab fortification. */
	// Fortification variables
	private static JLabel labelFortification;
	
	/** The combo country source. */
	private static JComboBox<String> comboboxCountrySource;

	/** The combo country destination. */
	private static JComboBox<String> comboboxCountryDestination;
	
	/** The combo army to move. */
	private static JComboBox<String> comboboxArmyToMove;
	
	/** The button move fortification. */
	private static JButton buttonMoveFortification = new JButton("Move Army");
	
	/** The button skip. */
	private static JButton buttonSkip = new JButton("Skip");

	/** The button player world domination view. */
	// Player World Domination Button
	private static JButton buttonPlayerWorldDominationView;

	/** The screen size. */
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	/** The screen height. */
	int screenHeight = screenSize.height;
	
	/** The screen width. */
	int screenWidth = screenSize.width;

	/** The active player name. */
	//Flags for determining the next actions
	String activePlayerName = null;
	
	/** The active player id. */
	int activePlayerId;
	
	/** The active player color. */
	Colors activePlayerColor = null;
	
	/** The reinforcement unassigned armies count. */
	String activePlayerUnassignedArmiesCount, reinforcementUnassignedArmiesCount;  
	
	/** The obj print. */
	PrintConsoleAndUserInput objPrint=new PrintConsoleAndUserInput();


	/** The map path. */
	public String mapPath;
	
	/** The country list. */
	ArrayList<CountryViewModel> countryList = new ArrayList<CountryViewModel>();
	
	/** The phase. */
	GamePhase phase;
	
	/** The map it. */
	MapModel mapIt;



	//----------------------------- View Update Function ---------------------------
	/**
	 * method to perform all the actions.
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		Game game = ((Game)arg0);
		mapIt = game.getMap();
		mapPath = game.getMap().getMapDir()+game.getMap().getMapName()+ ".bmp";
		phase = game.getGamePhase(); 
		File tempFile = new File(mapPath);
		boolean exists = tempFile.exists();
		if (!exists) {
			mapPath = game.getMap().getMapDir()+"no.bmp";
		}

		MapModel map = game.getMap();
		activePlayerName = game.getCurrentPlayer().getPlayerName();
		activePlayerId = game.getCurrentPlayerId();
		activePlayerColor = game.getCurrentPlayer().getColor();
		activePlayerUnassignedArmiesCount = Integer.toString(game.getCurrentPlayer().getNumberOfInitialArmies()); 
		reinforcementUnassignedArmiesCount = Integer.toString(game.getCurrentPlayer().getNumberOfReinforcedArmies());
		countryList.clear();

		for(Country country: map.getCountryList()){  
			CountryViewModel viewCountry = new CountryViewModel();
			viewCountry.setCountryId(country.getCountryId());
			viewCountry.setColorOfCountry(country.getCountryColor());
			viewCountry.setCountryName(country.getCountryName());
			viewCountry.setNumberOfArmies(country.getnoOfArmies());
			viewCountry.setxCoordinate(country.getxCoordinate());
			viewCountry.setyCoordinate(country.getyCoordinate());
			viewCountry.setNeighbours(country.getNeighboursString());
			viewCountry.setPlayerID(country.getPlayerId());
			JLabel label = (JLabel)hashMapCountryID.get(String.valueOf(country.getCountryId()));
			if(label != null){
				label.setText(String.valueOf(viewCountry.getNumberOfArmies()));
				label.setForeground(PrintConsoleAndUserInput.getColor(viewCountry.getColorOfCountry()));
			}
			countryList.add(viewCountry);
		}
		if(labelPlayersTurn != null){
			labelPlayersTurn.setText(activePlayerName);
			labelPlayersTurn.setForeground(PrintConsoleAndUserInput.getColor(activePlayerColor));
			labelArmiesLeft.setText(activePlayerUnassignedArmiesCount);
			labelUnassignedReinforcement.setText(reinforcementUnassignedArmiesCount);

			if (game.getGamePhase() == GamePhase.Startup) {
				labelNameOfPhase.setText("Initialization");
				labelFortification.setVisible(false); labelInitialisation.setVisible(true);
				labelReinforcement.setVisible(false);
			} else if (game.getGamePhase() == GamePhase.Reinforcement) {
				labelNameOfPhase.setText("Reinforcement");
				labelNameOfPhase.setForeground(Color.red);
				
				labelFortification.setVisible(false); labelReinforcement.setVisible(true);
				labelAttack.setVisible(false);
				 
			} else if (game.getGamePhase() == GamePhase.Attack) {
				labelNameOfPhase.setText("Attack Phase");
				labelNameOfPhase.setForeground(Color.BLUE);	
				labelAttack.setVisible(true); labelFortification.setVisible(false); 
				comboboxAttackerCountry();
			} else if (game.getGamePhase() == GamePhase.Fortification) {
				labelNameOfPhase.setText("Fortification");
				labelNameOfPhase.setForeground(Color.MAGENTA);	
				labelAttack.setVisible(false); labelFortification.setVisible(true); 
				comboboxSourceCountry();
			}
			componentGamePhaseActions.removeAll();
			int strartY = 5;
			ArrayList<String> gamePhaseDetailForPrint = game.getGamePhaseDetails();
			for (String gamePhaseDetailString : gamePhaseDetailForPrint) {
				JLabel textLabel = new JLabel();
				textLabel.setText(gamePhaseDetailString);
				Font font = new Font("Courier", Font.ITALIC, 16);
				textLabel.setFont(font);
				textLabel.setBounds(15, strartY, 220, 20);
				strartY = strartY + 20;

				componentGamePhaseActions.add(textLabel);
			}
			componentGamePhaseActions.setPreferredSize(new Dimension(300, strartY));
			componentGamePhaseActions.revalidate();
			componentGamePhaseActions.repaint();
		}

	}

	//-------------------------- Board View Initializer ---------------------------------
	/**
	 * Method that loads up the GUI window.
	 */
	public void gameWindowLoad() {
		
		frameGameWindow=  new JFrame("Risk Game");
		frameGameWindow.toFront();
		panelGameAction=new JPanel(null);

		mapGenerator();
		createPlayerWorldDominationView();
		createSaveGameButton();
		gamePhase();
		loadingPhaseActionLabel();
		viewInitialisation();
		reinforcements();
		fortification();
		viewAttackPhase();
		
		frameGameWindow.setSize(mapScrollPane.getWidth()+550, 1200);
		frameGameWindow.setVisible(true);
		panelGameAction.setBackground(Color.white);
		frameGameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}


	//------------------------ Map View initializing -----------------------------
	/**
	 * This method is initializing the jframe and importing the map file and country related data.
	 */
	public void mapGenerator() {

		File imageFile = null;

		imageFile = new File(mapPath);
		Image image;
		ImageIcon icon = null;
		try {
			image = ImageIO.read(imageFile);
			icon = new ImageIcon(image);
		} catch (IOException e) {

			e.printStackTrace();
		}

		labelMap = new JLabel(icon);

		for (int i = 0; i < countryList.size(); i++) {
			CountryViewModel viewCountry = countryList.get(i);
			int xCoordinate = viewCountry.getxCoordinate();
			int yCoordinate = viewCountry.getyCoordinate();
			JLabel newLabel = new JLabel("" + viewCountry.getNumberOfArmies());
			newLabel.setName("mapLabel" + viewCountry.getCountryId());
			newLabel.setToolTipText(viewCountry.getCountryName());
			newLabel.setBounds(xCoordinate, yCoordinate, 25, 25);
			newLabel.setFont(new Font("Courier", Font.BOLD, 20));
			newLabel.setForeground(PrintConsoleAndUserInput.getColor(viewCountry.getColorOfCountry()));
			hashMapCountryID.put(String.valueOf(viewCountry.getCountryId()), newLabel);
			labelMap.add(newLabel);
		}

		mapScrollPane = new JScrollPane(labelMap);
		mapScrollPane.setBounds(0,10,icon.getIconWidth()+20,icon.getIconHeight()+20);
		panelGameAction.add(mapScrollPane);
		frameGameWindow.add(panelGameAction);
	}

	//-------------------------------  Game Phase details ---------------------------
	/**
	 * Method that updates the phase of the game.
	 */
	public void gamePhase() {
		labelGamePhase = new JLabel();
		TitledBorder tb = BorderFactory.createTitledBorder(null, "Current Phase", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("Serif", Font.PLAIN, 12), Color.blue);
		labelGamePhase.setBorder(
				tb);
		String nm="#6600cc";
	//	tb.setBorder(new LineBorder(Color.decode(nm)));
		labelGamePhase.setBounds(mapScrollPane.getWidth()+10, mapScrollPane.getY()+55, 490, 60);

		labelNameOfPhase = new JLabel("Initialization");
		Font font = new Font("Courier", Font.BOLD, 20);
		labelNameOfPhase.setFont(font);
		labelNameOfPhase.setBounds(15, 15, 220, 40);

		labelGamePhase.add(labelNameOfPhase);
		panelGameAction.add(labelGamePhase);
	}

	/**
	 * Method to display the actions performed during each phase
	 */
	public void loadingPhaseActionLabel() {
		componentGamePhaseActions = new JPanel(){
		    @Override
		    public Dimension getPreferredSize() {
		        return new Dimension(300, 5000);
		    };
		};
		componentGamePhaseActions.setLayout(new FlowLayout(FlowLayout.LEFT));
		gameScrollPanePhaseView = new JScrollPane(componentGamePhaseActions);
		gameScrollPanePhaseView.setBounds(labelGamePhase.getX(),labelGamePhase.getY() + labelGamePhase.getHeight()+20,
				labelGamePhase.getWidth(), 150);
		TitledBorder tb = BorderFactory.createTitledBorder(null, "Phase Details", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("Serif", Font.PLAIN, 12), Color.blue);
		gameScrollPanePhaseView.setBorder(tb);
		panelGameAction.add(gameScrollPanePhaseView);
	}

	//--------------------------- initial Phase Start -----------------------------
	/**
	 * Method for initialization of game view.
	 */
	public void viewInitialisation() {
		labelInitialisation = new JLabel();
		
		
		TitledBorder tb = BorderFactory.createTitledBorder(null, "Initialization Phase", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("Serif", Font.PLAIN, 12), Color.blue);
		labelInitialisation.setBorder(tb);
		labelInitialisation.setBounds(labelGamePhase.getX(), gameScrollPanePhaseView.getY()+ gameScrollPanePhaseView.getHeight()+20, 490, 80);

		
		labelPlayersTurn = new JLabel(activePlayerName);
		Font font = new Font("Courier", Font.BOLD, 24);
		labelPlayersTurn.setFont(font);
		labelPlayersTurn.setBorder(new TitledBorder("Active Player Name"));
		labelPlayersTurn.setBounds(15, 25, 220, 50);
		labelArmiesLeft = new JLabel("" + activePlayerUnassignedArmiesCount);
		labelArmiesLeft.setBorder(new TitledBorder("Armies Left"));
		labelArmiesLeft.setBounds(labelPlayersTurn.getX() + 240,
				labelPlayersTurn.getY() - 50 + labelPlayersTurn.getHeight(), labelPlayersTurn.getWidth(),
				labelPlayersTurn.getHeight());

		labelInitialisation.add(labelPlayersTurn);
		labelInitialisation.add(labelPlayersTurn);
		labelInitialisation.add(labelArmiesLeft);
		panelGameAction.add(labelInitialisation);
	}

	//------------------------------- Reinforcement Phase ------------------------------
	/**
	 * Method for reinforcement implementation.
	 */
	public void reinforcements() {

		labelReinforcement = new JLabel();
		TitledBorder tb = BorderFactory.createTitledBorder(null, "Reinforcement Phase", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("Serif", Font.PLAIN, 12), Color.blue);
		labelReinforcement.setBorder(tb);
		labelReinforcement.setBounds(labelInitialisation.getX(),
				labelInitialisation.getY() +20 + labelInitialisation.getHeight(), labelInitialisation.getWidth(),
				80);
		labelUnassignedReinforcement = new JLabel(reinforcementUnassignedArmiesCount);
		labelUnassignedReinforcement.setBorder(new TitledBorder("Reinforced Unit"));
		labelUnassignedReinforcement.setBounds(15,25, 460,50);
		labelReinforcement.setVisible(false);
		labelReinforcement.add(labelUnassignedReinforcement);
		panelGameAction.add(labelReinforcement);
	}

	//------------------------------------ Attack phase ---------------------------------
	/**
	 * Method used to perform Attack phase of game.
	 */
	
	  public void viewAttackPhase() { 
		  labelAttack = new JLabel(); 
	  TitledBorder tb = BorderFactory.createTitledBorder(null, "Attack Phase", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("Serif", Font.PLAIN, 12), Color.blue);
		labelAttack.setBorder(tb);

	  labelAttack.setBounds(labelReinforcement.getX(), labelReinforcement.getY() + 25
	  + labelReinforcement.getHeight(), labelReinforcement.getWidth(), 250);
	  
	  comboboxAttackerCountry = new JComboBox();
	  comboboxAttackerCountry.setBorder(new
	  TitledBorder("Attack From"));
	  comboboxAttackerCountry.setBounds(15, 15, 220,50);
	  
	  comboboxDefenderCountry = new JComboBox();
	  comboboxDefenderCountry.setBorder(new TitledBorder("Attack To"));
	  comboboxDefenderCountry.setBounds(comboboxAttackerCountry.getX() + 20 +
	  comboboxAttackerCountry.getWidth() + 3, comboboxAttackerCountry.getY(),
	  comboboxAttackerCountry.getWidth(), comboboxAttackerCountry.getHeight());
	  
	  comboboxAttackerNoOfDice = new JComboBox<>();
	  comboboxAttackerNoOfDice.setBorder(new TitledBorder("Attacker's No Of Dice"));
	  comboboxAttackerNoOfDice.setBounds(comboboxAttackerCountry.getX(),
	  comboboxAttackerCountry.getY() + 7 + comboboxAttackerCountry.getHeight(),
	  comboboxAttackerCountry.getWidth(), comboboxAttackerCountry.getHeight());
	  
	  comboboxDefenderNoOfDice = new JComboBox<>();
	  comboboxDefenderNoOfDice.setBorder(new TitledBorder("Defender's No Of Dice"));
	  comboboxDefenderNoOfDice.setBounds(comboboxAttackerNoOfDice.getX() + 20 +
	  comboboxAttackerNoOfDice.getWidth() + 3, comboboxAttackerNoOfDice.getY(),
	  comboboxAttackerNoOfDice.getWidth(), comboboxAttackerNoOfDice.getHeight());
	  
	  buttonAttack.setBounds(comboboxAttackerNoOfDice.getX(),
	  comboboxAttackerNoOfDice.getY() + 7 + comboboxAttackerNoOfDice.getHeight(), 138,
	  30);
	  
	  buttonAllOut.setBounds(buttonAttack.getX() + buttonAttack.getWidth() + 21,
	  buttonAttack.getY(), 138, 30);
	  
	  buttonEndAttack.setBounds(buttonAllOut.getX() + buttonAllOut.getWidth() +
	  21, buttonAllOut.getY(), 138, 30);
	  
	  comboboxAttackMoveArmies = new JComboBox<>();
	  comboboxAttackMoveArmies.setBorder(new TitledBorder("Move armies"));
	  comboboxAttackMoveArmies.setBounds(buttonAttack.getX(), buttonAttack.getY() + buttonAttack.getHeight() + 7, comboboxAttackerNoOfDice.getWidth()+80,comboboxAttackerNoOfDice.getHeight());
	  comboboxAttackMoveArmies.setVisible(false);
	  buttonMoveArmies.setBounds(buttonEndAttack.getX(),comboboxAttackMoveArmies.getY() + 10, 138, 30);
	  buttonMoveArmies.setVisible(false);
	  labelAttack.add(comboboxAttackerCountry); labelAttack.add(comboboxDefenderCountry);
	  labelAttack.add(comboboxAttackerNoOfDice);
	  labelAttack.add(comboboxDefenderNoOfDice);
	  labelAttack.add(comboboxAttackMoveArmies); labelAttack.add(buttonMoveArmies);
	  labelAttack.add(buttonAttack); labelAttack.add(buttonAllOut);
	  labelAttack.add(buttonEndAttack); labelAttack.setVisible(false);
	  panelGameAction.add(labelAttack);
	  
	  }
	 
	 
	//------------------------ Fortification View Initial ------------------------------
	/**
	 * Method for fortification implementation.
	 */
	public void fortification() {
		labelFortification= new JLabel();
		TitledBorder tb = BorderFactory.createTitledBorder(null, "Fortification Phase", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("Serif", Font.PLAIN, 12), Color.blue);
		labelFortification.setBorder(tb);
		labelFortification.setBounds(labelReinforcement.getX(),
				labelReinforcement.getY() + 25 + labelReinforcement.getHeight(), labelReinforcement.getWidth(),
				140);

		comboboxCountrySource = new JComboBox();
		comboboxCountrySource.setBorder(new TitledBorder("Source Country"));
		comboboxCountrySource.setBounds(15, 25, 220, 50);
		comboboxCountryDestination = new JComboBox<>();
		comboboxCountryDestination.setBorder(new TitledBorder("Destination Country"));
		comboboxCountryDestination.setBounds(comboboxCountrySource.getX() + 20 + comboboxCountrySource.getWidth() + 3, comboboxCountrySource.getY(),
				comboboxCountrySource.getWidth(), comboboxCountrySource.getHeight());

		ArrayList<Integer> NoOfArmies = new ArrayList<Integer>();
		for (int i = 1; i <= Integer.parseInt(activePlayerUnassignedArmiesCount); i++) {
			NoOfArmies.add(i);
		}

		comboboxArmyToMove = new JComboBox(NoOfArmies.toArray());
		comboboxArmyToMove.setBounds(comboboxCountrySource.getX(), comboboxCountrySource.getHeight() + comboboxCountrySource.getY() + 7,
				comboboxCountrySource.getWidth(), comboboxCountrySource.getHeight());
		comboboxArmyToMove.setBorder(new TitledBorder("Total number of armies to move"));
		buttonMoveFortification.setBounds(comboboxCountryDestination.getX(),
				comboboxCountryDestination.getHeight() + comboboxCountryDestination.getY() + 17, 100, 30);
		buttonSkip.setBounds(buttonMoveFortification.getX() + buttonMoveFortification.getWidth() + 10,
				buttonMoveFortification.getY(), buttonMoveFortification.getWidth(),
				buttonMoveFortification.getHeight());
		labelFortification.add(comboboxCountrySource);
		labelFortification.add(comboboxCountryDestination);
		labelFortification.add(comboboxArmyToMove);
		labelFortification.add(buttonMoveFortification);
		labelFortification.add(buttonSkip);
		labelFortification.setVisible(false);
		panelGameAction.add(labelFortification);
	}

	// ----------------------------- Player Domination Button Initialized ------------------
	/**
	 * Method to display world domination view for each player.
	 */
	public void createPlayerWorldDominationView() {
		buttonPlayerWorldDominationView = new JButton("Player World Domination View");
		buttonPlayerWorldDominationView.setBounds(mapScrollPane.getWidth()+10, mapScrollPane.getY(), 360, 40);
		panelGameAction.add(buttonPlayerWorldDominationView,BorderLayout.NORTH);

	
	}

	/**
	 * World domination view listener.
	 * @param listener Action Listener
	 */
	//--------------------------------- Listener Initialization -------------------------
	public void worldDominationViewListener(ActionListener listener) {
		buttonPlayerWorldDominationView.addActionListener(listener);
	}	
	
	
	// ----------------------------- Save and load Game ------------------	
	/**
	 * This method is used to create the save game button in the panel window.
	 */
	public void createSaveGameButton() {	
		saveGameButton.setBounds(buttonPlayerWorldDominationView.getX() + buttonPlayerWorldDominationView.getWidth() + 15,
				mapScrollPane.getY(),110, 40);
		panelGameAction.add(saveGameButton);	
	}
	
	/**
	 * Save game Listener.
	 * @param listener Action Listener
	 */
	public void saveGameButtonListener(ActionListener listener) {
		saveGameButton.addActionListener(listener);
	}
	
	
	
	/**
	 * method to use for the mouse event for the map labels.
	 * @param listener MouseListener
	 */
	public void addMapLabelsListener(MouseListener listener) {
		int n = labelMap.getComponentCount();
		for (int i = 0; i < n; i++) {
			JLabel jLabel = (JLabel) labelMap.getComponent(i);
			jLabel.addMouseListener(listener);
		}
	}

	/**
	 * method to add a listener in the combobox of the source country.
	 * @param listener ActionListener
	 */
	public void addActionListenToSourceCountryList(ActionListener listener) {
		comboboxCountrySource.addActionListener(listener);
	}

	/**
	 * method to add a listener in the combobox of the attacker country.
	 * @param listener ActionListener
	 */
	public void addActionListenToAttackerCountryList(ActionListener listener) {
		comboboxAttackerCountry.addActionListener(listener);
	}

	/**
	 * method to add a listener in the combobox of the defender country.
	 * @param listener ActionListener
	 */
	public void addActionListenToDefenderCountryList(ActionListener listener) {
		comboboxDefenderCountry.addActionListener(listener);
	}

	/**
	 * Method for performing action listener on move army button.
	 * @param listener ActionListener
	 */
	public void moveArmyButtonListener(ActionListener listener) {
		buttonMoveFortification.addActionListener(listener);
	}
	
	/**
	 * Method for performing action listener on attack Button.
	 * @param listener  ActionListener
	 */
	public void addActionListenToAttackButton(ActionListener listener) {
		buttonAttack.addActionListener(listener);
	}
	
	/**
	 * Method for performing action listener on End attack Button.
	 * @param listener ActionListener
	 */
	public void addActionListenToEndAttackButton(ActionListener listener) {
		buttonEndAttack.addActionListener(listener);
	}
	
	/**
	 * Method for performing action listener on End attack Button.
	 * @param listener ActionListener
	 */
	public void addActionListenToAllOutButton(ActionListener listener) {
		buttonAllOut.addActionListener(listener);
	}
	
	/**
	 * Method for performing action listener on move armies after concuring Button.
	 * @param listener ActionListener
	 */
	public void addActionListenToMoveButton(ActionListener listener) {
		buttonMoveArmies.addActionListener(listener);
	}
	
	/**
	 * Skip the fortification round
	 * @param listener ActionListener
	 */
	public void skipFortificationActionListener(ActionListener listener) {
		buttonSkip.addActionListener(listener);
		
	}
	//--------------------- General Functions for data gathering ---------------- 

	/**
	 * Static method to get selected source country.
	 * @return selectedCountry
	 */
	public static String getSourceCountry() {

		return (String)comboboxCountrySource.getSelectedItem();

	}

	/**
	 * method to add countries to the source country combo box.
	 */
	public void comboboxSourceCountry(){
		comboboxCountrySource.removeAllItems();
		for (int i = 0; i < countryList.size(); i++) {
			CountryViewModel objCVM = countryList.get(i);
			if (activePlayerId == objCVM.getPlayerID()) {
				comboboxCountrySource.addItem(objCVM.getCountryName());
			}
		}

	}

	/**
	 * Static method to get selected attacker country.
	 * @return selectedCountry
	 */
	public static String getAttackerCountry() {
		return (String)comboboxAttackerCountry.getSelectedItem();

	}

	/**
	 * method to add countries to the attacker country combo box.
	 */

	public void comboboxAttackerCountry(){
		comboboxAttackerCountry.removeAllItems();
		for (int i = 0; i < countryList.size(); i++) {
			CountryViewModel objCVM = countryList.get(i);
			if (activePlayerId == objCVM.getPlayerID()&& objCVM.getNumberOfArmies()>1) {
				comboboxAttackerCountry.addItem(objCVM.getCountryName());
			}
		}

	}

	/**
	 * Static method to get selected attacker country.
	 * @return selectedCountry
	 */
	public  String getDefenderCountry() {
		return (String)comboboxDefenderCountry.getSelectedItem();

	}


	/**
	 * Method is populating value in the destination phase combobox.
	 * @param defenderCountries ArrayList
	 */	
	public void comboboxFillDefendersCountry(ArrayList<String> defenderCountries){   
		comboboxDefenderCountry.removeAllItems();
		for(String countryName : defenderCountries) {
			comboboxDefenderCountry.addItem(countryName);
		}

	}

	/**
	 * Method is populating value in the destination phase combobox.
	 * @param destinationCountries ArrayList
	 */	
	public void comboboxFillDestinationCountry(ArrayList<String> destinationCountries){   
		comboboxCountryDestination.removeAllItems();
		for(String countryName : destinationCountries) {
			comboboxCountryDestination.addItem(countryName);
		}

	}

	/**
	 * static method to get the selected item from destination combo.
	 * @return selectedCountry
	 */
	public static String getDestinationCountry() {

		Object selectedItem = comboboxCountryDestination.getSelectedItem();
		if(selectedItem != null){
			String selectedCountry = (String) selectedItem;
			return selectedCountry;
		}
		else {
			return "";
		}
	}

	/**
	 * Method to add the possible number of the army the player can move.
	 * @param NoOfArmies int
	 */
	public void comboboxFillArmyToMove(int NoOfArmies){   
		comboboxArmyToMove.removeAllItems();
		for(Integer i=0;i<NoOfArmies;i++)
			comboboxArmyToMove.addItem(i.toString());		
	}


	/**
	 * Static method to get number of army the player wants to move.
	 * @return NoOfArmies
	 */
	public static Integer comboboxGetArmyToMove() {
		Object selectedItem = comboboxArmyToMove.getSelectedItem();
		if(selectedItem != null){
			Integer NoOfArmies = (Integer.parseInt((String) selectedItem));
			return NoOfArmies;
		}
		return 0;
	}

	/**
	 * Method used to populate value in the attacker dice.
	 * @param allowableDices the new attacker dice combo box
	 */
	public void setAttackerDiceComboBox(int allowableDices) {
		comboboxAttackerNoOfDice.removeAllItems();
		for (int i = 1; i <= allowableDices; i++) {
			comboboxAttackerNoOfDice.addItem(Integer.toString(i));
		}
	}

	/**
	 * Static method to get selected attacker dice no.
	 * @return selectedCountry
	 */
	public static String getAttackerDiceNumber() {
		return (String) comboboxAttackerNoOfDice.getSelectedItem();

	}
	
	/**
	 * Method used to populate value in the defender dice.
	 * @param allowableDices the new defender dice combo box
	 */
	public void setDefenderDiceComboBox(int allowableDices) {
		comboboxDefenderNoOfDice.removeAllItems();
		for (int i = 1; i <= allowableDices; i++) {
			comboboxDefenderNoOfDice.addItem(Integer.toString(i));
		}
	}
	
	/**
	 * Static method to get selected defender dice no.
	 * @return selectedCountry
	 */
	public static String getDefenderDiceNumber() {
		return (String)comboboxDefenderNoOfDice.getSelectedItem();

	}
	
	/**
	 * A method to set visible to move after attack.
	 */
	public void setVisibalityOfMoveAfterConcure() {
		comboboxAttackMoveArmies.setVisible(true);
		buttonMoveArmies.setVisible(true);
	}
	
	/**
	 * A method to set visible to move after move.
	 */
	public void setVisibalityOfMoveAfterMove() {
		comboboxAttackMoveArmies.setVisible(false);
		buttonMoveArmies.setVisible(false);
	}
	
	/**
	 * Method used to populate value in the move.
	 * @param movePossible possible move
	 */
	public void setMoveComboBox(int movePossible) {
		comboboxAttackMoveArmies.removeAllItems();
		for (int i = 1; i < movePossible; i++) {
			comboboxAttackMoveArmies.addItem(Integer.toString(i));
		}
	}
	
	/**
	 * Static method to get selected move possible.
	 * @return selectedCountry
	 */
	public  String getMoveComboBox() {
		return (String)comboboxAttackMoveArmies.getSelectedItem();

	}
	

	/**
	 * get the frame to control the card conditions.
	 * @return frameGameWindow jframe for the card window
	 */
	public JFrame getFrameGameWindow() {
		return frameGameWindow;
	}
	
	
	/**
	 * This is the method to close the frame window
	 */
	public void closeFrameWindow() {
		// TODO Auto-generated method stub
		frameGameWindow.dispose();
	}
}
