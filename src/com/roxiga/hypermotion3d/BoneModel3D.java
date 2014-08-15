package com.roxiga.hypermotion3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class BoneModel3D extends Model3D
{
	
	public BoneModel3D()
	{
		super();
	}
	
	protected void addB(double x,double y,double z,int i)
	{
    	_vertices.add(new Vector3D((float)x,(float)y,(float)z));
		_boneIndices.add(i);
	}

	protected void addA3()
	{
		_animations.add(new ArrayList<ArrayList<Matrix3D>>());
		_currentAnimation = _animations.size() - 1;
	}

	protected void addA2()
	{
		ArrayList<ArrayList<Matrix3D>> anim = _animations.get(_currentAnimation);
		anim.add(new ArrayList<Matrix3D>());
		_current = anim.size() - 1;
	}

	protected void addA(
			double m0,double m1,double m2,double m3,
			double m4,double m5,double m6,double m7,
			double m8,double m9,double m10,double m11,
			double m12,double m13,double m14,double m15)
	{
			ArrayList<ArrayList<Matrix3D>> anim = _animations.get(_currentAnimation);
			ArrayList<Matrix3D> anim2 = anim.get(_current);
			anim2.add(new Matrix3D(m0,m1,m2,m3,m4,m5,m6,m7,m8,m9,m10,m11,m12,m13,m14,m15));
	}
    
    protected void init()
    {
    	int i;
    	
        ByteBuffer vbb = ByteBuffer.allocateDirect(_indices.size()*3*4);
        vbb.order(ByteOrder.nativeOrder());
        _vertexBuffer = vbb.asFloatBuffer();

        ByteBuffer ibb = ByteBuffer.allocateDirect(_indices.size()*2);
        ibb.order(ByteOrder.nativeOrder());
        _indexBuffer = ibb.asShortBuffer();
        for ( short index = 0; index < _indices.size(); index++)
        {
			_indexBuffer.put(index);
		}
		_indexBuffer.position(0);
		
		ByteBuffer nbb = ByteBuffer.allocateDirect(_indices.size()*3*4);
        nbb.order(ByteOrder.nativeOrder());
        _normalBuffer = nbb.asFloatBuffer();
		
		Vector3D [] normal = new Vector3D[_vertices.size()];
		for ( i = 0; i < normal.length; i++)normal[i] = new Vector3D(0, 0, 1);
		for ( short index = 0; index < _indices.size() / 3; index++)
		{
			Vector3D n = Vector3D.CalcNormal(this._vertices.get(_indices.get(index*3)), this._vertices.get(_indices.get(index*3+1)), this._vertices.get(_indices.get(index*3+2)));
			normal[_indices.get(index*3  )] = normal[_indices.get(index*3  )].add(n);
			normal[_indices.get(index*3+1)] = normal[_indices.get(index*3+1)].add(n);
			normal[_indices.get(index*3+2)] = normal[_indices.get(index*3+2)].add(n);
		}
		for( i = 0; i < normal.length; i++)
		{
			normal[i].normalize();
			//System.out.println(_normals.size()+": "+normal[i]._x+", "+normal[i]._y+", "+normal[i]._z);
			_normals.add(normal[i]);
		}
		_normalBuffer.position(0);

		ByteBuffer uvbb = ByteBuffer.allocateDirect(_uv.size() * 4);
		uvbb.order(ByteOrder.nativeOrder());
		_textureBuffer = uvbb.asFloatBuffer();
        for ( i = 0; i < _uv.size(); i++)
        {
			_textureBuffer.put(_uv.get(i));
		}
		_textureBuffer.position(0);
		
		_currentAnimation = 0;
    }

	public void draw(GL10 gl)
    {
    	if ( !_visible )
    	{
    		return;
    	}
    	int i;

    	gl.glPushMatrix();
    	setTransform(gl);
    	gl.glColor4f(1,  1,  1,  1);
    	ArrayList<ArrayList<Matrix3D>> anim = _animations.get(_currentAnimation);
 
        _vertexBuffer.position(0);
        _normalBuffer.position(0);
        for ( i = 0; i < _indices.size(); i++)
        {
        	int index = _indices.get(i);
        	ArrayList<Matrix3D> a = anim.get(_boneIndices.get(index));
        	Matrix3D m = a.get(_time);
        	Vector3D v = m.affine(_vertices.get(index));
        	Vector3D n = _normals.get(index);
			_vertexBuffer.put(v._x);
			_vertexBuffer.put(v._y);
			_vertexBuffer.put(v._z);
			_normalBuffer.put(n._x);
			_normalBuffer.put(n._y);
			_normalBuffer.put(n._z);
		}
        _vertexBuffer.position(0);
        _normalBuffer.position(0);
        
        float[] _matAmbient  = new float[]{0.75f, 0.75f, 0.75f, 1};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, _matAmbient, 0);
        
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, _normalBuffer);
        //テクスチャ管理番号バインド
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _textureNo);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer);
       // 描画
        gl.glDrawElements(GL10.GL_TRIANGLES, _indices.size(), GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        
        
        _time++;
        ArrayList<Matrix3D> anim2 = anim.get(0);
        if ( _time >= anim2.size() )
        {
        	if ( _loopAnimation )
        	{
        		_time = 0;
        	}
        	else if ( _animationLast )
        	{
        		_time = anim2.size() - 1;
        	}
        }
        
        gl.glPopMatrix();
    }
	
	public void draw(GL10 gl, int skipNum)
    {
    	if ( !_visible )
    	{
    		return;
    	}
    	int i;
    	
    	gl.glPushMatrix();
    	setTransform(gl);
    	gl.glColor4f(1,  1,  1,  1);
    	ArrayList<ArrayList<Matrix3D>> anim = _animations.get(_currentAnimation);
 
    	_time += skipNum;
    	if ( _time >= anim.get(0).size() )
        {
        	if ( _loopAnimation )
        	{
        		_time -= anim.get(0).size();
        	}
        	else if ( _animationLast )
        	{
        		_time = anim.get(0).size() - 1;
        	}
        }
        _vertexBuffer.position(0);
        _normalBuffer.position(0);
        for ( i = 0; i < _indices.size(); i++)
        {
        	int index = _indices.get(i);
        	ArrayList<Matrix3D> a = anim.get(_boneIndices.get(index));
        	Matrix3D m = a.get(_time);
        	Vector3D v = m.affine(_vertices.get(index));
        	Vector3D n = _normals.get(index);
			_vertexBuffer.put(v._x);
			_vertexBuffer.put(v._y);
			_vertexBuffer.put(v._z);
			_normalBuffer.put(n._x);
			_normalBuffer.put(n._y);
			_normalBuffer.put(n._z);
		}
        _vertexBuffer.position(0);
        _normalBuffer.position(0);
        
        float[] _matAmbient  = new float[]{0.75f, 0.75f, 0.75f, 1};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, _matAmbient, 0);
        
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, _normalBuffer);
        //テクスチャ管理番号バインド
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _textureNo);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer);
       // 描画
        gl.glDrawElements(GL10.GL_TRIANGLES, _indices.size(), GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        
        
        _time++;
        ArrayList<Matrix3D> anim2 = anim.get(0);
        if ( _time >= anim2.size() )
        {
        	if ( _loopAnimation )
        	{
        		_time = 0;
        	}
        	else if ( _animationLast )
        	{
        		_time = anim2.size() - 1;
        	}
        }
        
        gl.glPopMatrix();
    }
}
