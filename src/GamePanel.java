import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import com.sun.opengl.util.Animator;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends GLCanvas implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
    private Animator animator;
    private GameEngine gameEngine;
    private int mouseX, mouseY;
    private GL glContext;
    
    public GamePanel() {
        setPreferredSize(new Dimension(1024, 768));
        addGLEventListener(this);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        requestFocus();
    }
    
    public void startGame() {
        animator = new Animator(this);
        animator.start();
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        glContext = drawable.getGL();
        GL gl = glContext;
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        gl.glEnable(GL.GL_TEXTURE_2D);
        
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
        
        gameEngine = new GameEngine(1100, 800, glContext);
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        
        if (gameEngine != null) {
            gameEngine.update();
            gameEngine.render(gl);
        }
        
        gl.glColor3f(1.0f, 1.0f, 1.0f);
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, width, height, 0, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }
    
    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameEngine != null) {
            gameEngine.keyPressed(e.getKeyCode());
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (gameEngine != null) {
            gameEngine.keyReleased(e.getKeyCode());
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (gameEngine != null) {
            gameEngine.mousePressed(e.getX(), e.getY(), e.getButton());
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
    
    @Override
    public void mouseMoved(MouseEvent e) {
        if (gameEngine != null) {
            gameEngine.mouseMoved(e.getX(), e.getY());
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (gameEngine != null) {
            gameEngine.mouseMoved(e.getX(), e.getY());
        }
    }
}