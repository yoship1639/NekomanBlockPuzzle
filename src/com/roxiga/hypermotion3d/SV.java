package com.roxiga.hypermotion3d;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;

public class SV {
	
	private SV(){}
	
	//画面の大きさを仮想サイズに合わせる場合はfalse
	public static final boolean IsCorrentPixel = false;
	//画面の仮想サイズ
	private static final Vector2D VIRTUAL_SCREEN_SIZE = new Vector2D(720, 1280);
	
	public static Vector2D ScreenSize;
	public static Vector2D VirtualScreenSize = VIRTUAL_SCREEN_SIZE;
	public static Vector2D VirtualRatio;
	
	public static Context Context = null;
	
	public static void SetScreenSize(int width, int height)
	{
		ScreenSize = new Vector2D(width, height);
		if(!IsCorrentPixel){
			if(width > height)VirtualScreenSize = new Vector2D(VIRTUAL_SCREEN_SIZE._y, VIRTUAL_SCREEN_SIZE._x);
			else VirtualScreenSize = new Vector2D(VIRTUAL_SCREEN_SIZE._x, VIRTUAL_SCREEN_SIZE._y);
		}else VirtualScreenSize = ScreenSize;
    	VirtualRatio = new Vector2D(VirtualScreenSize._x/ScreenSize._x, VirtualScreenSize._y/ScreenSize._y);
    	System.out.println(ScreenSize);
    	System.out.println(VirtualScreenSize);
    	System.out.println(VirtualRatio);
	}
	
	public static void SavePref(String key, String value){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Context);
		Editor editor = pref.edit();
			
		try{
			Log.i("savepref", "key:"+key+", value:"+value);
			editor.putString(key, Crypto.encrypt(key, value));
			editor.commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	    
	public static int LoadPref_Int(String key){
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Context);
    	
    	try {
    		String get = pref.getString(key, null);
    		if(get == null)return 0;
    		Log.i("loadpref", "key:"+key+" value:"+Crypto.decrypt(key, get));
    		return Integer.parseInt(Crypto.decrypt(key, get));
    	} catch (Exception e) {
			e.printStackTrace();
    	}
    	return 0;
	}
	
	public static float LoadPref_Float(String key){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Context);
		
		try {
			String get = pref.getString(key, null);
			if(get == null)return 0;
			Log.i("loadpref", "key:"+key+" value:"+Crypto.decrypt(key, get));
			return Float.parseFloat(Crypto.decrypt(key, get));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static String LoadPref_String(String key){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Context);
		
		try {
			String get = pref.getString(key, null);
			if(get == null)return "";
			Log.i("loadpref", "key:"+key+" value:"+Crypto.decrypt(key, get));
			return Crypto.decrypt(key, get);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static boolean LoadPref_Boolean(String key){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Context);
		
		try {
			String get = pref.getString(key, null);
			if(get == null)return false;
			Log.i("loadpref", "key:"+key+" value:"+Crypto.decrypt(key, get));
			String str = Crypto.decrypt(key, get);
			if(str.equals(String.valueOf(true)))return true;
			else return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void PlaySE(MediaPlayer mp){
		if(mp != null){
			if(mp.isPlaying()){
				mp.seekTo(0);
			}
			mp.start();
		}
	}
	
}
