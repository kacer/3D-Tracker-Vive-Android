package cz.marw.threed_tracker_vive.rendering.glutils;

import android.content.Context;
import android.opengl.GLES31;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ShaderLoader {

    private static final String[] SHADER_EXTENSIONS = {".vert", ".frag"};
    private static final int[] GL_SHADER_TYPE_CONSTANTS = {GLES31.GL_VERTEX_SHADER, GLES31.GL_FRAGMENT_SHADER};

    private static final String SHADER_FOLDER = "shaders";

    public static int loadProgram(String fileName, Context context) {
        if(fileName == null) {
            System.err.println("Skipping shader program whose name is NULL");

            return -1;
        }

        // create shader program
        int program = GLES31.glCreateProgram();
        if(program < 0) {
            System.err.println("Unable create new shader program");

            return -1;
        }
        System.out.println("New shader program " + program + " created");

        int[] shaders = new int[SHADER_EXTENSIONS.length];
        for(int i = 0; i < SHADER_EXTENSIONS.length; i++) {
            String shaderFile = fileName + SHADER_EXTENSIONS[i];

            // read shader source code from text file
            System.out.println("Reading shader source code from file " + shaderFile + " ..... ");
            String shaderSrc = loadShader(shaderFile, context);
            if(shaderSrc == null) {
                // delete shaders
                GLES31.glDeleteProgram(program);
                removeShaders(shaders, program);

                return -1;
            }

            // create shader program
            shaders[i] = createShaderProgram(shaderSrc, GL_SHADER_TYPE_CONSTANTS[i]);
            if(shaders[i] > 0) {
                System.out.println("Shader: " + shaderFile + " - " + shaders[i] + " OK");
            } else {
                System.err.println("Shader is not supported");
                continue;
            }

            // compile shader program
            System.out.println("Compiling shader " + shaders[i] + " ..... ");
            shaders[i] = compileShaderProgram(shaders[i]);
            if(shaders[i] > 0) {
                System.out.println("OK");
            } else {
                // remove all created shaders and program
                GLES31.glDeleteProgram(program);
                removeShaders(shaders, program);

                return -1;
            }

            // attach shader program
            System.out.print("Attaching shader " + shaders[i] + " to program " + program + " ..... ");
            GLES31.glAttachShader(program, shaders[i]);
            System.out.println("OK");
        }

        // link shader programs
        System.out.println("Linking shader program " + program + " ..... ");
        if(linkProgram(program)) {
            System.out.println("OK");
        } else {
            // delete program
            GLES31.glDeleteProgram(program);
        }

        // detach and delete shader programs
        removeShaders(shaders, program);

        return program;
    }

    private static String loadShader(String shaderFile, Context context) {
        InputStream in;
        try {
            in = context.getAssets().open(SHADER_FOLDER + "/" + shaderFile);
        } catch(IOException e) {
            System.err.println("Could not find shader file: " + shaderFile);

            return null;
        }

        StringBuilder shader = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;

            while((line = br.readLine()) != null) {
                shader.append(line);
                shader.append("\n");
            }
        } catch(IOException e) {
            System.err.println("Could not open shader file: " + shaderFile);
            e.printStackTrace();

            return null;
        }

        return shader.toString();
    }

    private static int createShaderProgram(String shaderProgram, int shaderType) {
        int shader = GLES31.glCreateShader(shaderType);
        if(shader <= 0) {
            return shader;
        }

        GLES31.glShaderSource(shader, shaderProgram);

        return shader;
    }

    private static int compileShaderProgram(int shader) {
        GLES31.glCompileShader(shader);

        String error = GLES31.glGetShaderInfoLog(shader);
        if(error == null || error.equals("")) {
            return shader;
        } else {
            System.err.println("failed");
            System.err.println("\n" + error);

            return -1;
        }
    }

    private static boolean linkProgram(int program) {
        GLES31.glLinkProgram(program);

        String error = GLES31.glGetProgramInfoLog(program);
        if(error == null || error.equals("")) {
            return true;
        } else {
            System.err.println("failed");
            System.err.println("\n" + error);

            return false;
        }
    }

    private static void removeShaders(int[] shaders, int program) {
        for(int shader : shaders) {
            if(shader > 0) {
                GLES31.glDetachShader(program, shader);
                GLES31.glDeleteShader(shader);
            }
        }
    }

}
