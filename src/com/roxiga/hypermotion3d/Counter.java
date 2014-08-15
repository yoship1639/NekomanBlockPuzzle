package com.roxiga.hypermotion3d;

public class Counter {
	
	private int max;
	private int value;
	
	private boolean repeat;
	private boolean visible;
	
	public Counter(){}
	
	public Counter(int max, boolean repeat){
		set(max, repeat);
	}
	
	public void set(int max, boolean repeat){
		this.max = max;
		value = 0;
		this.repeat = repeat;
		visible = true;
	}
	
	public boolean incAndCheck(){
		if(!visible)return false;
		value++;
		if(value >= max){
			if(repeat){
				value = 0;
				return true;
			}else{
				value = max;
				return true;
			}
		}
		return false;
	}
	
	public int getValue(){
		return value;
	}
	
	public int getMaxValue(){
		return max;
	}
	
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	public boolean isVisible(){
		return visible;
	}
}
