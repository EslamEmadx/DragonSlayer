import javax.media.opengl.GL;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;

public class GameEngine {
    public enum GameState {
        MENU, USERNAME_INPUT, INSTRUCTIONS, LEVEL_SELECT, GAMEPLAY, PAUSE, WIN, GAMEOVER, HIGHSCORES
    }
    
    private GameState currentState;
    private int screenWidth, screenHeight;
    private GL glContext;
    
    private TextureManager textureManager;
    private SoundManager soundManager;
    private MenuManager menuManager;
    private Level currentLevel;
    private ScoreManager scoreManager;
    
    private String userName = "";
    private boolean singlePlayer = true;
    private int selectedLevel = 1;
    private int mouseX, mouseY;
    
    private Set<Integer> keysPressed;
    

    public GameEngine(int width, int height, GL gl) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.glContext = gl;
        this.currentState = GameState.MENU;
        this.keysPressed = new HashSet<>();
        
        textureManager = new TextureManager();
        textureManager.setGL(glContext);
        textureManager.loadAllTextures();
        
        soundManager = new SoundManager();
        scoreManager = new ScoreManager();
        menuManager = new MenuManager(width, height, textureManager);
        
        soundManager.playMusic("menu_music", true);
    }
    
    public void update() {
        switch (currentState) {
            case GAMEPLAY:
                if (currentLevel != null) {
                    currentLevel.update(keysPressed, mouseX, mouseY);
                    
                    if (currentLevel.isLevelComplete()) {
                        if (selectedLevel < 5) {
                            selectedLevel++;
                            startLevel();
                        } else {
                            currentState = GameState.WIN;
                            soundManager.stopMusic();
                            soundManager.playSound("victory");
                            scoreManager.saveScore(userName, currentLevel.getScore());
                        }
                    }
                    
                    if (currentLevel.isGameOver()) {
                        currentState = GameState.GAMEOVER;
                        soundManager.stopMusic();
                        soundManager.playSound("game_over");
                        scoreManager.saveScore(userName, currentLevel.getScore());
                    }
                }
                break;
        }
    }
    
    public void render(GL gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        switch (currentState) {
            case MENU:
                menuManager.renderMainMenu(gl, mouseX, mouseY);
                break;
            case USERNAME_INPUT:
                menuManager.renderUsernameInput(gl, userName);
                break;
            case INSTRUCTIONS:
                menuManager.renderInstructions(gl);
                break;
            case LEVEL_SELECT:
                menuManager.renderLevelSelect(gl, mouseX, mouseY);
                break;
            case GAMEPLAY:
                if (currentLevel != null) {
                    currentLevel.render(gl);
                }
                break;
            case PAUSE:
                if (currentLevel != null) {
                    currentLevel.render(gl);
                }
                menuManager.renderPauseMenu(gl, mouseX, mouseY);
                break;
            case WIN:
                menuManager.renderWinScreen(gl, currentLevel != null ? currentLevel.getScore() : 0);
                break;
            case GAMEOVER:
                menuManager.renderGameOverScreen(gl, currentLevel != null ? currentLevel.getScore() : 0);
                break;
            case HIGHSCORES:
                menuManager.renderHighScores(gl, scoreManager.getHighScores());
                break;
        }
        
        gl.glColor3f(1.0f, 1.0f, 1.0f);
    }
    
    public void keyPressed(int keyCode) {
        keysPressed.add(keyCode);
        
        if (currentState == GameState.USERNAME_INPUT) {
            if (keyCode == KeyEvent.VK_BACK_SPACE && userName.length() > 0) {
                userName = userName.substring(0, userName.length() - 1);
            } else if (keyCode == KeyEvent.VK_ENTER && userName.length() > 0) {
                currentState = GameState.LEVEL_SELECT;
            } else if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z && userName.length() < 15) {
                userName += (char) keyCode;
            } else if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9 && userName.length() < 15) {
                userName += (char) keyCode;
            }
        } else if (currentState == GameState.GAMEPLAY) {
            if (currentLevel != null) {
                currentLevel.handleKeyPress(keyCode);
            }
            
            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_P) {
                currentState = GameState.PAUSE;
            }
        } else if (currentState == GameState.PAUSE) {
            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_P) {
                currentState = GameState.GAMEPLAY;
            }
        }
    }
    
    public void keyReleased(int keyCode) {
        keysPressed.remove(keyCode);
    }
    
    public void mousePressed(int x, int y, int button) {
        mouseX = x;
        mouseY = y;
        
        switch (currentState) {
            case MENU:
                String menuAction = menuManager.getMainMenuAction(x, y);
                if (menuAction != null) {
                    handleMenuAction(menuAction);
                }
                break;
            case LEVEL_SELECT:
                int level = menuManager.getLevelSelection(x, y);
                if (level > 0) {
                    selectedLevel = level;
                    startLevel();
                } else if (menuManager.isBackButton(x, y)) {
                    currentState = GameState.MENU;
                }
                break;
            case INSTRUCTIONS:
                if (menuManager.isBackButton(x, y)) {
                    currentState = GameState.MENU;
                }
                break;
            case HIGHSCORES:
                if (menuManager.isBackButton(x, y)) {
                    currentState = GameState.MENU;
                }
                break;
            case PAUSE:
                String pauseAction = menuManager.getPauseMenuAction(x, y);
                if (pauseAction != null) {
                    handlePauseAction(pauseAction);
                }
                break;
            case WIN:
            case GAMEOVER:
                if (menuManager.isBackButton(x, y)) {
                    currentState = GameState.MENU;
                    soundManager.playMusic("menu_music", true);
                }
                break;
            case GAMEPLAY:
                if (currentLevel != null) {
                    currentLevel.handleMouseClick(button);
                }
                break;
        }
    }
    
    public void mouseMoved(int x, int y) {
        mouseX = x;
        mouseY = y;
    }
    
    private void handleMenuAction(String action) {
        switch (action) {
            case "start":
                singlePlayer = true;
                currentState = GameState.USERNAME_INPUT;
                userName = "";
                break;
            case "multiplayer":
                singlePlayer = false;
                currentState = GameState.USERNAME_INPUT;
                userName = "";
                break;
            case "instructions":
                currentState = GameState.INSTRUCTIONS;
                break;
            case "highscores":
                currentState = GameState.HIGHSCORES;
                break;
            case "exit":
                System.exit(0);
                break;
        }
    }
    
    private void handlePauseAction(String action) {
        switch (action) {
            case "resume":
                currentState = GameState.GAMEPLAY;
                break;
            case "menu":
                currentState = GameState.MENU;
                soundManager.stopMusic();
                soundManager.playMusic("menu_music", true);
                break;
        }
    }
    
    private void startLevel() {
        soundManager.stopMusic();
        soundManager.playMusic("background_music", true);
        currentLevel = new Level(screenWidth, screenHeight, selectedLevel, singlePlayer, textureManager, soundManager);
        currentState = GameState.GAMEPLAY;
    }
}