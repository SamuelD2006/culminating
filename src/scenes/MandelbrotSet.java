package scenes;

import game.KeyListener;
import game.Scene;
import game.Shader;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class MandelbrotSet extends Scene {
    private final Shader shader;
    private final Vector2f center = new Vector2f(0.0f, 0.0f);
    private float scale = 1.0f;
    private int vao;
    private int vbo;

    public MandelbrotSet() {
        this.shader = new Shader("C:\\Users\\smlvl\\IdeaProjects\\Culminating\\culminating\\assets\\shaders\\default.glsl");
    }

    @Override
    public void init() {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        float[] vertexData = new float[1920 * 1080 * 2];
        int index = 0;
        for (int x = 0; x < 1920; x++) {
            for (int y = 0; y < 1080; y++) {
                vertexData[index++] = (float) x / 1920 * 2 - 1;
                vertexData[index++] = (float) y / 1080 * 2 - 1;
            }
        }

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertexData.length);
        buffer.put(vertexData).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);
    }

    @Override
    public void Update(float dt) {
        shader.use();

        glBindVertexArray(vao);

        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            center.y -= 0.05f;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            center.x += 0.05f;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            center.y += 0.05f;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            center.x -= 0.05f;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_UP)) {
            scale += 0.05f;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
            scale -= 0.05f;
        }

        shader.setVec2("Center", center);
        shader.setFloat("Scale", scale);
        shader.setInt("Iterations", 32);

        glEnableVertexAttribArray(0);
        glDrawArrays(GL_POINTS, 0, 1920 * 1080);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        glUseProgram(0);
    }
}