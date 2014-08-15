package com.yoship;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.KeyEvent;

import com.roxiga.hypermotion3d.*;
import com.roxiga.models.*;

public final class State_Title extends State {
	
	private static State_Title instance = new State_Title();
	private State_Title(){}
	
	public static State_Title getInstance(){
		return instance;
	}
	
	private static final int 
		LOGO = 0,
		START = 1,
		SETTING = 2,
		QUIT = 3,
		HOWTOPLAY = 4,
		EDIT = 5,
		CHARANGE = 6,
		SPRITE_MAX_NUM = 7;
	
	private Sprite2D _sprite [] = new Sprite2D[SPRITE_MAX_NUM];
	private SpriteText _text;
	private cubemap _cubemap = new cubemap();
	private MediaPlayer _se;
	
	private Random rand = new Random();
	private PuzzleController _pc;
	private Vector3D camPos;

	@Override
	public void init(GL10 gl, Context context, String str, Camera cam) {
		
		boolean resume = Boolean.parseBoolean(str);
		//Sprite
		{
			final int drawable [] = {
					R.drawable.sprite_logo_title,
					R.drawable.sprite_start,
					R.drawable.sprite_settings,
					R.drawable.sprite_quit,
					R.drawable.sprite_howtoplay,
					R.drawable.sprite_editmode,
					R.drawable.sprite_charange,
			};
			
			for(int i=0; i<SPRITE_MAX_NUM; i++){
				_sprite[i] = new Sprite2D();
				_sprite[i].setTexture(gl, context.getResources(), drawable[i]);
			}
			_sprite[LOGO]._ratio = new Vector2D(1.5f, 1.5f);
			
			final Vector3D pos [] = {
					new Vector3D((SV.VirtualScreenSize._x - _sprite[LOGO]._width*_sprite[LOGO]._ratio._x)/2, SV.VirtualRatio._y * 50, 0),
					new Vector3D(64, 300+(80*0), 0),
					new Vector3D(64, 300+(80*1), 0),
					new Vector3D(64, 300+(80*2), 0),
					new Vector3D(364, 300+(80*0), 0),
					new Vector3D(364, 300+(80*1), 0),
					new Vector3D(364, 300+(80*2), 0),
			};
			for(int i=0; i<SPRITE_MAX_NUM; i++)_sprite[i]._pos = pos[i];
		}
		
		//PuzzleController
		{
			byte data [][][] = {
				{
					{0,1,1,1,0},
					{1,1,1,1,1},
					{1,1,1,1,1},
					{1,1,1,1,1},
					{0,1,1,1,0}
				},
				{
					{0,0,0,0,0},
					{0,2,0,3,0},
					{0,0,6,0,0},
					{0,4,0,5,0},
					{0,0,0,0,0},
				}
			};
			if(!resume || _pc == null){
				nekoman player = new nekoman();
				player._scale = new Vector3D(1.3f, 1.3f, 1.3f);
				_pc = new PuzzleController(data, player);
			}
			_pc._pc._model.setTexture(gl, context.getResources(), R.drawable.tex_neko);
			
			final int tf [] = {
					R.drawable.tex_floor,
					R.drawable.tex_floor2,
					R.drawable.tex_floor3,
			};
			floor f = new floor();
			f.setTexture(gl, context.getResources(), tf[GV.TextureQuality]);
			_pc.setFloorModel(f);
			
			final int tb [][] = {
				{
					R.drawable.tex_green,
					R.drawable.tex_red,
					R.drawable.tex_blue,
					R.drawable.tex_yellow
				},
				{
					R.drawable.tex_green2,
					R.drawable.tex_red2,
					R.drawable.tex_blue2,
					R.drawable.tex_yellow2
				},
				{
					R.drawable.tex_green3,
					R.drawable.tex_red3,
					R.drawable.tex_blue3,
					R.drawable.tex_yellow3
				},
			};
			block b [] = new block[4];
			for (int i = 0; i < b.length; i++) {
				b[i] = new block();
				b[i].setTexture(gl, context.getResources(), tb[GV.TextureQuality][i]);
			}
			_pc.setBlockModels(b);
		}
		
		//Text
		{
			if(resume && _text != null){
				_text.remake(gl);
			}else{
				_text = new SpriteText();
				_text._color = new Vector4D(0, 0, 0, 1);
				_text.setSize(gl, 30);
				if(GV.version == GV.VERSION_TRIAL)_text.setText(gl, "Trial: "+GV.versionName, new Vector2D(_sprite[LOGO]._pos._x + 720, 240));
				else _text.setText(gl, "Release: "+GV.versionName, new Vector2D(_sprite[LOGO]._pos._x + 720, 240));
			}
		}
		
		//CubeMap
		{
			final int ts [] = {
					R.drawable.skybox_sunny,
					R.drawable.skybox_sunny2,
					R.drawable.skybox_sunny3,
			};
			_cubemap.setTexture(gl, context.getResources(), ts[GV.TextureQuality]);
			_cubemap._scale = new Vector3D(5.0f, 5.0f, 5.0f);
			_cubemap._pos._y = -140;
		}
		
		//SE
		{
			if(_se != null)_se.release();
			_se = MediaPlayer.create(context, R.raw.se_switch);
		}
		cam.setDist(60f);
	}

