package com.roxiga.hypermotion3d;

public class Camera {
	
	public Vector3D _eye = new Vector3D(0.0f, 40.0f, -60.0f);
	public Vector3D _look = new Vector3D(0.0f, 10.0f, 0.0f);
	public Vector3D _up = new Vector3D(0.0f, 1.0f, 0.0f);
	
	private float rotAdd = 0.0f;
	private float rotXZ = 30f;
	private float dist = 60f;
	
	private static final double ONE_ANGLE = Math.PI / 180.0;
	
	public Camera()
	{
	}
	
	public Camera(Vector3D eye, Vector3D look, Vector3D up){
		_eye = eye;
		_look = look;
		_up = up;
	}
	
	public void rotCamera(Vector3D look, float rot, float height, float dist){
		_look = look;
		_eye._x = _look._x + (float)Math.sin(rot * ONE_ANGLE) * dist;
		_eye._y = height;
		_eye._z = _look._z + (float)Math.cos(rot * ONE_ANGLE) * dist;
	}
	
	public void rotCamera(Vector3D look, float rot, float dist){
		rotCamera(look, rot, _eye._y, dist);
	}
	
	public void rotCamera(Vector3D look, float rot){
		rotCamera(look, rot, _eye._y, dist);
	}
	
	public void rotAddCamera(Vector3D look, float add, float height, float dist){
		_look = look;
		rotAdd(add);
		_eye._x = _look._x + (float)Math.sin(rotAdd * ONE_ANGLE) * dist;
		_eye._y = height;
		_eye._z = _look._z + (float)Math.cos(rotAdd * ONE_ANGLE) * dist;
	}
	
	public void rotAddCamera(Vector3D look, float add, float dist){
		rotAddCamera(look, add, _eye._y, dist);
	}
	
	public void rotAddCamera(Vector3D look, float add){
		rotAddCamera(look, add, _eye._y, dist);
	}
	
	public void rotCameraDome(Vector3D look, float rotY, float rotXZ, float dist){
		_look = look;
		_eye._x = _look._x + (float)Math.sin(rotY * ONE_ANGLE) * (float)Math.cos(rotXZ * ONE_ANGLE) * dist;
		_eye._y = _look._y + (float)Math.sin(rotXZ * ONE_ANGLE) * dist;
		_eye._z = _look._z + (float)Math.cos(rotY * ONE_ANGLE) * (float)Math.cos(rotXZ * ONE_ANGLE) * dist; 
	}
	
	public void rotCameraDome(Vector3D look, float rotY, float dist){
		rotCameraDome(look, rotY, rotXZ, dist);
	}
	
	public void rotCameraDome(Vector3D look, float rotY){
		rotCameraDome(look, rotY, rotXZ, dist);
	}
	
	public void rotAddCameraDome(Vector3D look, float addY, float rotXZ, float dist){
		_look = look;
		rotAdd(addY);
		_eye._x = _look._x + (float)Math.sin(rotAdd * ONE_ANGLE) * (float)Math.cos(rotXZ * ONE_ANGLE) * dist;
		_eye._y = _look._y + (float)Math.sin(rotXZ * ONE_ANGLE) * dist;
		_eye._z = _look._z + (float)Math.cos(rotAdd * ONE_ANGLE) * (float)Math.cos(rotXZ * ONE_ANGLE) * dist; 
	}
	
	public void rotAddCameraDome(Vector3D look, float addY, float dist){
		rotAddCameraDome(look, addY, rotXZ, dist);
	}
	
	public void rotAddCameraDome(Vector3D look, float addY){
		rotAddCameraDome(look, addY, rotXZ, dist);
	}
	
	public void addRotXZ(float add, float max, float min){
		this.rotXZ += add;
		if(this.rotXZ > max)this.rotXZ = max;
		else if(this.rotXZ < min)this.rotXZ = min;
	}
	
	public void setRotXZ(float rotXZ){
		this.rotXZ = rotXZ;
	}
	
	public float getRotXZ(){
		return this.rotXZ;
	}
	
	public void addDist(float add, float max, float min){
		this.dist += add;
		if(this.dist > max)this.dist = max;
		else if(this.dist < min)this.dist = min;
	}

	public float getDist() {
		return dist;
	}

	public void setDist(float dist) {
		this.dist = dist;
	}
	
	private void rotAdd(float add){
		this.rotAdd += add;
		if(this.rotAdd > 360f)this.rotAdd -= 360f;
		else if(this.rotAdd < 0)this.rotAdd += 360f;
	}
}
