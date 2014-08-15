package com.roxiga.hypermotion3d;

public class Vector4D
{
	public float _x;
	public float _y;
	public float _z;
	public float _w;
	
	public Vector4D()
	{
		_x = 0;
		_y = 0;
		_z = 0;
		_w = 1;
	}
	
	public Vector4D(float x,float y,float z,float w)
	{
		_x = x;
		_y = y;
		_z = z;
		_w = w;
	}

	// •ÏŠ·‚ÌŒvZ‚ğs‚¤
	public Vector4D Transform(Matrix3D m)
	{
        float v0 = _x * m._e[0] + _y * m._e[4] + _z * m._e[8]  + _w * m._e[12];
        float v1 = _x * m._e[1] + _y * m._e[5] + _z * m._e[9]  + _w * m._e[13];
        float v2 = _x * m._e[2] + _y * m._e[6] + _z * m._e[10] + _w * m._e[14];
        float v3 = _x * m._e[3] + _y * m._e[7] + _z * m._e[11] + _w * m._e[15];
        return new Vector4D(v0,v1,v2,v3);
	}
	
	@Override
	public String toString()
	{
		return new String("x: "+_x+" , y: "+_y+" , z: "+_z+" , w: "+_w);
	}
	
	public float [] toFloatArray()
	{
		float f[] = {_x, _y, _z, _w};
		return f;
	}
	
	public Vector3D toVector3D()
	{
		return new Vector3D(_x, _y, _z);
	}
	
	public Vector2D toVector2D()
	{
		return new Vector2D(_x, _y);
	}
}
