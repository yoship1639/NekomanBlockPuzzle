package com.roxiga.hypermotion3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * 三角形ポリゴンのクラス
 */
public class Model3D extends Sprite2D
{
	//頂点情報バッファ
   	protected FloatBuffer _vertexBuffer;
    // インデックス情報バッファ
    protected ShortBuffer _indexBuffer;
	//テクスチャ座標バッファ
	protected FloatBuffer _textureBuffer;
	//法線情報バッファ
   	protected FloatBuffer _normalBuffer;
 
    protected ArrayList<Vector3D> _vertices = new ArrayList<Vector3D>();
    protected ArrayList<Short>    _indices  = new ArrayList<Short>();
    protected ArrayList<Float>    _uv       = new ArrayList<Float>();
    protected ArrayList<Vector3D> _normals  = new ArrayList<Vector3D>();
	//ボーンインデックス
	protected ArrayList<Integer> _boneIndices = new ArrayList<Integer>();
	protected ArrayList<ArrayList<ArrayList<Matrix3D>>> _animations = new ArrayList<ArrayList<ArrayList<Matrix3D>>>();
       
	//現在
	protected int _current;
	//アニメーション時間
	protected int _time = 0;
	//現在のアニメーション名
	protected int _currentAnimation = 0;
	//アニメーションが停止してるか？
	public boolean _stopAnimation = false;
	//アニメーションをループ再生するか？
	public boolean _loopAnimation = true;
	//アニメーションを最後で停止するか？
	public boolean _animationLast = false;
    //
	public float _distance = 0;
	public float _intersectU = 0;
	public float _intersectV = 0;
	
	public Vector3D _pos = new Vector3D(0,0,0);
	public Vector3D _rotate = new Vector3D(0,0,0);
	public Vector3D _scale = new Vector3D(1,1,1);
  
    public Model3D()
    {
    }
    
    protected void addV(double x,double y,double z)
    {
    	_vertices.add(new Vector3D((float)x,(float)y,(float)z));
    }
    
    protected void addN(double x,double y,double z)
    {
    	_normals.add(new Vector3D((float)x,(float)y,(float)z));
    }
    
    protected void addI(int i,int i2,int i3)
    {
    	_indices.add((short)i);
    	_indices.add((short)i2);
    	_indices.add((short)i3);
    }
        
    protected void addUV(double u,double v)
    {
    	_uv.add((float)u);
    	_uv.add((float)v);
    }
    
    protected void init()
    {
    	short i;
 
    	ByteBuffer vbb = ByteBuffer.allocateDirect(_indices.size()*3*4);
        vbb.order(ByteOrder.nativeOrder());
        _vertexBuffer = vbb.asFloatBuffer();
        for ( i = 0; i < _indices.size(); i++)
        {
        	Vector3D v = _vertices.get(_indices.get(i));
 			_vertexBuffer.put(v._x);
			_vertexBuffer.put(v._y);
			_vertexBuffer.put(v._z);
		}
        _vertexBuffer.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(_indices.size()*2);
        ibb.order(ByteOrder.nativeOrder());
        _indexBuffer = ibb.asShortBuffer();
        for ( i = 0; i < _indices.size(); i++)
        {
			_indexBuffer.put(i);
		}
		_indexBuffer.position(0);

		ByteBuffer uvbb = ByteBuffer.allocateDirect(_uv.size() * 4);
		uvbb.order(ByteOrder.nativeOrder());
		_textureBuffer = uvbb.asFloatBuffer();
        for ( i = 0; i < _uv.size(); i++)
        {
			_textureBuffer.put(_uv.get(i));
		}
		_textureBuffer.position(0);
    }
   
