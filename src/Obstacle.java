import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;

public class Obstacle {
    private float x, y;
    private float width, height;
    private int type; // 1-10 for different natural objects
    
    // نسبة الـ collision من حجم الصورة (0.6 = 60% من الصورة)
    private float collisionRatio = 0.65f;
    
    public Obstacle(float x, float y, int type, TextureManager tm) {
        this.x = x;
        this.y = y;
        this.type = type;
        
        // Vary size based on type
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
    
    // الموقع الفعلي للـ collision (مع المسافة من الحواف)
    public float getCollisionX() {
        float padding = width * (1 - collisionRatio) / 2;
        return x + padding;
    }
    
    public float getCollisionY() {
        float padding = height * (1 - collisionRatio) / 2;
        return y + padding;
    }
    
    // الحجم الفعلي للـ collision (أصغر من الصورة)
    public float getCollisionWidth() {
        return width * collisionRatio;
    }
    
    public float getCollisionHeight() {
        return height * collisionRatio;
    }
    
    // الدوال الأصلية للرسم
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