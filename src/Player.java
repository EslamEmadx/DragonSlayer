import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;
import java.util.List;

public class Player {
    private float x, y;
    private float width = 64, height = 64;
    private float speed = 0.69f;
    
    private String type;
    private boolean isKeyboardControlled;
    
    private int lives = 3;
    private boolean alive = true;
    
    private Texture[] walkFrames;
    private Texture[] idleFrames;
    private int currentFrame = 0;
    private int frameCounter = 0;
    private int frameDelay = 8;
    
    private int direction = 0;
    private boolean isMoving = false;
    
    // متغيرات جديدة للهجوم
    private boolean canAttackWhileMoving = true;
    private int attackCooldown = 0; // تأخير بين الهجمات
    private static final int ATTACK_COOLDOWN_TIME = 15; 
    
    // متغيرات جديدة للتصادم
    private float collisionWidth = width * 0.8f;  
    private float collisionHeight = height * 0.8f; 
    
    public Player(float x, float y, String type, boolean keyboard, TextureManager tm) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.isKeyboardControlled = keyboard;
        
        walkFrames = tm.getAnimation(type + "_walk");
        idleFrames = tm.getAnimation(type + "_idle");
    }
    
    public void move(float dx, float dy, List<Obstacle> obstacles, int screenW, int screenH) {
        if (dx == 0 && dy == 0) {
            isMoving = false;
            return;
        }
        
        isMoving = true;
        
       
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx = (dx / length) * speed;
            dy = (dy / length) * speed;
        }
        
        
        double angle = Math.atan2(dy, dx);
        direction = (int) Math.round((angle + Math.PI) / (Math.PI / 4)) % 8;
        if (direction < 0) direction += 8;
        
        
        float newX = x + dx;
        float newY = y + dy;
        
       
        if (newX < 0) newX = 0;
        if (newX > screenW - width) newX = screenW - width;
        if (newY < 0) newY = 0;
        if (newY > screenH - height) newY = screenH - height;
        
        
        boolean collides = false;
        for (Obstacle obs : obstacles) {
            if (checkCollisionWithObstacle(newX, newY, obs)) {
                collides = true;
                break;
            }
        }
        
        if (!collides) {
            x = newX;
            y = newY;
        }
    }
    
    public void moveTowards(int targetX, int targetY, List<Obstacle> obstacles, int screenW, int screenH) {
        float dx = targetX - (x + width / 2);
        float dy = targetY - (y + height / 2);
        
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance < 10) {
            isMoving = false;
            return;
        }
        
        dx /= distance;
        dy /= distance;
        
        move(dx, dy, obstacles, screenW, screenH);
    }
    
    public void update() {
        if (!alive) return;
        
        
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        
        frameCounter++;
        if (frameCounter >= frameDelay) {
            frameCounter = 0;
            currentFrame++;
            
            int maxFrame = isMoving ? 48 : 48;
            if (currentFrame >= maxFrame) {
                currentFrame = 0;
            }
        }
    }
    
    public void render(GL gl, TextureManager tm) {
        if (!alive) return;
        
        Texture[] frames = isMoving ? walkFrames : idleFrames;
        if (frames == null) return;
        
        
        int directionGroup = direction; 
        int frameStart = 0;
        
        switch (directionGroup) {
            case 0: frameStart = 0; break;  
            case 1: frameStart = 8; break;   
            case 2: frameStart = 16; break;  
            case 3: frameStart = 24; break;  
            case 4: frameStart = 32; break;  
            case 5: frameStart = 40; break;  
            case 6: frameStart = 16; break; 
            case 7: frameStart = 8; break;   
        }
        
        int animFrame = (currentFrame % 8) + frameStart;
        if (animFrame >= frames.length) animFrame = 0;
        
        boolean flipX = (directionGroup == 6 || directionGroup == 7);
        
        tm.drawTextureFlipped(gl, frames[animFrame], x, y, width, height, flipX, false);
    }
    
    public void takeDamage() {
        lives--;
        if (lives <= 0) {
            alive = false;
        }
    }
    
    public Projectile throwSword() {
        if (!alive || attackCooldown > 0) return null;
        
        float angle = (direction * 45) * (float) Math.PI / 180f;
        float dx = (float) Math.cos(angle);
        float dy = (float) Math.sin(angle);
        
        attackCooldown = ATTACK_COOLDOWN_TIME;
        
        return new Projectile(x + width / 2, y + height / 2, dx, dy);
    }
    
    public boolean collidesWith(Enemy enemy) {
        if (!alive || !enemy.isAlive()) return false;
        
        float playerCenterX = x + width/2;
        float playerCenterY = y + height/2;
        float enemyCenterX = enemy.getX() + enemy.getWidth()/2;
        float enemyCenterY = enemy.getY() + enemy.getHeight()/2;
        
        float dx = playerCenterX - enemyCenterX;
        float dy = playerCenterY - enemyCenterY;
        
        return (Math.abs(dx) < (collisionWidth/2 + enemy.getWidth()/2 * 0.7f) &&
                Math.abs(dy) < (collisionHeight/2 + enemy.getHeight()/2 * 0.7f));
    }
    
    private boolean checkCollisionWithObstacle(float testX, float testY, Obstacle obs) {
        float ox = obs.getCollisionX();
        float oy = obs.getCollisionY();
        float ow = obs.getCollisionWidth();
        float oh = obs.getCollisionHeight();
        
        float innerPlayerWidth = collisionWidth;
        float innerPlayerHeight = collisionHeight;
        float innerPlayerX = testX + (width - innerPlayerWidth) / 2;
        float innerPlayerY = testY + (height - innerPlayerHeight) / 2;
        
        float innerObstacleWidth = ow * 0.7f;
        float innerObstacleHeight = oh * 0.7f;
        float innerObstacleX = ox + (ow - innerObstacleWidth) / 2;
        float innerObstacleY = oy + (oh - innerObstacleHeight) / 2;
        
        return (innerPlayerX < innerObstacleX + innerObstacleWidth &&
                innerPlayerX + innerPlayerWidth > innerObstacleX &&
                innerPlayerY < innerObstacleY + innerObstacleHeight &&
                innerPlayerY + innerPlayerHeight > innerObstacleY);
    }
    
    public boolean canRespawnAt(float respawnX, float respawnY, List<Obstacle> obstacles) {
        for (Obstacle obs : obstacles) {
            if (checkCollisionWithObstacle(respawnX, respawnY, obs)) {
                return false;
            }
        }
        return true;
    }
    
    public void findSafeRespawnPosition(List<Obstacle> obstacles, int screenW, int screenH) {
        if (canRespawnAt(x, y, obstacles)) {
            return;
        }
        
        int radiusStep = 20;
        int maxRadius = 200;
        
        for (int radius = radiusStep; radius <= maxRadius; radius += radiusStep) {
            for (int angle = 0; angle < 360; angle += 45) {
                float newX = x + (float)(Math.cos(Math.toRadians(angle)) * radius);
                float newY = y + (float)(Math.sin(Math.toRadians(angle)) * radius);
                
                if (newX >= 0 && newX <= screenW - width &&
                    newY >= 0 && newY <= screenH - height &&
                    canRespawnAt(newX, newY, obstacles)) {
                    this.x = newX;
                    this.y = newY;
                    return;
                }
            }
        }
        
        java.util.Random rand = new java.util.Random();
        int attempts = 0;
        int maxAttempts = 100;
        
        while (attempts < maxAttempts) {
            float randomX = 50 + rand.nextFloat() * (screenW - width - 100);
            float randomY = 50 + rand.nextFloat() * (screenH - height - 100);
            
            if (canRespawnAt(randomX, randomY, obstacles)) {
                this.x = randomX;
                this.y = randomY;
                return;
            }
            attempts++;
        }
        
        this.x = 50;
        this.y = 50;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public int getLives() {
        return lives;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public int getDirection() {
        return direction;
    }
    
    public boolean isMoving() {
        return isMoving;
    }
    
    public boolean canAttack() {
        return attackCooldown == 0;
    }
    
    public void respawn(float newX, float newY, List<Obstacle> obstacles) {
        if (!canRespawnAt(newX, newY, obstacles)) {
            float tempX = x;
            float tempY = y;
            this.x = newX;
            this.y = newY;
            findSafeRespawnPosition(obstacles, 1024, 768);
            return;
        }
        
        this.x = newX;
        this.y = newY;
        this.alive = true;
        this.lives = 3;
        this.attackCooldown = 0;
    }
}