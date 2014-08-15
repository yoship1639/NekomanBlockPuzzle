package com.yoship;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.KeyEvent;

import com.roxiga.hypermotion3d.Camera;
import com.roxiga.hypermotion3d.KeyManager;
import com.roxiga.hypermotion3d.SV;
import com.roxiga.hypermotion3d.Sprite2D;
import com.roxiga.hypermotion3d.SpriteText;
import com.roxiga.hypermotion3d.State;
import com.roxiga.hypermotion3d.TouchManager;
import com.roxiga.hypermotion3d.Vector2D;
import com.roxiga.hypermotion3d.Vector3D;
import com.roxiga.hypermotion3d.Vector4D;
import com.roxiga.models.block;
import com.roxiga.models.floor;

public final class State_Setting extends State {
	
	private static State_Setting instance = new State_Setting();
	private State_Setting(){}
	
	public static State_Setting getInstance(){
		return instance;
	}
	
	private static final int 
	LOGO = 0,
	RETURN = 1,
	FPS_ON = 2,
	FPS_OFF = 3,
	SE_ON = 4,
	SE_OFF = 5,
	TEX_HIGH = 6,
	TEX_MIDDLE = 7,
	TEX_LOW = 8,
	CHANGE = 9,
	SPRITE_MAX_NUM = 10;
	
	private Sprite2D _sprite [] = new Sprite2D[SPRITE_MAX_NUM];
	private SpriteText _text;
	private MediaPlayer _se;
	private floor _floorModel [] = new floor[3];
	private block _blockModel [] = new block[3];

	@Override
	public void init(GL10 gl, Context context, String str, Camera cam) {
		
		boolean resume = Boolean.parseBoolean(str);
		//Sprite
		{
			int drawable [] = {
					R.drawable.sprite_logo_setting,
					R.drawable.sprite_return,
					R.drawable.sprite_on,
					R.drawable.sprite_off,
					R.drawable.sprite_on,
					R.drawable.sprite_off,
					R.drawable.sprite_high,
					R.drawable.sprite_middle,
					R.drawable.sprite_low,
					R.drawable.sprite_change,
			};
			
			for (int i = 0; i < drawable.length; i++) {
				_sprite[i] = new Sprite2D();
				_sprite[i].setTexture(gl, context.getResources(), drawable[i]);
			}
			
			Vector3D pos [] = {
					new Vector3D((SV.VirtualScreenSize._x - _sprite[LOGO]._width)/2, 0, 0),
					new Vector3D((SV.VirtualScreenSize._x - _sprite[RETURN]._width)/2, SV.VirtualScreenSize._y - _sprite[LOGO]._height - 10, 0),
					new Vector3D(700, 120, 0),
					new Vector3D(800, 120, 0),
					new Vector3D(700, 200, 0),
					new Vector3D(800, 200, 0),
					new Vector3D(700, 280, 0),
					new Vector3D(850, 280, 0),
					new Vector3D(1000, 280, 0),
					new Vector3D(500, 360, 0),
			};
			
			for (int i = 0; i < drawable.length; i++) {
				_sprite[i]._pos = pos[i];
			}
		}
		
		//Model
		{
			int tf [] = {
					R.drawable.tex_floor,
					R.drawable.tex_floor2,
					R.drawable.tex_floor3,
			};
			for (int i = 0; i < _floorModel.length; i++) {
				_floorModel[i] = new floor();
				_floorModel[i].setTexture(gl, context.getResources(), tf[i]);
				_floorModel[i]._pos = new Vector3D(-15, 0, 0);
			}
			
			int tb [] = {
					R.drawable.tex_red,
					R.drawable.tex_red2,
					R.drawable.tex_red3,
			};
			for (int i = 0; i < _blockModel.length; i++) {
				_blockModel[i] = new block();
				_blockModel[i].setTexture(gl, context.getResources(), tb[i]);
				_blockModel[i]._pos = new Vector3D(15, 0, 0);
			}
		}
		
		//Text
		{
			if(resume && _text != null){
				_text.remake(gl);
			}else{
				_text = new SpriteText();
				_text._color = new Vector4D(0, 0, 0, 1);
				_text.setSize(gl, 40);
				String text [] = {
						context.getString(R.string.setting_show_fps),
						context.getString(R.string.setting_play_se),
						context.getString(R.string.setting_texture_quality),
						context.getString(R.string.setting_drawing_method),
				};
				Vector2D pos [] = {
						new Vector2D(100, 128 + 80*0),
						new Vector2D(100, 128 + 80*1),
						new Vector2D(100, 128 + 80*2),
						new Vector2D(100, 128 + 80*3),
				};
				_text.setText(gl, text, pos);
			}
		}
		
		//SE
		{
				if(_se != null)_se.release();
				_se = MediaPlayer.create(context, R.raw.se_switch);
		}
		
		cam._look = new Vector3D(0, 14, 0);
		cam._eye = new Vector3D(0, 20, -50);
	}

