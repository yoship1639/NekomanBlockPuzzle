package com.roxiga.hypermotion3d;

import android.annotation.SuppressLint;

public class Vector3D
{
	public float _x;
	public float _y;
	public float _z;
	
	public Vector3D()
	{
		_x = _y = _z = 0.0f;
	}
	
	public Vector3D(float x,float y,float z)
	{
		_x = x;
		_y = y;
		_z = z;
	}
	
	@SuppressLint("FloatMath")
	public float length()
    {
		return (float)(Math.sqrt(_x*_x + _y*_y + _z*_z));
    }
	
	public Vector3D add(Vector3D v)
	{
		return new Vector3D(_x + v._x, _y + v._y, _z + v._z);
	}

	public Vector3D subtract(Vector3D v)
	{
		return new Vector3D(_x - v._x, _y - v._y, _z - v._z);
	}
	
	static public Vector3D Subtract(Vector3D dest, Vector3D eye)
	{
		float x = dest._x - eye._x;
		float y = dest._y - eye._y;
		float z = dest._z - eye._z;
				
		return new Vector3D(x,y,z);
	}
	
	public void normalize()
	{
		float l = this.length();
		if ( l == 0 )
		{
			_x = 0;
			_y = 0;
			_z = 1;
		}
		else
		{
			_x /= l;
			_y /= l;
			_z /= l;
		}
	}

	public float dotProduct(Vector3D v)
    {
 		return (v._x * _x + v._y * _y + v._z * _z);
    }

	public Vector3D crossProduct(Vector3D v)
	{
		float x = (_y*v._z) - (v._y*_z);
		float y = (v._x*_z) - (_x*v._z);
		float z = (_x*v._y) - (v._x*_y);
		return (new Vector3D(x, y, z));
	}
	
	public static Vector3D CalcNormal(Vector3D v1, Vector3D v2, Vector3D v3)
	{
		Vector3D n1, n2, cross;

		/* v1 = p1 - p2を求める */
		n1 = Vector3D.Subtract(v1, v2);

		/* v2 = p3 - p2を求める */
		n2 = Vector3D.Subtract(v3, v2);

		/* 外積v2×v1（= cross）を求める */
		cross = n2.crossProduct(n1);

		/* 外積v2×v1の長さ|v2×v1|（= length）を求める */
		/* 長さ|v2×v1|が0のときは法線ベクトルは求められない */
		cross.normalize();

		/* 外積v2×v1を長さ|v2×v1|で割って法線ベクトルnを求める */
		return cross;
	}
	
	@Override
	public String toString()
	{
		return new String("x: "+_x+" , y: "+_y+" , z: "+_z);
	}
	
	public float [] toFloatArray()
	{
		float f[] = {_x, _y, _z};
		return f;
	}
	
	public Vector2D toVector2D()
	{
		return new Vector2D(_x, _y);
	}
	
	public Vector4D toVector4D()
	{
		return new Vector4D(_x, _y, _z, 0);
	}
}
