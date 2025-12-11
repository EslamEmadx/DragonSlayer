import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;
import java.util.List;
import java.util.Random;

public class Enemy {
    public enum AIBehavior {
        AGGRESSIVE, DEFENSIVE, STRATEGIC, RANDOM
    }
    
    private float x, y;
    private float width = 60, height = 60;
    private float baseSpeed = 0.40f;
    private float speed;
    
    private String type;
    private AIBehavior behavior;
    
    private boolean alive = true;
    
    private Texture[] frames;
    private int currentFrame = 0;
    private int frameCounter = 0;
    private int frameDelay = 60;
    
    private int direction = 0;
    
    private Random random;
    private int randomMoveTimer = 0;
    private float randomDx = 0, randomDy = 0;
    
    // متغيرات للحركة الذكية
    private int stuckCounter = 0;
    private float lastX = 0, lastY = 0;
    private float targetDx = 0, targetDy = 0;
    private int pathRecalcTimer = 0;
    
    // متغيرات متطورة لتجنب العوائق
    private float avoidAngle = 0;
    private int avoidTimer = 0;
    private float lastValidX = 0, lastValidY = 0;
    private boolean isAvoiding = false;
    private int turnDirection = 0;
    private int turnCounter = 0;
    
    // متغيرات لتتبع الهدف
    private Player currentTarget;
    private float targetMemoryX, targetMemoryY;
    private float targetLastX, targetLastY;
    private int targetUpdateTimer = 0;
    
    public Enemy(float x, float y, String type, AIBehavior behavior, TextureManager tm) {
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = y;
        this.lastValidX = x;
        this.lastValidY = y;
        this.type = type;
        this.behavior = behavior;
        this.frames = tm.getAnimation(type);
        this.random = new Random();
        
        switch (behavior) {
            case AGGRESSIVE:
                speed = baseSpeed * 1.5f;
                break;
            case DEFENSIVE:
                speed = baseSpeed * 0.7f;
                break;
            case STRATEGIC:
                speed = baseSpeed * 1.2f;
                break;
            case RANDOM:
                speed = baseSpeed * 0.9f;
                break;
            default:
                speed = baseSpeed;
                break;
        }
    }
    
    public void update(List<Player> players, List<Obstacle> obstacles, int screenW, int screenH) {
        if (!alive || players.isEmpty()) return;
        
        targetUpdateTimer++;
        if (targetUpdateTimer >= 30 || currentTarget == null || !currentTarget.isAlive()) {
            currentTarget = selectTarget(players);
            targetUpdateTimer = 0;
            if (currentTarget != null) {
                targetMemoryX = currentTarget.getX();
                targetMemoryY = currentTarget.getY();
                targetLastX = currentTarget.getX();
                targetLastY = currentTarget.getY();
            }
        }
        
        if (currentTarget == null) return;
        
        if (currentTarget.isAlive()) {
            // تخزين الموضع السابق للهدف
            targetLastX = targetMemoryX;
            targetLastY = targetMemoryY;
            
            // تحديث الموضع الحالي
            targetMemoryX = currentTarget.getX();
            targetMemoryY = currentTarget.getY();
        }
        
        if (isValidPosition(x, y, obstacles)) {
            lastValidX = x;
            lastValidY = y;
        }
        
        if (isAvoiding) {
            handleAvoidance(obstacles, screenW, screenH);
            updateAnimation();
            return;
        }
        
        calculateTargetDirection(currentTarget, obstacles);
        
        boolean moved = tryMoveTowardsTarget(obstacles, screenW, screenH);
        
        if (!moved) {
            startAvoidance(obstacles);
            stuckCounter++;
            
            if (stuckCounter > 15) {
                x = lastValidX;
                y = lastValidY;
                stuckCounter = 0;
                isAvoiding = false;
            }
        } else {
            stuckCounter = 0;
        }
        
        updateDirection();
        updateAnimation();
    }
    
    private void handleAvoidance(List<Obstacle> obstacles, int screenW, int screenH) {
        avoidTimer--;
        
        if (avoidTimer <= 0) {
            isAvoiding = false;
            turnDirection = 0;
            turnCounter = 0;
            return;
        }
        
        turnCounter++;
        float moveAngle = avoidAngle;
        
        if (turnCounter > 10 && turnCounter < 30) {
            moveAngle += turnDirection * 0.1f;
        }
        
        float dx = (float) Math.cos(moveAngle) * speed * 1.3f;
        float dy = (float) Math.sin(moveAngle) * speed * 1.3f;
        
        float newX = x + dx;
        float newY = y + dy;
        
        newX = Math.max(10, Math.min(newX, screenW - width - 10));
        newY = Math.max(10, Math.min(newY, screenH - height - 10));
        
        if (!checkCollisionWithObstacles(newX, newY, obstacles)) {
            x = newX;
            y = newY;
        } else {
            avoidAngle += turnDirection * (float) Math.PI / 4;
        }
    }
    
