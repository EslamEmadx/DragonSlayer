import javax.media.opengl.GL;
import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.*;
import java.util.List;

public class MenuManager {
    private int screenWidth, screenHeight;
    private TextureManager textureManager;
    private TextRenderer titleRenderer;
    private TextRenderer textRenderer;
    private TextRenderer smallRenderer;
    
    private Rectangle startButton, multiplayerButton, instructionsButton, highscoresButton, exitButton;
    private Rectangle resumeButton, menuButton, backButton;
    private Rectangle[] levelButtons;
    
    public MenuManager(int width, int height, TextureManager tm) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.textureManager = tm;
        
        titleRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 48));
        textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 24));
        smallRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 18));
        
        int btnWidth = 300;
        int btnHeight = 60;
        int centerX = width / 2 - btnWidth / 2;
        
        int shiftX = 0;  
        int shiftY = 0; 
        
        startButton = new Rectangle(centerX + shiftX, 250 + shiftY, btnWidth, btnHeight);
        multiplayerButton = new Rectangle(centerX + shiftX, 330 + shiftY, btnWidth, btnHeight);
        instructionsButton = new Rectangle(centerX + shiftX, 410 + shiftY, btnWidth, btnHeight);
        highscoresButton = new Rectangle(centerX + shiftX, 490 + shiftY, btnWidth, btnHeight);
        exitButton = new Rectangle(centerX + shiftX, 570 + shiftY, btnWidth, btnHeight);
        
        resumeButton = new Rectangle(centerX + shiftX, 300 + shiftY, btnWidth, btnHeight);
        menuButton = new Rectangle(centerX + shiftX, 380 + shiftY, btnWidth, btnHeight);
        
        backButton = new Rectangle(50 + shiftX, screenHeight - 100 + shiftY, 150, 50);
        
        levelButtons = new Rectangle[5];
        int levelBtnWidth = 250;
        int levelBtnHeight = 60;
        int levelCenterX = width / 2 - levelBtnWidth / 2;
        int startY = 250;
        for (int i = 0; i < 5; i++) {
            levelButtons[i] = new Rectangle(levelCenterX + shiftX, startY + i * (levelBtnHeight + 20) + shiftY, levelBtnWidth, levelBtnHeight);
        }
    }
    
    public void renderMainMenu(GL gl, int mouseX, int mouseY) {
        drawBackground(gl, "Farm");
        
        drawOverlay(gl);
        
        titleRenderer.beginRendering(screenWidth, screenHeight);
        titleRenderer.setColor(0.9f, 0.9f, 0.1f, 1); 
        drawCenteredText(titleRenderer, "Dragon Slayer", screenHeight - 150);
        titleRenderer.endRendering();
        
        drawModernButton(gl, startButton, "Start Game", mouseX, mouseY, 0.2f, 0.6f, 0.9f);
        drawModernButton(gl, multiplayerButton, "Multiplayer", mouseX, mouseY, 0.3f, 0.7f, 0.5f);
        drawModernButton(gl, instructionsButton, "Instructions", mouseX, mouseY, 0.8f, 0.5f, 0.2f);
        drawModernButton(gl, highscoresButton, "High Scores", mouseX, mouseY, 0.7f, 0.3f, 0.8f);
        drawModernButton(gl, exitButton, "Exit", mouseX, mouseY, 0.9f, 0.3f, 0.3f);
    }
    
    public void renderUsernameInput(GL gl, String userName) {
        drawBackground(gl, "menu");
        drawOverlay(gl);
        
        textRenderer.beginRendering(screenWidth, screenHeight);
        textRenderer.setColor(1, 1, 1, 1);
        drawCenteredText(textRenderer, "Enter Your Name:", screenHeight - 300);
        
        textRenderer.setColor(0.9f, 0.9f, 0.1f, 1);
        drawCenteredText(textRenderer, userName + "_", screenHeight - 350);
        
        textRenderer.setColor(0.7f, 0.7f, 0.7f, 1);
        drawCenteredText(textRenderer, "Press ENTER to continue", screenHeight - 450);
        textRenderer.endRendering();
    }
    
    public void renderInstructions(GL gl) {
        drawBackground(gl, "Farm");
        drawOverlay(gl);
        
        titleRenderer.beginRendering(screenWidth, screenHeight);
        titleRenderer.setColor(0.9f, 0.9f, 0.1f, 1);
        drawCenteredText(titleRenderer, "HOW TO PLAY", screenHeight - 100);
        titleRenderer.endRendering();
        
        smallRenderer.beginRendering(screenWidth, screenHeight);
        smallRenderer.setColor(1, 1, 1, 1);
        int y = screenHeight - 200;
        drawCenteredText(smallRenderer, "KEYBOARD PLAYER:", y); y -= 30;
        drawCenteredText(smallRenderer, "• Arrow Keys - Move", y); y -= 25;
        drawCenteredText(smallRenderer, "• Left/Right Click - Throw Sword (Single Player)", y); y -= 25;
        drawCenteredText(smallRenderer, "• Space Button - Throw Sword (Multiplayer)", y); y -= 40;
        drawCenteredText(smallRenderer, "MOUSE PLAYER:", y); y -= 30;
        drawCenteredText(smallRenderer, "• Move Mouse - Character Follows", y); y -= 25;
        drawCenteredText(smallRenderer, "• Left/Right Click - Throw Sword (Multiplayer Only)", y); y -= 40;
        drawCenteredText(smallRenderer, "Defeat all enemies to win!", y);
        smallRenderer.endRendering();
        
        drawModernButton(gl, backButton, "Back", 0, 0, 0.7f, 0.7f, 0.7f);
    }
    
    public void renderLevelSelect(GL gl, int mouseX, int mouseY) {
        drawBackground(gl, "menu");
        drawOverlay(gl);
        
        titleRenderer.beginRendering(screenWidth, screenHeight);
        titleRenderer.setColor(0.9f, 0.9f, 0.1f, 1);
        drawCenteredText(titleRenderer, "SELECT LEVEL", screenHeight - 180);
        titleRenderer.endRendering();
        
        float[][] levelColors = {
            {0.2f, 0.6f, 0.9f},
            {0.3f, 0.7f, 0.5f},
            {0.8f, 0.5f, 0.2f},
            {0.7f, 0.3f, 0.8f},
            {0.9f, 0.3f, 0.3f} 
        };
        
        for (int i = 0; i < 5; i++) {
            float[] color = levelColors[i];
            drawModernButton(gl, levelButtons[i], "Level " + (i + 1), mouseX, mouseY, color[0], color[1], color[2]);
        }
        
        drawModernButton(gl, backButton, "Back", mouseX, mouseY, 0.7f, 0.7f, 0.7f);
    }
    
    public void renderPauseMenu(GL gl, int mouseX, int mouseY) {
        gl.glColor4f(0, 0, 0, 0.8f);
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(0, 0);
        gl.glVertex2f(screenWidth, 0);
        gl.glVertex2f(screenWidth, screenHeight);
        gl.glVertex2f(0, screenHeight);
        gl.glEnd();
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1); 
        
        titleRenderer.beginRendering(screenWidth, screenHeight);
        titleRenderer.setColor(0.9f, 0.9f, 0.1f, 1);
        drawCenteredText(titleRenderer, "PAUSED", screenHeight - 200);
        titleRenderer.endRendering();
        
        drawModernButton(gl, resumeButton, "Resume", mouseX, mouseY, 0.2f, 0.7f, 0.3f);
        drawModernButton(gl, menuButton, "Main Menu", mouseX, mouseY, 0.8f, 0.5f, 0.2f);
    }
    
    public void renderWinScreen(GL gl, int score) {
        drawBackground(gl, "victory");
        drawOverlay(gl);
        
        titleRenderer.beginRendering(screenWidth, screenHeight);
        titleRenderer.setColor(1, 0.84f, 0, 1);
        drawCenteredText(titleRenderer, "VICTORY!", screenHeight - 250);
        titleRenderer.endRendering();
        
        textRenderer.beginRendering(screenWidth, screenHeight);
        textRenderer.setColor(1, 1, 1, 1);
        drawCenteredText(textRenderer, "Score: " + score, screenHeight - 320);
        textRenderer.draw("🎉", (screenWidth / 2) - 100, screenHeight - 320);
        textRenderer.draw("🎉", (screenWidth / 2) + 60, screenHeight - 320);
        textRenderer.endRendering();
        
        drawModernButton(gl, backButton, "Menu", 0, 0, 0.9f, 0.9f, 0.1f);
    }
    
    public void renderGameOverScreen(GL gl, int score) {
        drawBackground(gl, "gameover");
        drawOverlay(gl);
        
        titleRenderer.beginRendering(screenWidth, screenHeight);
        titleRenderer.setColor(1, 0.3f, 0.3f, 1);
        drawCenteredText(titleRenderer, "GAME OVER", screenHeight - 250);
        titleRenderer.endRendering();
        
        textRenderer.beginRendering(screenWidth, screenHeight);
        textRenderer.setColor(1, 1, 1, 1);
        drawCenteredText(textRenderer, "Score: " + score, screenHeight - 320);
        textRenderer.endRendering();
        
        drawModernButton(gl, backButton, "Menu", 0, 0, 0.7f, 0.7f, 0.7f);
    }
    
    public void renderHighScores(GL gl, List<ScoreEntry> scores) {
        drawBackground(gl, "menu");
        drawOverlay(gl);
        
        titleRenderer.beginRendering(screenWidth, screenHeight);
        titleRenderer.setColor(0.9f, 0.9f, 0.1f, 1);
        drawCenteredText(titleRenderer, "HIGH SCORES", screenHeight - 150);
        titleRenderer.endRendering();
        
        textRenderer.beginRendering(screenWidth, screenHeight);
        textRenderer.setColor(1, 1, 1, 1);
        
        int y = screenHeight - 250;
        for (int i = 0; i < Math.min(10, scores.size()); i++) {
            ScoreEntry entry = scores.get(i);
            String text = (i + 1) + ". " + entry.getName() + " - " + entry.getScore();
            
            if (i == 0) textRenderer.setColor(1, 0.84f, 0, 1);
            else if (i == 1) textRenderer.setColor(0.75f, 0.75f, 0.75f, 1);
            else if (i == 2) textRenderer.setColor(0.8f, 0.5f, 0.2f, 1);
            else textRenderer.setColor(1, 1, 1, 1);
            
            drawCenteredText(textRenderer, text, y);
            y -= 40;
        }
        textRenderer.endRendering();
        
        drawModernButton(gl, backButton, "Back", 0, 0, 0.7f, 0.7f, 0.7f);
    }
    
    private void drawBackground(GL gl, String textureName) {
        textureManager.drawTexture(gl, textureManager.getTexture(textureName), 0, 0, screenWidth, screenHeight);
    }
    
    private void drawOverlay(GL gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4f(0, 0, 0, 0.5f);
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(0, 0);
        gl.glVertex2f(screenWidth, 0);
        gl.glVertex2f(screenWidth, screenHeight);
        gl.glVertex2f(0, screenHeight);
        gl.glEnd();
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);
        gl.glDisable(GL.GL_BLEND);
    }
    
    private void drawModernButton(GL gl, Rectangle rect, String text, int mouseX, int mouseY, float r, float g, float b) {
        boolean hover = rect.contains(mouseX, mouseY);
        float cornerRadius = 15.0f;
        
        gl.glColor3f(1, 1, 1);
        
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL.GL_TEXTURE_2D);
        
        if (hover) {
            gl.glColor4f(0.1f, 0.1f, 0.1f, 0.4f);
            drawRoundedRect(gl, rect.x + 4, rect.y - 4, rect.width, rect.height, cornerRadius);
        }

        if (hover) {

            gl.glColor4f(r + 0.2f, g + 0.2f, b + 0.2f, 0.9f);
        } else {
            gl.glColor4f(r, g, b, 0.9f);
        }
        drawRoundedRect(gl, rect.x, rect.y, rect.width, rect.height, cornerRadius);
        

        gl.glColor4f(r - 0.2f, g - 0.2f, b - 0.2f, 1.0f);
        gl.glLineWidth(2.0f);
        drawRoundedRectOutline(gl, rect.x, rect.y, rect.width, rect.height, cornerRadius);
        
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_BLEND);
        
        textRenderer.beginRendering(screenWidth, screenHeight);
        if (hover) {
            textRenderer.setColor(1, 1, 1, 1); 
        } else {
            textRenderer.setColor(0.95f, 0.95f, 0.95f, 1);
        }
        int textWidth = (int) textRenderer.getBounds(text).getWidth();
        
        int textOffsetX = 30;
        int textOffsetY = -10;
        
        textRenderer.draw(text, 
                         rect.x + (rect.width - textWidth) / 2 + textOffsetX, 
                         screenHeight - rect.y - rect.height / 2 - 8 + textOffsetY); 
        textRenderer.endRendering();
        
        gl.glColor3f(1, 1, 1);
    }
    
    private void drawRoundedRect(GL gl, float x, float y, float width, float height, float radius) {
        int segments = 20;
        
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x + radius, y);
        gl.glVertex2f(x + width - radius, y);
        gl.glVertex2f(x + width - radius, y + height);
        gl.glVertex2f(x + radius, y + height);
        gl.glEnd();
        
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y + radius);
        gl.glVertex2f(x + radius, y + radius);
        gl.glVertex2f(x + radius, y + height - radius);
        gl.glVertex2f(x, y + height - radius);
        
        gl.glVertex2f(x + width - radius, y + radius);
        gl.glVertex2f(x + width, y + radius);
        gl.glVertex2f(x + width, y + height - radius);
        gl.glVertex2f(x + width - radius, y + height - radius);
        gl.glEnd();
        
        drawRoundedCorner(gl, x + radius, y + radius, radius, 180, 270, segments);
        drawRoundedCorner(gl, x + width - radius, y + radius, radius, 270, 360, segments);
        drawRoundedCorner(gl, x + radius, y + height - radius, radius, 90, 180, segments);
        drawRoundedCorner(gl, x + width - radius, y + height - radius, radius, 0, 90, segments);
    }
    
    private void drawRoundedCorner(GL gl, float cx, float cy, float radius, 
                                   float startAngle, float endAngle, int segments) {
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glVertex2f(cx, cy);
        
        float angleStep = (endAngle - startAngle) / segments;
        for (int i = 0; i <= segments; i++) {
            float angle = (float) Math.toRadians(startAngle + i * angleStep);
            float x = cx + radius * (float) Math.cos(angle);
            float y = cy + radius * (float) Math.sin(angle);
            gl.glVertex2f(x, y);
        }
        gl.glEnd();
    }
    
    private void drawRoundedRectOutline(GL gl, float x, float y, float width, float height, float radius) {
        int segments = 20;
        
        gl.glBegin(GL.GL_LINE_STRIP);
        
        for (int i = 0; i <= segments; i++) {
            float angle = (float) Math.toRadians(90 + i * 90.0f / segments);
            float px = x + width - radius + radius * (float) Math.cos(angle);
            float py = y + height - radius + radius * (float) Math.sin(angle);
            gl.glVertex2f(px, py);
        }
        
        gl.glVertex2f(x + width, y + radius);
        
        for (int i = 0; i <= segments; i++) {
            float angle = (float) Math.toRadians(0 + i * 90.0f / segments);
            float px = x + width - radius + radius * (float) Math.cos(angle);
            float py = y + radius + radius * (float) Math.sin(angle);
            gl.glVertex2f(px, py);
        }
        
        gl.glVertex2f(x + radius, y);
        
        for (int i = 0; i <= segments; i++) {
            float angle = (float) Math.toRadians(270 + i * 90.0f / segments);
            float px = x + radius + radius * (float) Math.cos(angle);
            float py = y + radius + radius * (float) Math.sin(angle);
            gl.glVertex2f(px, py);
        }
        
        gl.glVertex2f(x, y + height - radius);
        
        for (int i = 0; i <= segments; i++) {
            float angle = (float) Math.toRadians(180 + i * 90.0f / segments);
            float px = x + radius + radius * (float) Math.cos(angle);
            float py = y + height - radius + radius * (float) Math.sin(angle);
            gl.glVertex2f(px, py);
        }
        
        gl.glEnd();
    }
    
    private void drawCenteredText(TextRenderer renderer, String text, int y) {
        int textWidth = (int) renderer.getBounds(text).getWidth();
        renderer.draw(text, (screenWidth - textWidth) / 2, y);
    }
    
    public String getMainMenuAction(int x, int y) {
        if (startButton.contains(x, y)) return "start";
        if (multiplayerButton.contains(x, y)) return "multiplayer";
        if (instructionsButton.contains(x, y)) return "instructions";
        if (highscoresButton.contains(x, y)) return "highscores";
        if (exitButton.contains(x, y)) return "exit";
        return null;
    }
    
    public String getPauseMenuAction(int x, int y) {
        if (resumeButton.contains(x, y)) return "resume";
        if (menuButton.contains(x, y)) return "menu";
        return null;
    }
    
    public int getLevelSelection(int x, int y) {
        for (int i = 0; i < levelButtons.length; i++) {
            if (levelButtons[i].contains(x, y)) {
                return i + 1;
            }
        }
        return -1;
    }
    
    public boolean isBackButton(int x, int y) {
        return backButton.contains(x, y);
    }
}