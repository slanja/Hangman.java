import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Hangman extends JFrame {
    private JPanel frame;

    // mistake indicator
    private JLabel mistakesDone;

    private JTextField inputField;

    private JButton hallOfFameButton;
    private JButton guessButton;
    private JButton resetButton;

    private JList<String> list;

    // word that player has to guess
    String word;

    // letter that player guessed
    private char guessedLetter;

    // number of mistakes
    int mistakes = 0;

    Random rand = new Random();
    DefaultListModel<String> listModel;

    public Hangman() throws IOException {
        initComponents();

        randomWord();

        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (inputField.getText().toLowerCase().equals(word)) {
                    fillWholeWord();
                    won();
                } else if (inputField.getText().length() > 2) {
                    inputField.setText("");
                    updateMistakes();
                }

                else {
                    try {
                        guessedLetter = inputField.getText().toLowerCase().charAt(0);
                    } catch (StringIndexOutOfBoundsException e) {
                        updateMistakes();
                        if (mistakes == 10) lost();
                        throw new RuntimeException(e);
                    }

                    boolean contained = false;

                    for (int i = 0; i < word.length(); i++) {
                        if (Objects.equals(word.toCharArray()[i], guessedLetter)) {
                            contained = true;

                            // replacing underscores that contains guessed letter
                            listModel.setElementAt(String.valueOf(guessedLetter), i);
                        }
                    }

                    // increasing indicator of mistakes
                    if (!contained) {
                        updateMistakes();
                    }

                    // clearing input field for more user-friendly feel
                    inputField.setText("");

                    if (mistakes == 10) lost();

                    if (!listModel.contains("_")) won();
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    inputField.setEnabled(true);
                    guessButton.setEnabled(true);
                    inputField.setText("");
                    mistakes = 0;

                    // generating new word
                    randomWord();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        hallOfFameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new HallOfFame();
            }
        });
    }

    private void initComponents() {
        setContentPane(frame);
        setTitle("Hangman");
        setSize(600, 300);
        setMinimumSize(new Dimension(600, 300));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // generates a random word
    public void randomWord() throws IOException {
        // loading word list
        Path path = Paths.get("src/main/wordList.txt");
        List<String> lines = Files.readAllLines(path);
        word = lines.get(rand.nextInt(lines.size()));

        listModel = new DefaultListModel<>();
        list.setModel(listModel);

        // setting the number of underscores to number of letters in word
        for (int i = 0; i < word.length(); i++) {
            listModel.addElement("_");
        }
    }

    private void won() {
        inputField.setText("You won!");
        inputField.setEnabled(false);
        guessButton.setEnabled(false);

        // asking for nickname
        String name = JOptionPane.showInputDialog("Set your nickname:");
        new HallOfFame(mistakes, name);
    }

    private void lost() {
        inputField.setText("You lost!");
        inputField.setEnabled(false);
        guessButton.setEnabled(false);
    }

    private void updateMistakes() {
        mistakes++;
        mistakesDone.setText(String.valueOf(mistakes) + " mistakes");
    }

    private void fillWholeWord() {
        for (int x = 0; x < word.length(); x++) {
            listModel.setElementAt(String.valueOf(word.charAt(x)), x);
        }
    }
}