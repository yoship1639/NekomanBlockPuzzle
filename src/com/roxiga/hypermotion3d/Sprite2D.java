package com.roxiga.hypermotion3d;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class Sprite2D
{
    public boolean _visible = true;
	//テクスチャID番号
    public int _textureNo;
    //配置する位置
    public Vector3D _pos = new Vector3D(0,0,0);
    //幅
    public int _width;
    //高さ
    public int _height;
    
    private boolean _hit = false, _oldHit = false;
    
    public Vector2D _ratio = new Vector2D(1, 1);
    
    public Sprite2D()
    {
    }
    
    public Sprite2D(int px, int py, Vector2D ratio)
    {
    	_pos._x = px;
    	_pos._y = py;
    	_ratio = ratio;
    }
    
    public void releaseTexture(GL10 gl)
    {
    	int[] texNo = {_textureNo};
    	gl.glDeleteTextures(1, texNo, 0);
    }
    
    public void setTexture(GL10 gl, Bitmap bitmap)
    {
    	gl.glEnable(GL10.GL_ALPHA_TEST);
    	gl.glEnable(GL10.GL_BLEND);
    	gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    	gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
    	//テクスチャIDを割り当てる
    	int[] textureNo = new int[1];
    	gl.glGenTextures(1, textureNo, 0);
    	if(_textureNo != 0){
    		int no [] = {_textureNo};
    		gl.glDeleteTextures(1, no, 0);
    	}
    	_textureNo = textureNo[0];
    	//テクスチャIDのバインド
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, _textureNo);	
     	//OpenGL ES用のメモリ領域に画像データを渡す。上でバインドされたテクスチャIDと結び付けられる。
    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
    	//テクスチャ座標が1.0fを超えたときの、テクスチャをS軸方向に繰り返す設定
    	gl.glTexParameterx(GL10.GL_TEXTURE_2D,
    			GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT );
    	//テクスチャ座標が1.0fを超えたときの、テクスチャをT軸方向に繰り返す設定
    	gl.glTexParameterx(GL10.GL_TEXTURE_2D,
    			GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT );
    	//テクスチャを元のサイズから拡大、縮小して使用したときの色の使い方を設定
    	gl.glTexParameterx(GL10.GL_TEXTURE_2D,
    			GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR );
    	//テクスチャを元のサイズから拡大、縮小して使用したときの色の使い方を設定
    	gl.glTexParameterx(GL10.GL_TEXTURE_2D,
    			GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR );
    	
    	_width = bitmap.getWidth();
    	_height = bitmap.getHeight();
    }

    //テクスチャを読み込んでセット
    public void setTexture(GL10 gl, Resources res, int id)
    {
    	//　テクスチャをresから読み出す
    	InputStream is = res.openRawResource(id);

    	Bitmap bitmap;
    	try
    	{
    		bitmap = BitmapFactory.decodeStream(is);
    	}
    	finally
    	{
    		try
    		{
    			is.close();
    		}
    		catch(IOException e)
    		{
    		}
    	}
    	setTexture(gl, bitmap);
    }

    //2Dスプライト描画
    public void draw(GL10 gl)
    {
    	draw(gl, false, new Vector4D(1,1,1,1));
	}

    public void draw(GL10 gl, Vector4D color)
    {
    	if ( !_visible )
    	{
    		return;
    	}
    	
    	gl.glDisable(GL10.GL_DEPTH_TEST);
   		//色セット
   		gl.glColor4f(color._x, color._y, color._z, color._w);
   		
   		//テクスチャ0番をアクティブにする
   		gl.glActiveTexture(GL10.GL_TEXTURE0);
   		//テクスチャID(_textureNo)に対応するテクスチャをバインドする
   		gl.glBindTexture(GL10.GL_TEXTURE_2D, _textureNo);

		//テクスチャの座標と幅高さを指定
   		int rect[] = {0, _height, _width, -_height};
   
   		//バインドされているテクスチャ画像のどの部分を使うかを指定
   		((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D,
   			GL11Ext.GL_TEXTURE_CROP_RECT_OES, rect, 0);
   		//高速2D描画
   		((GL11Ext) gl).glDrawTexfOES(
   				_pos._x / SV.VirtualRatio._x, 
   				SV.ScreenSize._y - ((_height*_ratio._y) + _pos._y ) / SV.VirtualRatio._y, 
   				_pos._z, 
   				_width*_ratio._x / SV.VirtualRatio._x, 
   				_height*_ratio._y / SV.VirtualRatio._y);
   		//
   	   	gl.glEnable(GL10.GL_DEPTH_TEST);
	}
    
    public void draw(GL10 gl, boolean isHitDraw)
    {
    	draw(gl, isHitDraw, new Vector4D(0.6f, 0.6f, 0.6f, 1));
	}

    public void draw(GL10 gl, boolean isHitDraw, Vector4D color)
    {
    	if ( !_visible )
    	{
    		return;
    	}
    	
    	gl.glDisable(GL10.GL_DEPTH_TEST);
   		//色セット
   		if(isHitDraw && _hit)gl.glColor4f(color._x, color._y, color._z, color._w);
   		else gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
   		
   		//テクスチャ0番をアクティブにする
   		gl.glActiveTexture(GL10.GL_TEXTURE0);
   		//テクスチャID(_textureNo)に対応するテクスチャをバインドする
   		gl.glBindTexture(GL10.GL_TEXTURE_2D, _textureNo);

		//テクスチャの座標と幅高さを指定
   		int rect[] = {0, _height, _width, -_height};
   
   		//バインドされているテクスチャ画像のどの部分を使うかを指定
   		((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D,
   			GL11Ext.GL_TEXTURE_CROP_RECT_OES, rect, 0);
   		//高速2D描画
   		((GL11Ext) gl).glDrawTexfOES(
   				_pos._x / SV.VirtualRatio._x, 
   				SV.ScreenSize._y - ((_height*_ratio._y) + _pos._y ) / SV.VirtualRatio._y, 
   				_pos._z, 
   				_width*_ratio._x / SV.VirtualRatio._x, 
   				_height*_ratio._y / SV.VirtualRatio._y);
   		//
   	   	gl.glEnable(GL10.GL_DEPTH_TEST);
	}
    
    public void drawInPixelCorrect(GL10 gl)
    {
    	drawInPixelCorrect(gl, false, new Vector4D(1,1,1,1));
	}

    public void drawInPixelCorrect(GL10 gl, Vector4D color)
    {
    	if ( !_visible )
    	{
    		return;
    	}
    	
    	gl.glDisable(GL10.GL_DEPTH_TEST);
   		//色セット
   		gl.glColor4f(color._x, color._y, color._z, color._w);
   		
   		//テクスチャ0番をアクティブにする
   		gl.glActiveTexture(GL10.GL_TEXTURE0);
   		//テクスチャID(_textureNo)に対応するテクスチャをバインドする
   		gl.glBindTexture(GL10.GL_TEXTURE_2D, _textureNo);

		//テクスチャの座標と幅高さを指定
   		int rect[] = {0, _height, _width, -_height};
   
   		//バインドされているテクスチャ画像のどの部分を使うかを指定
   		((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D,
   			GL11Ext.GL_TEXTURE_CROP_RECT_OES, rect, 0);
   		//高速2D描画
   		((GL11Ext) gl).glDrawTexfOES(
   				_pos._x, 
   				SV.ScreenSize._y - ((_height*_ratio._y) + _pos._y ), 
   				_pos._z, 
   				_width*_ratio._x, 
   				_height*_ratio._y);
   		//
   	   	gl.glEnable(GL10.GL_DEPTH_TEST);
	}
    
    public void drawInPixelCorrect(GL10 gl, boolean isHitDraw)
    {
    	drawInPixelCorrect(gl, isHitDraw, new Vector4D(0.6f, 0.6f, 0.6f, 1));
	}

    public void drawInPixelCorrect(GL10 gl, boolean isHitDraw, Vector4D color)
    {
    	if ( !_visible )
    	{
    		return;
    	}
    	
    	gl.glDisable(GL10.GL_DEPTH_TEST);
   		//色セット
   		if(isHitDraw && _hit)gl.glColor4f(color._x, color._y, color._z, color._w);
   		else gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
   		
   		//テクスチャ0番をアクティブにする
   		gl.glActiveTexture(GL10.GL_TEXTURE0);
   		//テクスチャID(_textureNo)に対応するテクスチャをバインドする
   		gl.glBindTexture(GL10.GL_TEXTURE_2D, _textureNo);

		//テクスチャの座標と幅高さを指定
   		int rect[] = {0, _height, _width, -_height};
   
   		//バインドされているテクスチャ画像のどの部分を使うかを指定
   		((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D,
   			GL11Ext.GL_TEXTURE_CROP_RECT_OES, rect, 0);
   		//高速2D描画
   		((GL11Ext) gl).glDrawTexfOES(
   				_pos._x, 
   				SV.ScreenSize._y - ((_height*_ratio._y) + _pos._y ), 
   				_pos._z, 
   				_width*_ratio._x, 
   				_height*_ratio._y);
   		//
   	   	gl.glEnable(GL10.GL_DEPTH_TEST);
	}
    
    public void drawNumber(GL10 gl, int num)
    {
    	drawNumber(gl, num, 0, false, _ratio);
    }
    
    public void drawNumber(GL10 gl, int num, int keta)
    {
    	drawNumber(gl, num, keta, false, _ratio);
    }
    
    public void drawNumber(GL10 gl, int num, int keta, boolean isFillZero)
    {
    	drawNumber(gl, num, keta, isFillZero, _ratio);
    }
    
    public void drawNumber(GL10 gl, int num, int keta, boolean isFillZero, Vector2D ratio)
    {
    	if ( !_visible )
    	{
    		return;
    	}
    	
    	_ratio = ratio;
    	
    	gl.glDisable(GL10.GL_DEPTH_TEST);

   		//白色セット
   		gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
   		//テクスチャ0番をアクティブにする
   		gl.glActiveTexture(GL10.GL_TEXTURE0);
   		//テクスチャID(_textureNo)に対応するテクスチャをバインドする
   		gl.glBindTexture(GL10.GL_TEXTURE_2D, _textureNo);

   		String str =  String.valueOf(num);
   		if(keta == 0)keta = str.length();
   		
   		for ( int i = 0; i < keta; i++ )
   		{
   			int j;
   			if(str.length() < keta-i){
   				if(!isFillZero)continue;
   				else j = 0;
   			}else j = (int)(num/Math.pow(10, keta-(i+1))) % 10;
   			
   			//テクスチャの座標と幅高さを指定
   			int rect[] = {j*_width/10, _height, _width/10, -_height};
   
   			//バインドされているテクスチャ画像のどの部分を使うかを指定
   			((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D,
   				GL11Ext.GL_TEXTURE_CROP_RECT_OES, rect, 0);
   			//2D描画
   			((GL11Ext) gl).glDrawTexfOES(
   				(_pos._x + (i*(_width/10)*ratio._x)) / SV.VirtualRatio._x,
   				SV.ScreenSize._y - ((_height*ratio._y) + _pos._y ) / SV.VirtualRatio._y, 
   				_pos._z,
				(_width/10)*ratio._x / SV.VirtualRatio._x, 
				(_height*ratio._y) / SV.VirtualRatio._y);
   		}
   	   	gl.glEnable(GL10.GL_DEPTH_TEST);
	}
    
    public boolean hitCheck(Vector2D pos)
    {
    	_oldHit = _hit;
    	if ( pos._x >= _pos._x  && pos._x <= (_pos._x + (_width*_ratio._x) ))
    	{
        	if ( pos._y >= _pos._y  && pos._y <= (_pos._y + (_height*_ratio._y)))
        	{
        		return _hit = true;
        	}
        }
    	return _hit = false;
    }
    
    public boolean releaseCheck(Vector2D pos)
    {
    	_oldHit = _hit;
    	_hit = false;
    	if ( pos._x >= _pos._x  && pos._x <= (_pos._x + (_width*_ratio._x)))
    	{
        	if ( pos._y >= _pos._y  && pos._y <= (_pos._y + (_height*_ratio._y)))
        	{
        		if(_oldHit)return true;
        	}
        }
    	return false;
    }
}
