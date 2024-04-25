import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class HallOfFame extends JFrame {
    private JPanel frame;
    private JList fameWall;

    DefaultListModel listModel = new DefaultListModel();
    // file with saved hall of fame data
    Path path = Paths.get("src/main/fame.txt");


    public HallOfFame(int mistakes, String name) {
        initComponents();

        // setting format of table text
        String text = mistakes +" mistakes: "+ name +"\n";

        try {
            Files.write(Paths.get(String.valueOf(path)), text.getBytes(), StandardOpenOption.APPEND);

            LoadTable();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HallOfFame() {
        initComponents();

        try {
            LoadTable();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initComponents() {
        setContentPane(frame);
        setTitle("Hangman - Hall of Fame");
        setSize(500, 300);
        setMinimumSize(new Dimension(500, 300));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        fameWall.setModel(listModel);
    }

    public void LoadTable() throws IOException {
        List<String> lines = Files.readAllLines(path);

        // adding all lines from saved file
        for (int i = 0; i < lines.size(); i++) {
            listModel.add(i, lines.get(i));
        }
    }
}