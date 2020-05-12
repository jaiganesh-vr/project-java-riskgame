package views;

import helper.JTableRowNameDominationView;
import model.Game;
import strategies.PlayerStrategy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TournamentModeResultView {
    public static JPanel panelTournamentModeResultView;
    public static JFrame frameTournamentModeResultView;
    public static JTable table1, table2;
    public static JScrollPane scrollPane1, scrollPane2;
    public static JList rowHeader;
    private static Game gameGlobal;

    public static void callTournamentResult(int M, int G, int D, HashMap<String, ArrayList<String>> tournamentResult,
                                            ArrayList<PlayerStrategy> strategies) {
        frameTournamentModeResultView = new JFrame("The Tournament Result");
        panelTournamentModeResultView = new JPanel(new BorderLayout());
        panelTournamentModeResultView.setLayout(new FlowLayout());

        StringBuilder strategiesNameString = new StringBuilder();
        for (int i = 0; i < strategies.size(); i++) {
            strategiesNameString.append(strategies.get(i).getStrategyName() + ", ");
        }
        String[] getMapsUserUsed = tournamentResult.keySet().toArray(new String[tournamentResult.keySet().size()]);
        String[] columns = {"","",""};
        String rowData[][] =new String[6][6] ;

        rowData[0][1] = "M: "+Arrays.toString(getMapsUserUsed);
        rowData[1][1] = "P: " + "[ "+strategiesNameString.toString()+"]";
        rowData[2][1] = "G: "+Integer.toString(G);
        rowData[3][1] = "D: "+ Integer.toString(D);
        rowData[4][1] = "";

        ArrayList<String> gameNumber= new ArrayList<>();
        for(int i=1; i<=G; i++){
            gameNumber.add("Game "+ i);
        }
        String[] gameNumberString = new String[gameNumber.size()];
        gameNumberString = gameNumber.toArray(gameNumberString);

        ListModel lm = new AbstractListModel() {
            public int getSize() { return getMapsUserUsed.length; }
            public Object getElementAt(int index) {
                return getMapsUserUsed[index];
            }
        };

        String[][] theResultForTheGameAndMap = new String[M][G];
        for (int i=0; i< M; i++){
            ArrayList<String> gameResults = tournamentResult.get(getMapsUserUsed[i]);
            for(int j = 0; j< G; j++){
               String result = gameResults.get(j);
               theResultForTheGameAndMap[i][j]=result;
//                System.out.println(tournamentResult.);
            }
        }

        table1 = new JTable(rowData,columns);
        scrollPane1 = new JScrollPane(table1);
        Container table1ContentPane = frameTournamentModeResultView.getContentPane();
        table1ContentPane.setLayout(new BoxLayout(table1ContentPane, BoxLayout.Y_AXIS));
        table1ContentPane.add(table1.getTableHeader());
        table1ContentPane.add(table1);

        table2 = new JTable(theResultForTheGameAndMap, gameNumberString);
        table2.setEnabled(false);
        table2.getTableHeader().setBackground(Color.orange);
        rowHeader = new JList(lm);
        rowHeader.setFixedCellWidth(150);
        rowHeader.setCellRenderer(new JTableRowNameDominationView(table2));
        scrollPane2 = new JScrollPane(table2);
        scrollPane2.setRowHeaderView(rowHeader);
        frameTournamentModeResultView.setLocationRelativeTo(table1);
        frameTournamentModeResultView.add(scrollPane2, BorderLayout.SOUTH);


        frameTournamentModeResultView.add(panelTournamentModeResultView);
        frameTournamentModeResultView.pack();
        frameTournamentModeResultView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameTournamentModeResultView.setSize(1000, 300);
        frameTournamentModeResultView.setVisible(true);
        frameTournamentModeResultView.validate();
    }
}
