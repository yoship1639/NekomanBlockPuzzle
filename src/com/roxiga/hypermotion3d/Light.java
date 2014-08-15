package com.roxiga.hypermotion3d;

import javax.microedition.khronos.opengles.GL10;

public class Light {
	
	public Vector4D _pos = new Vector4D(1, 1, 1, 0);
	public Vector4D _diffuse = new Vector4D(1, 1, 1, 1);
	public Vector4D _specular = new Vector4D(1, 1, 1, 1);
	public Vector4D _ambient = new Vector4D(1, 1, 1, 1);
	
	public Light()
	{
	}
	
	public Light(Vector4D pos)
	{
		_pos = pos;
	}
	
	public void setColor(GL10 gl)
	{
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, _diffuse.toFloatArray(), 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, _specular.toFloatArray(), 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, _ambient.toFloatArray(), 0);
	}
	
	public void setPosition(GL10 gl)
	{
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, _pos.toFloatArray(), 0);
	}
}
