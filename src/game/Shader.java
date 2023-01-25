package game;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import util.Utils;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL40.glUniform1d;

public class Shader {
    private final String shaderPath;
    private final int shaderProgram;

    public Shader(String shaderPath) {
        this.shaderPath = shaderPath;
        String shaderSource;
        try {
            shaderSource = Utils.readFile(shaderPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!shaderSource.contains("#type vertex")) System.err.printf("Missing vertex shader: %s%n", shaderPath);
        else if (!shaderSource.contains("#type fragment")) System.err.printf("Missing fragment shader: %s%n", shaderPath);

        String[] lines = shaderSource.split("\n");
        StringBuilder vertexSource = new StringBuilder();
        StringBuilder fragmentSource = new StringBuilder();
        boolean isVertexShader = false;

        for (String line : lines) {
            if (!line.startsWith("#type")) {
                if (isVertexShader) {
                    vertexSource.append(line).append("\n");
                } else {
                    fragmentSource.append(line).append("\n");
                }
            } else {
                isVertexShader = line.startsWith("#type vertex");
            }
        }


        int vertexID, fragmentID;

        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSource.toString());
        glCompileShader(vertexID);
        checkCompileErrors(vertexID, "VERTEX");

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource.toString());
        glCompileShader(fragmentID);
        checkCompileErrors(fragmentID, "FRAGMENT");

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);
        checkCompileErrors(shaderProgram, "PROGRAM");

        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
    }

    public void use() {
        glUseProgram(shaderProgram);
    }

    public void setBool(String name, boolean value) {
        glUniform1i(glGetUniformLocation(shaderProgram, name), value ? 1 : 0);
    }

    public void setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(shaderProgram, name), value);
    }

    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(shaderProgram, name), value);
    }
    public void setDouble(String name, double value) {
        glUniform1d(glGetUniformLocation(shaderProgram, name), value);
    }

    public void setVec4(String name, Vector4f vec) {
        glUniform4f(glGetUniformLocation(shaderProgram, name), vec.x, vec.y, vec.z, vec.w);
    }

    public void setVec3(String name, Vector3f vec) {
        glUniform3f(glGetUniformLocation(shaderProgram, name), vec.x, vec.y, vec.z);
    }

    public void setVec2(String name, Vector2f vec) {
        glUniform2f(glGetUniformLocation(shaderProgram, name), vec.x, vec.y);
    }


    private void checkCompileErrors(int shader, String type) {
        int success;
        int len;
        if (type.equals("PROGRAM")) {
            success = glGetProgrami(shader, GL_LINK_STATUS);
            if (success == GL_FALSE) {
                len = glGetProgrami(shader, GL_INFO_LOG_LENGTH);
                System.out.printf("Error: '%s'\n\tLinking of shaders failed.%n", this.shaderPath);
                System.out.println(glGetProgrami(shader, len));
            }
        } else {
            success = glGetShaderi(shader, GL_COMPILE_STATUS);
            if (success == GL_FALSE) {
                len = glGetShaderi(shader, GL_INFO_LOG_LENGTH);
                System.out.printf("Error: '%s'\n\t%s shader compilation failed.%n", this.shaderPath, type.equals("FRAGMENT") ? "Fragment" : "Vertex");
                System.out.println(glGetShaderInfoLog(shader, len));
            }
        }
    }
}