    private boolean tryMoveTowardsTarget(List<Obstacle> obstacles, int screenW, int screenH) {
        float dx = targetDx;
        float dy = targetDy;
        
        if (behavior == AIBehavior.RANDOM) {
            dx += (random.nextFloat() - 0.5f) * 0.3f;
            dy += (random.nextFloat() - 0.5f) * 0.3f;
        }
        
        float newX = x + dx;
        float newY = y + dy;
        
        float margin = 5f;
        newX = Math.max(margin, Math.min(newX, screenW - width - margin));
        newY = Math.max(margin, Math.min(newY, screenH - height - margin));
        
        if (checkCollisionWithObstacles(newX, newY, obstacles)) {
            return false;
        }
        
        x = newX;
        y = newY;
        return true;
    }
    
    private void startAvoidance(List<Obstacle> obstacles) {
        isAvoiding = true;
        avoidTimer = 40 + random.nextInt(20);
        
        if (turnDirection == 0) {
            turnDirection = random.nextBoolean() ? 1 : -1;
        }
        
        float currentAngle = (float) Math.atan2(targetDy, targetDx);
        avoidAngle = currentAngle + (turnDirection * (float) Math.PI / 2);
        
        avoidAngle += (random.nextFloat() - 0.5f) * 0.7f;
    }
    
    private void updateDirection() {
        float dx = x - lastX;
        float dy = y - lastY;
        
        if (Math.abs(dx) > 0.01f || Math.abs(dy) > 0.01f) {
            float angle = (float) Math.atan2(dy, dx);
            
            int dir = (int) Math.round((angle + Math.PI) / (Math.PI / 4)) % 8;
            if (dir < 0) dir += 8;
            direction = dir;
        }
        
        lastX = x;
        lastY = y;
    }
    
    private void updateAnimation() {
        frameCounter++;
        if (frameCounter >= frameDelay) {
            frameCounter = 0;
            currentFrame = (currentFrame + 1) % 2;
        }
    }
    
    private boolean isValidPosition(float testX, float testY, List<Obstacle> obstacles) {
        return !checkCollisionWithObstacles(testX, testY, obstacles);
    }
    
