package com.roxiga.hypermotion3d;

public class Matrix3D
{
	float _e[] = 
	{
			1,0,0,0,
			0,1,0,0,
			0,0,1,0,
			0,0,0,1
	};
	
	public Matrix3D(){}
	
	public Matrix3D(
			double m0 ,double m1 ,double m2 ,double m3 ,
			double m4 ,double m5 ,double m6 ,double m7 ,
			double m8 ,double m9 ,double m10,double m11,
			double m12,double m13,double m14,double m15)
	{
		_e[0]  = (float)m0;
		_e[1]  = (float)m1;
		_e[2]  = (float)m2;
		_e[3]  = (float)m3;
		_e[4]  = (float)m4;
		_e[5]  = (float)m5;
		_e[6]  = (float)m6;
		_e[7]  = (float)m7;
		_e[8]  = (float)m8;
		_e[9]  = (float)m9;
		_e[10] = (float)m10;
		_e[11] = (float)m11;
		_e[12] = (float)m12;
		_e[13] = (float)m13;
		_e[14] = (float)m14;
		_e[15] = (float)m15;
	}

	public Vector3D affine(Vector3D v)
	{
		float x = v._x*_e[0]+v._y*_e[4]+v._z*_e[8]+_e[12];
		float y = v._x*_e[1]+v._y*_e[5]+v._z*_e[9]+_e[13];
		float z = v._x*_e[2]+v._y*_e[6]+v._z*_e[10]+_e[14];
		
		return new Vector3D(x,y,z);
	}

	public void multiply(Matrix3D a,Matrix3D b)
	{
		_e[0] = a._e[0]*b._e[0] + a._e[1]*b._e[4] + a._e[2]*b._e[8] + a._e[3]*b._e[12];
		_e[1] = a._e[0]*b._e[1] + a._e[1]*b._e[5] + a._e[2]*b._e[9] + a._e[3]*b._e[13];
		_e[2] = a._e[0]*b._e[2] + a._e[1]*b._e[6] + a._e[2]*b._e[10] + a._e[3]*b._e[14];
		_e[3] = a._e[0]*b._e[3] + a._e[1]*b._e[7] + a._e[2]*b._e[11] + a._e[3]*b._e[15];
		_e[4] = a._e[4]*b._e[0] + a._e[5]*b._e[4] + a._e[6]*b._e[8] + a._e[7]*b._e[12];
		_e[5] = a._e[4]*b._e[1] + a._e[5]*b._e[5] + a._e[6]*b._e[9] + a._e[7]*b._e[13];
		_e[6] = a._e[4]*b._e[2] + a._e[5]*b._e[6] + a._e[6]*b._e[10] + a._e[7]*b._e[14];
		_e[7] = a._e[4]*b._e[3] + a._e[5]*b._e[7] + a._e[6]*b._e[11] + a._e[7]*b._e[15];
		_e[8] = a._e[8]*b._e[0] + a._e[9]*b._e[4] + a._e[10]*b._e[8] + a._e[11]*b._e[12];
		_e[9] = a._e[8]*b._e[1] + a._e[9]*b._e[5] + a._e[10]*b._e[9] + a._e[11]*b._e[13];
		_e[10] = a._e[8]*b._e[2] + a._e[9]*b._e[6] + a._e[10]*b._e[10] + a._e[11]*b._e[14];
		_e[11] = a._e[8]*b._e[3] + a._e[9]*b._e[7] + a._e[10]*b._e[11] + a._e[11]*b._e[15];
		_e[12] = a._e[12]*b._e[0] + a._e[13]*b._e[4] + a._e[14]*b._e[8] + a._e[15]*b._e[12];
		_e[13] = a._e[12]*b._e[1] + a._e[13]*b._e[5] + a._e[14]*b._e[9] + a._e[15]*b._e[13];
		_e[14] = a._e[12]*b._e[2] + a._e[13]*b._e[6] + a._e[14]*b._e[10] + a._e[15]*b._e[14];
		_e[15] = a._e[12]*b._e[3] + a._e[13]*b._e[7] + a._e[14]*b._e[11] + a._e[15]*b._e[15];
	}
}
