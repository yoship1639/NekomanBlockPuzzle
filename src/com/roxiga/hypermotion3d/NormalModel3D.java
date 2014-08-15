package com.roxiga.hypermotion3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.microedition.khronos.opengles.GL10;

/**
 * �O�p�`�|���S���̃N���X
 */
public class NormalModel3D extends Model3D
{
	//�����A���r�G���g
    float[] _lightAmbient=
    	new float[]{0.7f,0.7f,0.7f,1 };
    //�����ʒu
    float[] _lightPos    =
    	new float[]{200,400,100,1};
    //���̌���
    float[] _lightDir    =
    	new float[]{-2,-4,-1};
    //�}�e���A���A���r�G���g
    float[] _matAmbient  =
    	new float[]{0.7f,0.7f,0.7f,1 };

    public NormalModel3D()
    {
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

    	ByteBuffer nbb = ByteBuffer.allocateDirect(_normals.size()*3*4);
        nbb.order(ByteOrder.nativeOrder());
        _normalBuffer = nbb.asFloatBuffer();
        for ( i = 0; i < _normals.size(); i++)
        {
        	Vector3D n = _normals.get(i);
 			_normalBuffer.put(n._x);
			_normalBuffer.put(n._y);
			_normalBuffer.put(n._z);
		}
        _normalBuffer.position(0);
    }
   
    /**
     * @Override
     * �`��
     */
    public void draw(GL10 gl)
    {
    	if ( !_visible )
    	{
    		return;
    	}
    	gl.glPushMatrix();
    	setTransform(gl);
       //���C�e�B���O�̎w��
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,GL10.GL_AMBIENT,_matAmbient,0);
        gl.glLightfv(GL10.GL_LIGHT0,GL10.GL_AMBIENT,_lightAmbient,0);
        gl.glLightfv(GL10.GL_LIGHT0,GL10.GL_POSITION,_lightPos,0);
        gl.glLightfv(GL10.GL_LIGHT0,GL10.GL_SPOT_DIRECTION,_lightDir,0);
        //
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        //�@���z��̎w��
        gl.glNormalPointer(GL10.GL_FLOAT,0,_normalBuffer);
        //�e�N�X�`��
        gl.glActiveTexture( GL10.GL_TEXTURE0 );
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, _textureNo);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, _indices.size(), GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        //
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glPopMatrix();
    }
}

