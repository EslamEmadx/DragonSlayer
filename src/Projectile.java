import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;

public class Projectile {
    private float x, y;
    private float width = 32, height = 32;
    private float dx, dy;
    private float speed = 0.5f; 
    
    private float rotationAngle = 0;
    private float rotationSpeed = 10.0f; 
    
    private boolean active = true;
    private int lifetime = 0;
    private int maxLifetime = 500; 
    
    public Projectile(float x, float y, float dirX, float dirY) {
        this.x = x;
        this.y = y;
        this.dx = dirX * speed;
        this.dy = dirY * speed;
    }
    
    public void update() {
        if (!active) return;
        
        x += dx;
        y += dy;
        
        rotationAngle += rotationSpeed;
        if (rotationAngle >= 360) {
            rotationAngle -= 360;
        }
        
        lifetime++;
        if (lifetime >= maxLifetime) {
            active = false;
        }
    }
    
    public void render(GL gl, TextureManager tm) {
        if (!active) return;
        
        Texture swordTexture = tm.getTexture("sword");
        if (swordTexture != null) {
            gl.glPushMatrix();
            
            gl.glTranslatef(x, y, 0);
            
            gl.glRotatef(rotationAngle, 0, 0, 1);
            
            tm.drawTexture(gl, swordTexture, -width / 2, -height / 2, width, height);
            
            gl.glPopMatrix();
        }
    }
    
    public boolean collidesWith(Enemy enemy) {
        float ex = enemy.getX();
        float ey = enemy.getY();
        float ew = enemy.getWidth();
        float eh = enemy.getHeight();
        
        return x > ex && x < ex + ew && y > ey && y < ey + eh;
    }
    
    public boolean collidesWith(Obstacle obstacle) {
        float ox = obstacle.getX();
        float oy = obstacle.getY();
        float ow = obstacle.getWidth();
        float oh = obstacle.getHeight();
        
        return x > ox && x < ox + ow && y > oy && y < oy + oh;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
}