	@Override
	public void proccess(TouchManager tm, KeyManager km, Camera cam) {

		if(State.canInput){
			int touch = -1;
			if(tm._touch){
				for(int i=1; i<SPRITE_MAX_NUM-2; i++)_sprite[i].hitCheck(tm._pos);
				if(GV.version == GV.VERSION_RELEASE){
					for(int i=SPRITE_MAX_NUM-2; i<SPRITE_MAX_NUM; i++)_sprite[i].hitCheck(tm._pos);
				}
			}else if(tm._old){
				for(int i=1; i<SPRITE_MAX_NUM-2; i++){
					if(_sprite[i].releaseCheck(tm._pos))touch = i-1;
				}
				if(GV.version == GV.VERSION_RELEASE){
					for(int i=SPRITE_MAX_NUM-2; i<SPRITE_MAX_NUM; i++){
						if(_sprite[i].releaseCheck(tm._pos))touch = i-1;
					}
				}
			}
			
			if(touch != -1){
				final int next [] = {
						GV.STATE_STAGE_SELECT,
						GV.STATE_SETTING,
						-2,
						GV.STATE_TUTORIAL,
						GV.STATE_EDIT,
						GV.STATE_CHARANGE,
				};
				NekomanBlockPuzzleActivity act = (NekomanBlockPuzzleActivity)SV.Context;
				if(touch != 0)act.hideAd();
				NextState = next[touch];
				if(GV.IsPlaySE)SV.PlaySE(_se);
			}
			else if(km.isKeyUp(KeyEvent.KEYCODE_BACK)){
				NextState = -2;
				if(GV.IsPlaySE)SV.PlaySE(_se);
			}
			
			if(tm._touch && tm._pos._x > SV.VirtualScreenSize._x/2){
				float height = tm._pos._y - tm._oldPos._y;
				if(!tm._old)height = 0;
				cam.addRotXZ(height/20f, 70f, 10f);
			}
		}
		
		int dir = -1;
		if(rand.nextInt(40) == 0){
			dir = rand.nextInt(4);
		}
		_pc.playerUpdate(dir);
		cam.rotAddCameraDome(new Vector3D(20f, 10f, 20f), 0.2f);
		camPos = cam._eye;
	}

	@Override
	public void draw(GL10 gl) {
		_cubemap.draw(gl);
		_pc.drawFloor(gl);
		_pc.drawModel(gl, GV.FrameSkip);
		_pc.drawBlock(gl, camPos);
		for(int i=0; i<SPRITE_MAX_NUM-2; i++)_sprite[i].draw(gl, true);
		if(GV.version == GV.VERSION_TRIAL){
			for(int i=SPRITE_MAX_NUM-2; i<SPRITE_MAX_NUM; i++)_sprite[i].draw(gl, new Vector4D(0.5f, 0.5f, 0.5f, 1.0f));
		}else{
			for(int i=SPRITE_MAX_NUM-2; i<SPRITE_MAX_NUM; i++)_sprite[i].draw(gl, true);
		}
		_text.draw(gl);
	}
}
