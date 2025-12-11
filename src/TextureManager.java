import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import com.sun.opengl.util.texture.TextureData;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    private Map<String, Texture> textures;
    private Map<String, Texture[]> animations;
    private GL glContext;
    
    public TextureManager() {
        textures = new HashMap<>();
        animations = new HashMap<>();
    }
    
    public void setGL(GL gl) {
        this.glContext = gl;
    }
    
    public void loadAllTextures() {
        if (glContext == null) {
            System.err.println("GL not set! Call setGL() first.");
            return;
        }
        
        loadTexture("ground", "Assets/texture/ground.png");
        loadTexture("sword", "Assets/texture/sword.png");
        loadTexture("heart", "Assets/texture/heart.png");
        loadTexture("Farm", "Assets/texture/Farm.png");
        loadTexture("menu", "Assets/texture/menu.png");
        loadTexture("victory", "Assets/texture/victory.png");
        loadTexture("gameover", "Assets/texture/gameover.png");
        
        for (int i = 1; i <= 10; i++) {
            loadTexture("natural" + i, "Assets/texture/natural/" + i + ".png");
        }
        
        loadAnimation("female_walk", "Assets/texture/Female/Walk/WomanWalk", 48);
        loadAnimation("female_idle", "Assets/texture/Female/Idle/WomanIdle", 48);
        
        loadAnimation("male_walk", "Assets/texture/Male/Walk/ManWalk", 48);
        loadAnimation("male_idle", "Assets/texture/Male/Idle/ManIdle", 48);
        
        loadEnemyAnimation("red_dragon", "Assets/texture/Enemies/Red/dragon");
        
        loadEnemyAnimation("green_dragon", "Assets/texture/Enemies/Green/dragon");
        
        loadEnemyAnimation("blue_dragon", "Assets/texture/Enemies/Blue/dragon");
    }
    
    private void loadTexture(String name, String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("Texture file not found: " + path);
                return;
            }
            
            TextureData data = TextureIO.newTextureData(file, false, null);
            Texture texture = TextureIO.newTexture(data);
            
            texture.bind();
            glContext.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            glContext.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            glContext.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
            glContext.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
            
            glContext.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
            
            textures.put(name, texture);
            
        } catch (Exception e) {
            System.err.println("Failed to load texture: " + path);
            e.printStackTrace();
        }
    }
    
    private void loadAnimation(String name, String basePath, int frameCount) {
        Texture[] frames = new Texture[frameCount];
        for (int i = 0; i < frameCount; i++) {
            String path = basePath + String.format("%03d", i + 1) + ".png";
            try {
                File file = new File(path);
                if (!file.exists()) {
                    System.err.println("Animation frame not found: " + path);
                    continue;
                }
                
                TextureData data = TextureIO.newTextureData(file, false, null);
                Texture texture = TextureIO.newTexture(data);
                
                texture.bind();
                glContext.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                glContext.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                glContext.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
                glContext.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
                glContext.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
                
                frames[i] = texture;
                
            } catch (Exception e) {
                System.err.println("Failed to load animation frame: " + path);
                e.printStackTrace();
            }
        }
        animations.put(name, frames);
    }
    
    private void loadEnemyAnimation(String name, String basePath) {
        Texture[] frames = new Texture[8];
        String[] directions = {"Top1", "Top2", "Down1", "Down2", "Right1", "Right2", "Left1", "Left2"};
        
        for (int i = 0; i < 8; i++) {
            String path = basePath + directions[i] + ".png";
            try {
                File file = new File(path);
                if (!file.exists()) {
                    System.err.println("Enemy texture not found: " + path);
                    continue;
                }
                
                TextureData data = TextureIO.newTextureData(file, false, null);
                Texture texture = TextureIO.newTexture(data);
                
                texture.bind();
                glContext.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                glContext.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                glContext.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
                glContext.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
                glContext.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
                
                frames[i] = texture;
                
            } catch (Exception e) {
                System.err.println("Failed to load enemy frame: " + path);
                e.printStackTrace();
            }
        }
        animations.put(name, frames);
    }
    
    public Texture getTexture(String name) {
        return textures.get(name);
    }
    
    public Texture[] getAnimation(String name) {
        return animations.get(name);
    }
    
    public void drawTexture(GL gl, Texture texture, float x, float y, float width, float height) {
        if (texture == null) return;
        
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        texture.bind();
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0, 0); gl.glVertex2f(x, y);
        gl.glTexCoord2f(1, 0); gl.glVertex2f(x + width, y);
        gl.glTexCoord2f(1, 1); gl.glVertex2f(x + width, y + height);
        gl.glTexCoord2f(0, 1); gl.glVertex2f(x, y + height);
        gl.glEnd();
        
        gl.glColor3f(1.0f, 1.0f, 1.0f);
    }
    
    public void drawTextureFlipped(GL gl, Texture texture, float x, float y, float width, float height, boolean flipX, boolean flipY) {
        if (texture == null) return;
        
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        float u1 = flipX ? 1 : 0;
        float u2 = flipX ? 0 : 1;
        float v1 = flipY ? 1 : 0;
        float v2 = flipY ? 0 : 1;
        
        texture.bind();
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(u1, v1); gl.glVertex2f(x, y);
        gl.glTexCoord2f(u2, v1); gl.glVertex2f(x + width, y);
        gl.glTexCoord2f(u2, v2); gl.glVertex2f(x + width, y + height);
        gl.glTexCoord2f(u1, v2); gl.glVertex2f(x, y + height);
        gl.glEnd();
        
        gl.glColor3f(1.0f, 1.0f, 1.0f);
    }
}