	@Override
	public void proccess(TouchManager tm, KeyManager km, Camera cam) {
		
		if(State.canInput){
			int touch = -1;
			if(tm._touch){
				for(int i=1; i<SPRITE_MAX_NUM; i++)_sprite[i].hitCheck(tm._pos);
			}else if(tm._old){
				for(int i=1; i<SPRITE_MAX_NUM; i++){
					if(_sprite[i].releaseCheck(tm._pos))touch = i;
				}
			}
			
			if(touch != -1){
				switch(touch){
				case RETURN:
					GV.ShowAd();
					NextState = GV.STATE_TITLE;
					break;
				case FPS_ON:
					GV.IsShowFPS = true;
					break;
				case FPS_OFF:
					GV.IsShowFPS = false;
					break;
				case SE_ON:
					GV.IsPlaySE = true;
					break;
				case SE_OFF:
					GV.IsPlaySE = false;
					break;
				case TEX_HIGH:
					GV.TextureQuality = GV.QUALITY_HIGH;
					break;
				case TEX_MIDDLE:
					GV.TextureQuality = GV.QUALITY_MIDDLE;
					break;
				case TEX_LOW:
					GV.TextureQuality = GV.QUALITY_LOW;
					break;
				case CHANGE:
					GV.DrawingMethod++;
					if(GV.DrawingMethod > GV.DRAW_ANY_FRAMES_SKIP){
						GV.DrawingMethod = GV.DRAW_USUALLY;
						GV.FrameSkip = 0;
					}
					if(GV.DrawingMethod == GV.DRAW_USUALLY)GV.setDrawingCacheEnable(false);
			    	else GV.setDrawingCacheEnable(true);
					break;
				}
				if(GV.IsPlaySE)SV.PlaySE(_se);
			}
			else if(km.isKeyUp(KeyEvent.KEYCODE_BACK)){
				GV.ShowAd();
				NextState = GV.STATE_TITLE;
				if(GV.IsPlaySE)SV.PlaySE(_se);
			}
		}
		for (int i = 0; i < _floorModel.length; i++) {
			_floorModel[i]._rotate._y += 0.2f;
			_blockModel[i]._rotate._y += 0.2f;
			if(_floorModel[i]._rotate._y > 360f)_floorModel[i]._rotate._y -= 360f;
			if(_blockModel[i]._rotate._y > 360f)_blockModel[i]._rotate._y -= 360f;
		}
	}

	@Override
	public void draw(GL10 gl) {
		Vector4D color = new Vector4D(0.7f, 0.7f, 0.7f, 0.6f);
		_sprite[LOGO].draw(gl);
		_sprite[RETURN].draw(gl, true);
		_floorModel[GV.TextureQuality].draw(gl);
		_blockModel[GV.TextureQuality].draw(gl);
		if(GV.IsShowFPS){
			_sprite[FPS_ON].draw(gl);
			_sprite[FPS_OFF].draw(gl, color);
		}
		else{
			_sprite[FPS_ON].draw(gl, color);
			_sprite[FPS_OFF].draw(gl);
		}
		if(GV.IsPlaySE){
			_sprite[SE_ON].draw(gl);
			_sprite[SE_OFF].draw(gl, color);
		}
		else{
			_sprite[SE_ON].draw(gl, color);
			_sprite[SE_OFF].draw(gl);
		}
		switch(GV.TextureQuality){
		case GV.QUALITY_HIGH:
			_sprite[TEX_HIGH].draw(gl);
			_sprite[TEX_MIDDLE].draw(gl, color);
			_sprite[TEX_LOW].draw(gl, color);
			break;
		case GV.QUALITY_MIDDLE:
			_sprite[TEX_HIGH].draw(gl, color);
			_sprite[TEX_MIDDLE].draw(gl);
			_sprite[TEX_LOW].draw(gl, color);
			break;
		case GV.QUALITY_LOW:
			_sprite[TEX_HIGH].draw(gl, color);
			_sprite[TEX_MIDDLE].draw(gl, color);
			_sprite[TEX_LOW].draw(gl);
			break;
		}
		_sprite[CHANGE].draw(gl, true);
		String s [] = {
				SV.Context.getString(R.string.setting_draw_usually),
				SV.Context.getString(R.string.setting_draw_one_frame_skip),
				SV.Context.getString(R.string.setting_draw_any_frames_skip),
		};
		_text.setSize(gl, 30);
		_text.setText(gl, 4, "*"+s[GV.DrawingMethod], new Vector2D(120, 128 + 80*4));
		for (int i = 0; i < 5; i++) {
			_text.draw(gl, i);
		}
	}
}
