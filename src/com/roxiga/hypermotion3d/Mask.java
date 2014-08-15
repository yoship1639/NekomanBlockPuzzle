package com.roxiga.hypermotion3d;

import javax.microedition.khronos.opengles.GL10;

public class Mask {
	
	private Sprite2D mask;
	private int maxInTime, maxOutTime, time = -1;
	private int isIn = -1;
	
	public Mask(){}
	
	public Mask(Sprite2D mask){
		setSprite(mask);
	}
	
	public void setSprite(Sprite2D mask){
		this.mask = mask;
		mask._ratio = new Vector2D(SV.VirtualScreenSize._x/mask._width, SV.VirtualScreenSize._y/mask._height);
	}
	
	public void setMaskTime(int time){
		this.maxInTime = this.maxOutTime = this.time = time;
		isIn = 1;
	}
	
	public void update(){
		if(isIn == -1)return;
		
		if(isIn == 1){
			time--;
			if(time == -1){
				time = maxOutTime;
				isIn = 0;
			}
		}else{
			time--;
			if(time == -1){
				isIn = -1;
			}
		}
	}
	
	public void drawMask(GL10 gl){
		if(isIn == -1)return;
		if(isIn == 1)mask.draw(gl, new Vector4D(1, 1, 1, (float)Math.cos((time/(float)maxInTime)*Math.PI/2.0f)));
		else if(isIn == 0)mask.draw(gl, new Vector4D(1, 1, 1, (float)Math.cos((1.0f - (time/(float)maxInTime))*Math.PI/2.0f)));
	}
	
	public boolean isJustEndFadeIn(){
		if(isIn == 0 && time == maxOutTime)return true;
		else return false;
	}
	
	public boolean isMasking(){
		if(isIn == -1)return false;
		else return true;
	}
}
