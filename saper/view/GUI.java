package lab_5.saper.view;

import lab_5.saper.model.Board;
import lab_5.saper.model.Cell;
import lab_5.saper.model.GameOverException;
import lab_5.saper.model.YouWinException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class GUI extends JFrame{
    private JPanel panel1;
    private JPanel grid;
    private JLabel FlagsCount;

    private Board board;
    private JButton[][] buttonBoardList;
    private int buttonSize = 40;



    public GUI(Board board) {


        setContentPane(panel1);
        setTitle("MineSweper");
        setIconImage(new ImageIcon("src/lab_5/saper/view/mineSweperIcon.png").getImage());

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createHelpMenu());
        setJMenuBar(menuBar);


        this.board = board;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        createGUIBoard();
        displayGUIBoard();
        pack();
        setVisible(true);
    }

    // sorry for this monster...
    public void createGUIBoard() {

        GridLayout layout = new GridLayout(this.board.getSize(), this.board.getSize(), 2, 2);
        grid.removeAll();
        grid.setLayout(layout);

        buttonBoardList = new JButton[board.getSize()][board.getSize()];

        for (int i = 0; i < this.board.getSize(); i++) {
            for (int j = 0; j < this.board.getSize(); j++) {
                int x = i;
                int y = j;
                buttonBoardList[x][y] = new JButton();
                buttonBoardList[x][y].setBorder(BorderFactory.createEmptyBorder());
                buttonBoardList[x][y].setPreferredSize(new Dimension(buttonSize, buttonSize));

                buttonBoardList[x][y].addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (buttonBoardList[x][y].isEnabled()){
                            if (e.getButton() == MouseEvent.BUTTON1) {
                                try {
                                    board.openCellAction(x, y);
                                    displayGUIBoard();
                                } catch (GameOverException ex) {
                                    gameOverAction();
                                    JOptionPane.showMessageDialog(GUI.this,
                                            "You lose!");
                                    newGame();
                                }

                            } else if (e.getButton() == MouseEvent.BUTTON3) {
                                try {
                                    board.toggleFlag(x, y);
                                    displayGUIBoard();
                                } catch (YouWinException ex) {
                                    youWinAction();
                                    JOptionPane.showMessageDialog(GUI.this,
                                            "You win!");
                                    newGame();
                                }
                            }
                        }
                    }
                });


                grid.add(buttonBoardList[i][j]);
            }
        }
    }

    public void displayGUIBoard() {
        for (int i = 0; i < this.board.getSize(); i++) {
            for (int j = 0; j < this.board.getSize(); j++) {
                displayCell(i, j);
            }
        }
        FlagsCount.setText(String.valueOf(board.getFlagsCount()));
    }

    private void displayCell(int x, int y){
        Cell cell = this.board.getCellClone(x,y);
        int countOfBomb = this.board.howManyBombsAround(x,y);

        if (cell.isOpen() && cell.hasBomb()){

            this.buttonBoardList[x][y].setIcon(_prepareIcon("src/lab_5/saper/view/Bomb.png"));
            return;
        }
        if (cell.hasFlag() && !cell.isOpen()){
            this.buttonBoardList[x][y].setIcon(_prepareIcon("src/lab_5/saper/view/Flag.png"));
            return;
        }
        if (!cell.isOpen()){
            this.buttonBoardList[x][y].setIcon(_prepareIcon("src/lab_5/saper/view/ClosedCell.png"));
            return;
        }
        if (countOfBomb > 0){
            this.buttonBoardList[x][y].setIcon(null);
            this.buttonBoardList[x][y].setText("" + countOfBomb + "");
            this.buttonBoardList[x][y].setEnabled(false);
            return;
        }
        this.buttonBoardList[x][y].setIcon(null);
        this.buttonBoardList[x][y].setText("");
        this.buttonBoardList[x][y].setEnabled(false);


    }

    private ImageIcon _prepareIcon(String filename) {
        ImageIcon icon = new ImageIcon(filename);
        Image tmp = icon.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_DEFAULT);
        icon = new ImageIcon(tmp);
        return icon;
    }

    private void clearAllButtons(){
        for (JButton[] jButtons : this.buttonBoardList) {
            for (int j = 0; j < this.buttonBoardList.length; j++) {
                jButtons[j].setEnabled(true);
                jButtons[j].setText("");
                jButtons[j].setIcon(null);
            }
        }
    }


    private void newGame(){
        board = new Board(board.getSize(), board.getBombCount());
        clearAllButtons();
        displayGUIBoard();
    }


    public void gameOverAction() {
        System.out.println("game over");
        displayGUIBoard();
    }

    public void youWinAction() {
        System.out.println("you win");
        displayGUIBoard();
    }


    private JMenu createFileMenu(){
        JMenu fileMenu = new JMenu("File");


        JMenuItem newGame = new JMenuItem("New Game");
        JMenuItem configBoard = new JMenuItem("Config Board");
        JMenuItem open = new JMenuItem("Open");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem saveAs = new JMenuItem("Save ass");
        JMenuItem exit = new JMenuItem("Exit");

        newGame.setMnemonic('n');
        configBoard.setMnemonic('c');
        open.setMnemonic('o');
        save.setMnemonic('s');
        exit.setMnemonic('e');

        open.addActionListener(new OpenBoard());
        save.addActionListener(new SaveBoard());
        configBoard.addActionListener(new ConfigBoard());
        newGame.addActionListener(el -> newGame());
        exit.addActionListener(el -> System.exit(0));


        fileMenu.add(newGame);
        fileMenu.add(configBoard);
        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.add(exit);

        return fileMenu;

    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");

        JMenuItem about = new JMenuItem("About");

        about.setMnemonic('a');

        about.addActionListener(el -> JOptionPane.showMessageDialog(GUI.this,
            "I write this with java and swing!"));

        helpMenu.add(about);

        return helpMenu;
    }



    class OpenBoard implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser choicer = new JFileChooser();
            int rVal = choicer.showOpenDialog(GUI.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                System.out.println(choicer.getSelectedFile().toPath());
                try {
                    board = Board.loadBoardFromFile(choicer.getSelectedFile().toPath().toString());
                    createGUIBoard();
                    displayGUIBoard();
                    GUI.this.pack();
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
            if (rVal == JFileChooser.CANCEL_OPTION) {
                System.out.println("Rejected");
            }
        }
    }

    class SaveBoard implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser choicer = new JFileChooser();
            int rVal = choicer.showSaveDialog(GUI.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                System.out.println(choicer.getSelectedFile().toPath());
                try {
                    board.saveBoardToFile(choicer.getSelectedFile().toPath().toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (rVal == JFileChooser.CANCEL_OPTION) {
                System.out.println("Rejected");
            }
        }
    }

    class ConfigBoard implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // create a dialog Box
            JDialog d = new JDialog(GUI.this, "dialog Box");

            d.setModal(true);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            d.setContentPane(panel);

            JLabel boardSizeLabel = new JLabel("Board size");
            SpinnerModel sizeModel = new SpinnerNumberModel(15, 5, 30, 1);
            JSpinner sizeSpinner = new JSpinner(sizeModel);
            JLabel bombCountLabel = new JLabel("bomb count");
            SpinnerModel countModel = new SpinnerNumberModel(20, 1, 500, 1);
            JSpinner bombCountSpinner = new JSpinner(countModel);
            JButton button = new JButton("OK");

            button.addActionListener(e1 -> {
            int boardSize = (int)sizeSpinner.getValue();
            int countOfBomb = (int)bombCountSpinner.getValue();
                board = new Board(boardSize, countOfBomb);
                createGUIBoard();
                displayGUIBoard();
                GUI.this.pack();
                d.setVisible(false);
            });

            d.getContentPane().add(boardSizeLabel);
            d.getContentPane().add(sizeSpinner);
            d.getContentPane().add(bombCountLabel);
            d.getContentPane().add(bombCountSpinner);
            d.getContentPane().add(button);

            d.pack();

            d.setVisible(true);
        }}


    public static void main(String[] args) {
//        JFrame.setDefaultLookAndFeelDecorated(true);
        Board b = new Board(15, 15);
        new GUI(b);
    }
}