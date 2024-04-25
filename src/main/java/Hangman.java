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

    private JList list;

    // word that player has to guess
    String word;

    // letter that player guessed
    private char guessedLetter;

    // number of mistakes
    int mistakes = 0;

    Random rand = new Random();
    DefaultListModel model;

    public Hangman() throws IOException {
        initComponents();

        RandomWord();

        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (inputField.getText().equals(word)) {
                    FillWholeWord();
                    Won();
                }

                else {
                    try {
                        guessedLetter = inputField.getText().charAt(0);
                    } catch (StringIndexOutOfBoundsException e) {
                        UpdateMistakes();
                        if (mistakes == 10) Lost();
                        throw new RuntimeException(e);
                    }

                    boolean contained = false;

                    for (int i = 0; i < word.length(); i++) {
                        if (Objects.equals(word.toCharArray()[i], guessedLetter)) {
                            contained = true;

                            // replacing underscores that contains guessed letter
                            model.setElementAt(guessedLetter, i);
                        }
                    }

                    // increasing indicator of mistakes
                    if (!contained) {
                        UpdateMistakes();
                    }

                    // clearing input field for more user-friendly feel
                    inputField.setText("");

                    if (mistakes == 10) Lost();

                    if (!model.contains("_")) Won();
                }
            }

            private void Won() {
                inputField.setText("You won!");
                inputField.setEnabled(false);
                guessButton.setEnabled(false);

                // asking for nickname
                String name = JOptionPane.showInputDialog("Set your nickname:");
                new HallOfFame(mistakes, name);
            }

            private void Lost() {
                inputField.setText("You lost!");
                inputField.setEnabled(false);
                guessButton.setEnabled(false);
            }

            private void UpdateMistakes() {
                mistakes++;
                mistakesDone.setText(String.valueOf(mistakes));
            }

            private void FillWholeWord() {
                for (int x = 0; x < word.length(); x++) {
                    model.setElementAt(word.charAt(x), x);
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
                    RandomWord();
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
    public void RandomWord() throws IOException {
        // loading word list
        Path path = Paths.get("src/main/wordList.txt");
        List<String> lines = Files.readAllLines(path);
        word = lines.get(rand.nextInt(lines.size()));

        model = new DefaultListModel();
        list.setModel(model);

        // setting the number of underscores to number of letters in word
        for (int i = 0; i < word.length(); i++) {
            model.addElement("_");
        }
    }
}