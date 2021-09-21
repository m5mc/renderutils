package net.quantium.renderutils.shaders;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;

public class ShaderUtils {
	public static String readFile(String file) throws IOException{
		InputStream stream = ShaderProgram.class.getResourceAsStream(file);
		if(stream == null) throw new FileNotFoundException(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		StringBuilder builder = new StringBuilder();
		
		String line = null;
		while((line = reader.readLine()) != null) //read file line-by-line
			builder.append(line).append("\n");
		
		return builder.toString();
	}
	
	public static int createShader(String source, int type) throws CompilationException{
		int shaderId = GL20.glCreateShader(type); //create empty shader
		GL20.glShaderSource(shaderId, source); //attach source
		GL20.glCompileShader(shaderId); //and compile
		int state = GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS);
		if(state == GL11.GL_FALSE) //check status
		{
			int len = GL20.glGetShaderi(shaderId, GL20.GL_INFO_LOG_LENGTH);
			String log = GL20.glGetShaderInfoLog(shaderId, len);
			
			GL20.glDeleteShader(shaderId);
			throw new CompilationException("Shader " + shaderId + " failed to compile: " + log);
		}
		
		return shaderId;
	}
	
	public static int createProgram(int vertex, int fragment) throws CompilationException{
		int program = GL20.glCreateProgram();
		
		GL20.glAttachShader(program, vertex);
		GL20.glAttachShader(program, fragment);
		
		GL20.glLinkProgram(program);

		int state = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);
		if (state == GL11.GL_FALSE)
		{
			int len = GL20.glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH);
			String log = GL20.glGetProgramInfoLog(program, len);
			
			GL20.glDeleteProgram(program);
			GL20.glDeleteShader(vertex);
			GL20.glDeleteShader(fragment);

			throw new CompilationException("Program " + program + " failed to compile: " + log);
		}

		//GL20.glDetachShader(program, vertex);
		//GL20.glDetachShader(program, fragment);
		
		return program;
	}

	public static boolean isSupported(){
		return OpenGlHelper.shadersSupported;
	}
}
