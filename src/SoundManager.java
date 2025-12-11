import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private Map<String, Clip> sounds;
    private Clip currentMusic;
    
    public SoundManager() {
        sounds = new HashMap<>();
        loadAllSounds();
    }
    
    private void loadAllSounds() {
        loadSound("background_music", "Assets/sound/background_music.wav");
        loadSound("menu_music", "Assets/sound/menu_music.wav");
        loadSound("enemy_death", "Assets/sound/enemy_death.wav");
        loadSound("game_over", "Assets/sound/game_over.wav");
        loadSound("player_hurt", "Assets/sound/player_hurt.wav");
        loadSound("sword_hit", "Assets/sound/sword_hit.wav");
        loadSound("victory", "Assets/sound/victory.wav");
    }
    
    private void loadSound(String name, String path) {
        try {
            File soundFile = new File(path);
            if (soundFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                sounds.put(name, clip);
            }
        } catch (Exception e) {
            System.err.println("Failed to load sound: " + path);
        }
    }
    
    public void playSound(String name) {
        Clip clip = sounds.get(name);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    public void playMusic(String name, boolean loop) {
        stopMusic();
        currentMusic = sounds.get(name);
        if (currentMusic != null) {
            currentMusic.setFramePosition(0);
            if (loop) {
                currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                currentMusic.start();
            }
        }
    }
    
    public void stopMusic() {
        if (currentMusic != null && currentMusic.isRunning()) {
            currentMusic.stop();
        }
    }
}