    /**
     * @Override
     * 描画
     */
    public void draw(GL10 gl)
    {
    	if ( !_visible )
    	{
    		return;
    	}
    	gl.glPushMatrix();
    	setTransform(gl);
    	gl.glColor4f(1, 1, 1, 1);
    	gl.glActiveTexture( GL10.GL_TEXTURE0 );
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, _textureNo);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, _indices.size(), GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        gl.glPopMatrix();
    }
    
    protected void setTransform(GL10 gl)
    {
        gl.glTranslatef(_pos._x,_pos._y,_pos._z);
        gl.glRotatef(_rotate._x, 1, 0, 0);
        gl.glRotatef(_rotate._y, 0, 1, 0);
        gl.glRotatef(_rotate._z, 0, 0, 1);
        gl.glScalef(_scale._x, _scale._y, _scale._z);
    }
    
    public Vector3D getPos()
    {
    	return new Vector3D(_pos._x,_pos._y,_pos._z);
    }
	
	public void setAnimation(int i)
	{
		if ( i >= 0 && i < _animations.size() )
		{
			_currentAnimation = i;
			_time = 0;
		}
	}
	
	public void playAnimation(int i,boolean loop,boolean last)
	{
		if ( i >= 0 && i < _animations.size() )
		{
			_currentAnimation = i;
			_time = 0;
		}
        _loopAnimation = loop;
        _animationLast = last;
	}
	
	public void nextAnimation()
	{
		_currentAnimation++;
		if ( _currentAnimation >= _animations.size() )
		{
			_currentAnimation = 0;
		}
		_time = 0;
	}
	
	public boolean intersect(Vector3D origin, Vector3D dir)
	{
        for ( int i = 0; i < _indices.size(); i+=3)
        {
        	Vector3D v0 = _vertices.get(_indices.get(i));
        	Vector3D v1 = _vertices.get(_indices.get(i+1));
        	Vector3D v2 = _vertices.get(_indices.get(i+2));
        	if ( intersectTriangle(origin,dir,v0,v1,v2) )
        	{
        		return true;
        	}
		}
        
		return false;
	}

	private boolean intersectTriangle(
			Vector3D origin, Vector3D dir,
		    Vector3D v0, Vector3D v1, Vector3D v2)
	{
		    Vector3D e1, e2, pv, tv, qv;
		    float det;
		    float t, u, v;
		    float inv_det;

		    e1 = v1.subtract(v0);
		    e2 = v2.subtract(v0);

		    pv = dir.crossProduct(e2);
		    det = e1.dotProduct(pv);

		    if (det > (1e-3))
		    {
		    	tv = origin.subtract(v0);
		        u = tv.dotProduct(pv);
		        if (u < 0.0f || u > det) return false;

		        qv = tv.crossProduct(e1);

		        v = dir.dotProduct(qv);
		        if (v < 0.0 || u + v > det) return false;
		    }
		    else if (det < -(1e-3))
		    {
		        tv = origin.subtract(v0);

		        u = tv.dotProduct(pv);
		        if (u > 0.0 || u < det) return false;

		        qv = tv.crossProduct(e1);

		        v = dir.dotProduct(qv);
		        if (v > 0.0 || u + v < det) return false;

		    }
		    else
		    {
		        return false;
		    }

		    inv_det = 1.0f / det;

		    t = e2.dotProduct(qv);
		    t *= inv_det;
		    u *= inv_det;
		    v *= inv_det;

		    _distance = t;
		    _intersectU = u;
		    _intersectV = v;

		    return true;    //hit!!
	}

	static public Vector2D getScreenPos(GL10 gl,Vector4D v,int[] viewport)
	{
		int i;
		// 現在のモデルビューマトリックスを取得する
		int[] bits = new int[16];
		Matrix3D model = new Matrix3D();
		Matrix3D proj = new Matrix3D();

		((GL11)gl).glGetIntegerv(GL11.GL_MODELVIEW_MATRIX_FLOAT_AS_INT_BITS_OES, bits, 0);
		for(i = 0; i < bits.length; i++)
		{
			model._e[i] = Float.intBitsToFloat(bits[i]);
		}
		((GL11)gl).glGetIntegerv(GL11.GL_PROJECTION_MATRIX_FLOAT_AS_INT_BITS_OES, bits, 0);
		for( i = 0; i < bits.length; i++)
		{
			proj._e[i] = Float.intBitsToFloat(bits[i]);
		}
		Vector4D v2 = v.Transform(model);
		Vector4D v3 = v2.Transform(proj);
		if (v3._z == 0.0) return null;
	    v3._x /= v3._w;
	    v3._y /= v3._w;
	    v3._z /= v3._w;
	    /* Map x, y and z to range 0-1 */
	    v3._x = v3._x * 0.5f + 0.5f;
	    v3._y = v3._y * 0.5f + 0.5f;
	    v3._z = v3._z * 0.5f + 0.5f;

	    /* Map x,y to viewport */
	    v3._x = v3._x * viewport[2] + viewport[0];
	    v3._y = v3._y * viewport[3] + viewport[1];

	    // x、y座標を返す    
        return new Vector2D(v3._x,viewport[3]-v3._y);
    }
	
    static public float GetDirRadian(Vector3D eye, Vector3D dest)
    {
        float radian;
        Vector3D dir;

        dir = Vector3D.Subtract(dest, eye);

        if (dir._x > 0.0f)
        {
            radian = (float)(-Math.atan(dir._z / dir._x) + Math.PI / 2);
        }
        else if (dir._x < 0.0f)
        {
            radian = (float)(-Math.atan(dir._z / dir._x) - Math.PI / 2);
        }
        else
        {
            return 0;
        }

        return radian;
    }

    // 視点から見た目的地の方向を度で返す
	static public float GetDirDegree(Vector3D eye,Vector3D dest)
	{
		float degree;

		degree = (float)Math.toDegrees(GetDirRadian(eye,dest));

		if ( degree < 0 )
		{
			degree += 360;
		}
		if ( degree >= 360 )
		{
			degree -= 360;
		}

		return degree;
	}
}
