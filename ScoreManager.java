import java.io.*;
import java.util.*;

public class ScoreManager {
    private List<ScoreEntry> highScores;
    private static final String SCORES_FILE = "Assets/data/highscores.txt";
    
    public ScoreManager() {
        highScores = new ArrayList<>();
        loadScores();
    }
    
    public void saveScore(String playerName, int score) {
        highScores.add(new ScoreEntry(playerName, score));
        Collections.sort(highScores);
        
        if (highScores.size() > 10) {
            highScores = highScores.subList(0, 10);
        }
        
        saveScoresToFile();
    }
    
    public List<ScoreEntry> getHighScores() {
        return new ArrayList<>(highScores);
    }
    
    private void loadScores() {
        try {
            File file = new File(SCORES_FILE);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        String name = parts[0];
                        int score = Integer.parseInt(parts[1]);
                        highScores.add(new ScoreEntry(name, score));
                    }
                }
                reader.close();
            }
        } catch (Exception e) {
            System.err.println("Failed to load high scores");
        }
    }
    
    private void saveScoresToFile() {
        try {
            File file = new File(SCORES_FILE);
            file.getParentFile().mkdirs();
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (ScoreEntry entry : highScores) {
                writer.write(entry.getName() + "," + entry.getScore());
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            System.err.println("Failed to save high scores");
        }
    }
}

class ScoreEntry implements Comparable<ScoreEntry> {
    private String name;
    private int score;
    
    public ScoreEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }
    
    public String getName() {
        return name;
    }
    
    public int getScore() {
        return score;
    }
    
    @Override
    public int compareTo(ScoreEntry other) {
        return Integer.compare(other.score, this.score);
    }
}