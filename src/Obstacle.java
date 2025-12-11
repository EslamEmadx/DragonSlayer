import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;

public class Obstacle {
    private float x, y;
    private float width, height;
    private int type;
    
    private float collisionRatio = 0.65f;
    
    public Obstacle(float x, float y, int type, TextureManager tm) {
        this.x = x;
        this.y = y;
        this.type = type;
        
        if (type <= 3) {
            width = 80;
            height = 80;
        } else if (type <= 6) {
            width = 60;
            height = 60;
        } else {
            width = 50;
            height = 50;
        }
    }
    
    public void render(GL gl, TextureManager tm) {
        Texture texture = tm.getTexture("natural" + type);
        if (texture != null) {
            tm.drawTexture(gl, texture, x, y, width, height);
        }
    }
    
    public float getCollisionX() {
        float padding = width * (1 - collisionRatio) / 2;
        return x + padding;
    }
    
    public float getCollisionY() {
        float padding = height * (1 - collisionRatio) / 2;
        return y + padding;
    }
    
    public float getCollisionWidth() {
        return width * collisionRatio;
    }
    
    public float getCollisionHeight() {
        return height * collisionRatio;
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
}