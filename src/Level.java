import javax.media.opengl.GL;
import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class Level {
    private int screenWidth, screenHeight;
    private int levelNumber;
    private boolean singlePlayer;
    
    private TextureManager textureManager;
    private SoundManager soundManager;
    
    private Player player1;
    private Player player2;
    
    private List<Enemy> enemies;
    private List<Projectile> projectiles;
    private List<Obstacle> obstacles;
    
    private int totalEnemies;
    private int enemiesKilled;
    private int score;
    private long startTime;
    
    private TextRenderer hudRenderer;
    
    public Level(int width, int height, int level, boolean single, TextureManager tm, SoundManager sm) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.levelNumber = level;
        this.singlePlayer = single;
        this.textureManager = tm;
        this.soundManager = sm;
        
        this.enemies = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        
        this.enemiesKilled = 0;
        this.score = 0;
        this.startTime = System.currentTimeMillis();
        
        this.hudRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 20));
        
        initLevel();
    }
    
    private void initLevel() {
        Random rand = new Random();
        
        int obstacleCount = 2 + levelNumber * 2;
        for (int i = 0; i < obstacleCount; i++) {
            float x = 100 + rand.nextInt(screenWidth - 200);
            float y = 100 + rand.nextInt(screenHeight - 200);
            int type = 1 + rand.nextInt(10);
            obstacles.add(new Obstacle(x, y, type, textureManager));
        }
        
        float p1X = findSafeSpawnPosition(screenWidth / 2 - 100, screenHeight / 2);
        float p1Y = findSafeSpawnPosition(screenWidth / 2 - 100, screenHeight / 2);
        player1 = new Player(p1X, p1Y, "female", true, textureManager);
        
        if (!singlePlayer) {
            float p2X = findSafeSpawnPosition(screenWidth / 2 + 100, screenHeight / 2);
            float p2Y = findSafeSpawnPosition(screenWidth / 2 + 100, screenHeight / 2);
            player2 = new Player(p2X, p2Y, "male", false, textureManager);
        }
        
        totalEnemies = 5 * levelNumber;
        spawnEnemies(totalEnemies);
    }
    
    private float findSafeSpawnPosition(float preferredX, float preferredY) {
        Random rand = new Random();
        int maxAttempts = 50;
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            float testX = preferredX + rand.nextInt(200) - 100;
            float testY = preferredY + rand.nextInt(200) - 100;
            
            testX = Math.max(50, Math.min(testX, screenWidth - 114));
            testY = Math.max(50, Math.min(testY, screenHeight - 114));
            
            boolean isSafe = true;
            for (Obstacle obs : obstacles) {
                float dx = Math.abs((testX + 32) - (obs.getCollisionX() + obs.getCollisionWidth() / 2));
                float dy = Math.abs((testY + 32) - (obs.getCollisionY() + obs.getCollisionHeight() / 2));
                float minDistance = 100;
                
                if (dx < minDistance && dy < minDistance) {
                    isSafe = false;
                    break;
                }
            }
            
            if (isSafe) {
                return (attempt % 2 == 0) ? testX : testY;
            }
        }
        
        return (preferredX == screenWidth / 2 - 100) ? screenWidth / 2 : screenHeight / 2;
    }
    
    private void spawnEnemies(int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            float x, y;
            
            int side = rand.nextInt(4);
            switch (side) {
                case 0: 
                    x = rand.nextInt(screenWidth);
                    y = -50;
                    break;
                case 1: 
                    x = screenWidth + 50;
                    y = rand.nextInt(screenHeight);
                    break;
                case 2:
                    x = rand.nextInt(screenWidth);
                    y = screenHeight + 50;
                    break;
                default:
                    x = -50;
                    y = rand.nextInt(screenHeight);
                    break;
            }
            
            String[] types = {"red_dragon", "green_dragon", "blue_dragon"};
            String type = types[rand.nextInt(types.length)];
            
            Enemy.AIBehavior[] behaviors = Enemy.AIBehavior.values();
            Enemy.AIBehavior behavior = behaviors[rand.nextInt(behaviors.length)];
            
            enemies.add(new Enemy(x, y, type, behavior, textureManager));
        }
    }
    
    public void update(Set<Integer> keysPressed, int mouseX, int mouseY) {
        if (player1.isAlive()) {
            float dx = 0, dy = 0;
            if (keysPressed.contains(KeyEvent.VK_LEFT)) dx -= 1;
            if (keysPressed.contains(KeyEvent.VK_RIGHT)) dx += 1;
            if (keysPressed.contains(KeyEvent.VK_UP)) dy -= 1;
            if (keysPressed.contains(KeyEvent.VK_DOWN)) dy += 1;
            player1.move(dx, dy, obstacles, screenWidth, screenHeight);
            player1.update();
        }
        
        if (!singlePlayer && player2 != null && player2.isAlive()) {
            player2.moveTowards(mouseX, mouseY, obstacles, screenWidth, screenHeight);
            player2.update();
        }
        
        List<Player> players = new ArrayList<>();
        if (player1.isAlive()) players.add(player1);
        if (player2 != null && player2.isAlive()) players.add(player2);
        
        Iterator<Enemy> enemyIter = enemies.iterator();
        while (enemyIter.hasNext()) {
            Enemy enemy = enemyIter.next();
            enemy.update(players, obstacles, screenWidth, screenHeight);
            
            for (Player player : players) {
                if (player.isAlive() && enemy.collidesWith(player)) {
                    player.takeDamage();
                    soundManager.playSound("player_hurt");
                    enemy.die();
                    soundManager.playSound("enemy_death");
                    break;
                }
            }
            
            if (!enemy.isAlive()) {
                enemiesKilled++;
                score += 10;
                enemyIter.remove();
            }
        }
        
        Iterator<Projectile> projIter = projectiles.iterator();
        while (projIter.hasNext()) {
            Projectile proj = projIter.next();
            proj.update();
            
            for (Obstacle obs : obstacles) {
                if (proj.collidesWith(obs)) {
                    proj.setActive(false);
                    break;
                }
            }
            
            for (Enemy enemy : enemies) {
                if (proj.isActive() && proj.collidesWith(enemy)) {
                    enemy.die();
                    soundManager.playSound("enemy_death");
                    proj.setActive(false);
                    break;
                }
            }
            
            if (!proj.isActive()) {
                projIter.remove();
            }
        }
    }
    
    public void render(GL gl) {
        textureManager.drawTexture(gl, textureManager.getTexture("ground"), 0, 0, screenWidth, screenHeight);
        
        for (Obstacle obs : obstacles) {
            obs.render(gl, textureManager);
        }
        
        for (Projectile proj : projectiles) {
            proj.render(gl, textureManager);
        }
        
        for (Enemy enemy : enemies) {
            enemy.render(gl, textureManager);
        }
        
        if (player1.isAlive()) {
            player1.render(gl, textureManager);
        }
        if (player2 != null && player2.isAlive()) {
            player2.render(gl, textureManager);
        }
        
        renderHUD(gl);
    }
    
    private void renderHUD(GL gl) {
        gl.glPushMatrix();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0, screenWidth, screenHeight, 0, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        int heartSize = 30;
        int heartSpacing = 35;
        int startX = screenWidth - 150;
        int topY = 30;
        
        for (int i = 0; i < player1.getLives(); i++) {
            textureManager.drawTexture(gl, textureManager.getTexture("heart"), 
                startX + i * heartSpacing, topY, heartSize, heartSize);
        }
        
        if (player2 != null) {
            int p2TopY = topY + 40;
            for (int i = 0; i < player2.getLives(); i++) {
                textureManager.drawTexture(gl, textureManager.getTexture("heart"), 
                    startX + i * heartSpacing, p2TopY, heartSize, heartSize);
            }
        }
        
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();
        
        hudRenderer.beginRendering(screenWidth, screenHeight);
        
        hudRenderer.setColor(1, 1, 1, 1);
        hudRenderer.draw("Score: " + score, 20, screenHeight - 40);
        
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        hudRenderer.draw(String.format("Time: %02d:%02d", minutes, seconds), 20, screenHeight - 70);
        
        int enemiesRemaining = totalEnemies - enemiesKilled;
        
        if (enemiesRemaining <= 3) {
            hudRenderer.setColor(1, 0.5f, 0, 1); 
        } else {
            hudRenderer.setColor(0.2f, 0.8f, 0.2f, 1);
        }
        
        String enemiesText = String.format("Enemies: %d/%d", enemiesRemaining, totalEnemies);
        int textWidth = hudRenderer.getBounds(enemiesText).getBounds().width;
        hudRenderer.draw(enemiesText, (screenWidth - textWidth) / 2, screenHeight - 40);
        
        hudRenderer.setColor(0.6f, 0.8f, 1.0f, 1); 
        hudRenderer.draw("Level: " + levelNumber, 20, 40);
        
        if (totalEnemies > 0) {
            int percent = (int)((float)enemiesKilled / totalEnemies * 100);
            hudRenderer.setColor(0.8f, 0.8f, 0.8f, 1);
            hudRenderer.draw("Progress: " + percent + "%", screenWidth / 2 - 60, 40);
        }
        
        hudRenderer.endRendering();
    }
    
    public void handleMouseClick(int button) {
        Player mousePlayer = null;

        if (singlePlayer) {
            mousePlayer = player1;
        } else {
            mousePlayer = player2;
        }
        
        if (mousePlayer != null && mousePlayer.isAlive()) {
             if (button == MouseEvent.BUTTON1 || button == MouseEvent.BUTTON3) {
                Projectile proj = mousePlayer.throwSword();
                if (proj != null) {
                    projectiles.add(proj);
                    soundManager.playSound("sword_hit");
                }
            }
        }
    }

    public void handleKeyPress(int keyCode) {
        if (!singlePlayer && player1.isAlive()) {
            if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_CONTROL) {
                Projectile proj = player1.throwSword();
                if (proj != null) {
                    projectiles.add(proj);
                    soundManager.playSound("sword_hit");
                }
            }
        }
    }
    
    public boolean isLevelComplete() {
        return enemies.isEmpty() && enemiesKilled >= totalEnemies;
    }
    
    
    public boolean isGameOver() {
        boolean p1Dead = !player1.isAlive();
        if (singlePlayer) {
            return p1Dead;
        } else {
            return p1Dead && (player2 == null || !player2.isAlive());
        }
    }
    
    public int getScore() {
        return score;
    }
}