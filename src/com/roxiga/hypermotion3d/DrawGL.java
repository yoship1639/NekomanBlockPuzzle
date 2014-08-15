package com.roxiga.hypermotion3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;

public class DrawGL {
	
	public static void DrawFillBox(GL10 gl, int x, int y, int width, int height, Vector4D color)
	{
		if(width == 0 || height == 0)return;
		float wh = SV.ScreenSize._y;

		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_DEPTH_TEST);

		gl.glPushMatrix();
		{
			gl.glLoadIdentity();
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glPushMatrix();
			{
				gl.glLoadIdentity();
				GLU.gluOrtho2D(gl, 0, SV.ScreenSize._x, 0, wh);
				
				float vertex [] = {
					x/SV.VirtualRatio._x, (wh - (y+height))/SV.VirtualRatio._y,
					(x+width)/SV.VirtualRatio._x, (wh - (y+height))/SV.VirtualRatio._y,
					x/SV.VirtualRatio._x, (wh - y)/SV.VirtualRatio._y,
					(x+width)/SV.VirtualRatio._x, (wh - y)/SV.VirtualRatio._y,
				};
				
				ByteBuffer vbb = ByteBuffer.allocateDirect(vertex.length*4);
		        vbb.order(ByteOrder.nativeOrder());
		        FloatBuffer vertexBuffer = vbb.asFloatBuffer();
				vertexBuffer.put(vertex);
				vertexBuffer.position(0);
				
				gl.glEnable(GL10.GL_ALPHA_TEST);
		    	gl.glEnable(GL10.GL_BLEND);
		    	gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				gl.glColor4f(color._x, color._y, color._z, color._w);
				
				gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
				
				gl.glDisable(GL10.GL_BLEND);
				
				gl.glPopMatrix();
			}
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glPopMatrix();
		}
		gl.glEnable(GL10.GL_DEPTH_TEST);

	}
}