    private void calculateTargetDirection(Player target, List<Obstacle> obstacles) {
        float targetCenterX = targetMemoryX + (target.getWidth() / 2);
        float targetCenterY = targetMemoryY + (target.getHeight() / 2);
        float enemyCenterX = x + (width / 2);
        float enemyCenterY = y + (height / 2);
        
        float dx = targetCenterX - enemyCenterX;
        float dy = targetCenterY - enemyCenterY;
        
        switch (behavior) {
            case AGGRESSIVE:
                // مباشرة نحو الهدف
                break;
                
            case DEFENSIVE:
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float desiredDistance = 200f;
                
                if (distance < desiredDistance) {
                    dx = -dx;
                    dy = -dy;
                }
                break;
                
            case STRATEGIC:
                // حساب اتجاه حركة الهدف (بدون استخدام getVelocityX/Y)
                float targetMoveX = targetMemoryX - targetLastX;
                float targetMoveY = targetMemoryY - targetLastY;
                
                // توقع موقع الهدف بعد فترة قصيرة
                float predictFactor = 0.5f; // عامل التنبؤ
                dx += targetMoveX * predictFactor * 50;
                dy += targetMoveY * predictFactor * 50;
                
                // إضافة عنصر عشوائي صغير للحركة الاستراتيجية
                dx += (random.nextFloat() - 0.5f) * 30;
                dy += (random.nextFloat() - 0.5f) * 30;
                break;
                
            case RANDOM:
                randomMoveTimer++;
                if (randomMoveTimer > 30) {
                    randomDx = (random.nextFloat() - 0.5f) * 100;
                    randomDy = (random.nextFloat() - 0.5f) * 100;
                    randomMoveTimer = 0;
                }
                dx += randomDx;
                dy += randomDy;
                break;
        }
        
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length > 0.1f) {
            targetDx = (dx / length) * speed;
            targetDy = (dy / length) * speed;
        } else {
            targetDx = 0;
            targetDy = 0;
        }
    }
    
    private boolean checkCollisionWithObstacles(float testX, float testY, List<Obstacle> obstacles) {
        for (Obstacle obs : obstacles) {
            if (checkCollision(testX, testY, obs)) {
                return true;
            }
        }
        return false;
    }
    
    private Player selectTarget(List<Player> players) {
        if (players.isEmpty()) return null;
        
        Player selected = null;
        float bestScore = -Float.MAX_VALUE;
        
        for (Player p : players) {
            if (!p.isAlive()) continue;
            
            float score = 0;
            float distance = distanceTo(p);
            
            switch (behavior) {
                case AGGRESSIVE:
                    score = -distance;
                    break;
                    
                case DEFENSIVE:
                    score = distance;
                    break;
                    
                case STRATEGIC:
                    score = -distance - (p.getLives() * 50);
                    break;
                    
                case RANDOM:
                    score = -distance + (random.nextFloat() * 200 - 100);
                    break;
            }
            
            if (score > bestScore) {
                bestScore = score;
                selected = p;
            }
        }
        
        return selected;
    }
    
    private float distanceTo(Player player) {
        float dx = (player.getX() + player.getWidth()/2) - (x + width/2);
        float dy = (player.getY() + player.getHeight()/2) - (y + height/2);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    public void render(GL gl, TextureManager tm) {
        if (!alive || frames == null || frames.length == 0) return;
        
        int animDirection = 0;
        if (direction >= 6 || direction <= 2) animDirection = 0; // يمين
        else if (direction == 3 || direction == 5) animDirection = 1; // فوق
        else if (direction == 4) animDirection = 2; // يسار
        else animDirection = 3; // تحت
        
        int frameIndex = animDirection * 2 + currentFrame;
        
        if (frameIndex >= frames.length) {
            frameIndex = frameIndex % frames.length;
        }
        
        tm.drawTexture(gl, frames[frameIndex], x, y, width, height);
    }
    
    public boolean collidesWith(Player player) {
        if (!alive || !player.isAlive()) return false;
        
        float px = player.getX();
        float py = player.getY();
        float pw = player.getWidth();
        float ph = player.getHeight();
        
        // تصادم من حدود الصورة للاعب والعدو
        float collisionWidth = Math.min(width, pw) * 0.8f;
        float collisionHeight = Math.min(height, ph) * 0.8f;
        
        float enemyCenterX = x + width/2;
        float enemyCenterY = y + height/2;
        float playerCenterX = px + pw/2;
        float playerCenterY = py + ph/2;
        
        // تحقق من التصادم بين المراكز
        return (Math.abs(enemyCenterX - playerCenterX) < collisionWidth/2 &&
                Math.abs(enemyCenterY - playerCenterY) < collisionHeight/2);
    }
    
    private boolean checkCollision(float testX, float testY, Obstacle obs) {
        float ox = obs.getCollisionX();
        float oy = obs.getCollisionY();
        float ow = obs.getCollisionWidth();
        float oh = obs.getCollisionHeight();
        
        // استخدام مستطيل داخلي للتصادم (70% من الحجم)
        float innerWidth = width * 0.7f;
        float innerHeight = height * 0.7f;
        float innerX = testX + (width - innerWidth) / 2;
        float innerY = testY + (height - innerHeight) / 2;
        
        float obsInnerWidth = ow * 0.7f;
        float obsInnerHeight = oh * 0.7f;
        float obsInnerX = ox + (ow - obsInnerWidth) / 2;
        float obsInnerY = oy + (oh - obsInnerHeight) / 2;
        
        return (innerX < obsInnerX + obsInnerWidth &&
                innerX + innerWidth > obsInnerX &&
                innerY < obsInnerY + obsInnerHeight &&
                innerY + innerHeight > obsInnerY);
    }
    
    public boolean isAlive() { return alive; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public AIBehavior getBehavior() { return behavior; }
    public boolean isAvoiding() { return isAvoiding; }
    
    public void die() {
        alive = false;
    }
    
    public boolean hitByBullet(float bx, float by, float bw, float bh) {
        return (bx < x + width &&
                bx + bw > x &&
                by < y + height &&
                by + bh > y);
    }
    
    // لمنع الريسبون داخل عائق
    public boolean canSpawnHere(float spawnX, float spawnY, List<Obstacle> obstacles) {
        return !checkCollisionWithObstacles(spawnX, spawnY, obstacles);
    }
    
    // البحث عن موقع آمن
    public void findSafePosition(List<Obstacle> obstacles, int screenW, int screenH) {
        if (canSpawnHere(x, y, obstacles)) {
            return; // الموقع الحالي آمن
        }
        
        // البحث في دوائر حول المركز
        for (int radius = 20; radius < 200; radius += 20) {
            for (int angle = 0; angle < 360; angle += 45) {
                float newX = x + (float)(Math.cos(Math.toRadians(angle)) * radius);
                float newY = y + (float)(Math.sin(Math.toRadians(angle)) * radius);
                
                // تأكد من أن الموقع داخل الشاشة
                if (newX >= 0 && newX <= screenW - width &&
                    newY >= 0 && newY <= screenH - height &&
                    canSpawnHere(newX, newY, obstacles)) {
                    this.x = newX;
                    this.y = newY;
                    return;
                }
            }
        }
        
        // إذا لم يجد موقعاً آمناً، ضعه في مكان عشوائي
        this.x = 50 + random.nextInt(screenW - 100);
        this.y = 50 + random.nextInt(screenH - 100);
    }
}