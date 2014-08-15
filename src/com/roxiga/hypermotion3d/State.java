package com.roxiga.hypermotion3d;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public abstract class State {
	
	public static int NextState = -1;
	public static boolean canInput = true;
	
	public abstract void init(GL10 gl, Context context, String str, Camera cam);
	public static int CheckNext()
	{
		if(NextState != -1){
			int n = NextState;
			NextState = -1;
			return n;
		}
		return -1;
	}
	public abstract void proccess(TouchManager tm, KeyManager km, Camera cam);
	public abstract void draw(GL10 gl);
}
