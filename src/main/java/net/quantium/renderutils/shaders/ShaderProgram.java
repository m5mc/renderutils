package net.quantium.renderutils.shaders;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Yaaay, custom shaders!
 * 
 * https://www.khronos.org/opengl/wiki/Shader_Compilation
 */

@SideOnly(Side.CLIENT)
public class ShaderProgram {	
	private static final HashSet<ShaderProgram> programs = new HashSet<ShaderProgram>();
	
	private int programId = -1;

	private String vertexShaderPath;
	private String fragmentShaderPath;
	
	public ShaderProgram(String vertexShader, String fragmentShader){
		this.vertexShaderPath = vertexShader;
		this.fragmentShaderPath = fragmentShader;
		
		programs.add(this);
		
		if(ShaderUtils.isSupported())
			try{
				reload();
			}catch(Exception e){
				System.err.println("Shader " + this + " has failed loading.");
				throw new RuntimeException(e);
			}	
	}
	
	public int getProgramId(){
		return programId;
	}
	
	public boolean isValid(){
		return programId > 0;
	}
	
	/**
	 * Binds current program and returns handler for reverting to previous program
	 * @return Handle for reverting
	 */
	public Handle use(){
		if(!isValid()) return new EmptyHandle();
		
		Handle handle = new Handle(programId);
		GL20.glUseProgram(programId);
		return handle;
	}
	
	public void reload() throws CompilationException, IOException{
		if(isValid()) GL20.glDeleteProgram(programId);
		int vertex   = ShaderUtils.createShader(ShaderUtils.readFile(this.vertexShaderPath),   GL20.GL_VERTEX_SHADER);   //creates vertex shader
		int fragment = ShaderUtils.createShader(ShaderUtils.readFile(this.fragmentShaderPath), GL20.GL_FRAGMENT_SHADER); //creates pixel shader
		this.programId = ShaderUtils.createProgram(vertex, fragment); //create program

		Handle h = use();
		init(h);
		h.revert();
	}
	
	protected void init(Handle handle) { }

	public static class Handle {
		private int previousId;
		private int currentId;
		private boolean reverted = false;
		
		private Handle(int currentId) {
			this.currentId = currentId;
			this.previousId = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		}
		
		public void revert() {
			if(!check()) return;

			//if(GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) != currentId) throw new IllegalStateException("Wrong revertion order!");
			GL20.glUseProgram(previousId);
			this.reverted = true;
		}

		public void setInteger(String name, int i) {
			if(!check()) return;

			int uLoc = GL20.glGetUniformLocation(currentId, name);
			GL20.glUniform1i(uLoc, i);
		}

		public void setFloat(String name, float f) {
			if(!check()) return;

			int uLoc = GL20.glGetUniformLocation(currentId, name);
			GL20.glUniform1f(uLoc, f);
		}

		public void setVec2(String name, float f0, float f1) {
			if(!check()) return;

			int uLoc = GL20.glGetUniformLocation(currentId, name);
			GL20.glUniform2f(uLoc, f0, f1);
		}

		protected boolean check() {
			if(this.reverted) throw new IllegalStateException("Already reverted!");
			return true;
		}
	}
	
	private static class EmptyHandle extends Handle {
		private EmptyHandle() {
			super(-1);
		}

		@Override
		protected boolean check() {
			return false;
		}
	}
	
	public static Collection<ShaderProgram> getShaders(){
		return Collections.unmodifiableCollection(programs);
	}
	
	@Override
	public String toString(){
		return vertexShaderPath + "#" + fragmentShaderPath + "@" + programId;
	}
}
