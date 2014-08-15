package com.roxiga.hypermotion3d;

public class Vector2D
{
	public float _x;
	public float _y;
	
	public Vector2D()
	{
		_x = _y = 0;
	}
	
	public Vector2D(float x,float y)
	{
		_x = x;
		_y = y;
	}
	
	@Override
	public String toString()
	{
		return new String("x: "+_x+" , y: "+_y);
	}
	
	public float [] toFloatArray()
	{
		float f[] = {_x, _y};
		return f;
	}
	
	public Vector2D add(Vector2D v){
		return new Vector2D(_x + v._x, _y + v._y);
	}
	
	public Vector3D toVector3D()
	{
		return new Vector3D(_x, _y, 0);
	}
	
	public Vector4D toVector4D()
	{
		return new Vector4D(_x, _y, 0, 0);
	}
}
