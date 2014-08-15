package com.yoship;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.roxiga.hypermotion3d.Camera;
import com.roxiga.hypermotion3d.KeyManager;
import com.roxiga.hypermotion3d.State;
import com.roxiga.hypermotion3d.TouchManager;

public class State_Edit extends State {

	private static State_Edit instance = new State_Edit();
	private State_Edit(){}
	
	public static State_Edit getInstance(){
		return instance;
	}
	
	@Override
	public void init(GL10 gl, Context context, String str, Camera cam) {
		// TODO Auto-generated method stub

	}

	@Override
	public void proccess(TouchManager tm, KeyManager km, Camera cam) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(GL10 gl) {
		// TODO Auto-generated method stub

	}
}
