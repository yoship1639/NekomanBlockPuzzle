package com.roxiga.hypermotion3d;

import java.util.ArrayList;

public class KeyManager {
	
	private class KeyChecker{
		public int keyCode;
		public boolean isDown = false;
		public boolean isOldDown = false;
	}
	
	private ArrayList<KeyChecker> key = new ArrayList<KeyChecker>();
	
	private int action = -1;
	private int undecideKey;
	
	public void addCheckKey(int keyCode){
		KeyChecker kc = new KeyChecker();
		kc.keyCode = keyCode;
		key.add(kc);
	}
	
	public void onKeyDown(int keyCode){
		action = 0;
		undecideKey = keyCode;
	}
	
	public void onKeyUp(int keyCode){
		action = 1;
		undecideKey = keyCode;
	}
	
	public void decideKey(){
		
		for (int i = 0; i < key.size(); i++) {
			key.get(i).isOldDown = key.get(i).isDown;
		}
		
		if(action != -1){
			for (int i = 0; i < key.size(); i++) {
				if(key.get(i).keyCode == undecideKey){
					if(action == 0){
						key.get(i).isDown = true;
					}else{
						key.get(i).isDown = false;
					}
				}
			}
			action = -1;
		}
		
	}
	
	public boolean isKeyDown(int keyCode){
		for (int i = 0; i < key.size(); i++) {
			if(key.get(i).keyCode == keyCode){
				if(key.get(i).isDown)return true;
			}
		}
		return false;
	}
	
	public boolean isKeyJustDown(int keyCode){
		for (int i = 0; i < key.size(); i++) {
			if(key.get(i).keyCode == keyCode){
				if(!key.get(i).isOldDown && key.get(i).isDown)return true;
			}
		}
		return false;
	}
	
	public boolean isKeyUp(int keyCode){
		for (int i = 0; i < key.size(); i++) {
			if(key.get(i).keyCode == keyCode){
				if(key.get(i).isOldDown && !key.get(i).isDown)return true;
			}
		}
		return false;
	}
}
