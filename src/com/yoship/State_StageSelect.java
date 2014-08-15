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
import com.roxiga.models.cubemap;
import com.roxiga.models.floor;

public final class State_StageSelect extends State {
	
	private static State_StageSelect instance = new State_StageSelect();
	private State_StageSelect(){}
	
	public static State_StageSelect getInstance(){
		return instance;
	}
	
	private static final int
	SP_LOGO = 0,
	SP_PLAY = 1,
	SP_GOTITLE = 2,
	SP_NEXT = 3,
	SP_PREV = 4,
	SP_TIMER = 5,
	SP_WALK = 6,
	SPRITE_MAX_NUM = 7;
	
	private static final int
	T_STAGE = 0,
	T_SCORE = 1,
	T_TIMER_CNT = 2,
	T_WALK_CNT = 3;
	
	private Sprite2D _sprite [] = new Sprite2D[SPRITE_MAX_NUM];
	private SpriteText _text;
	private MediaPlayer _se;
	private cubemap _cubemap = new cubemap();
	
	private PuzzleController _pc = new PuzzleController();

	@Override
	public void init(GL10 gl, Context context, String str, Camera cam) {
		
		boolean resume = Boolean.parseBoolean(str);
		//sprite
		{
			int drawable [] = {
					R.drawable.sprite_logo_stageselect,
					R.drawable.sprite_select,
					R.drawable.sprite_gotitle,
					R.drawable.sprite_next,
					R.drawable.sprite_previous,
					R.drawable.sprite_timer,
					R.drawable.sprite_walk,
			};
			for (int i = 0; i < drawable.length; i++) {
				_sprite[i] = new Sprite2D();
				_sprite[i].setTexture(gl, context.getResources(), drawable[i]);
			}
			
			Vector3D pos [] = {
					new Vector3D((SV.VirtualScreenSize._x - _sprite[SP_LOGO]._width)/2, 0, 0),
					new Vector3D((SV.VirtualScreenSize._x - _sprite[SP_PLAY]._width)/2, 480, 0),
					new Vector3D(20, SV.VirtualScreenSize._y - _sprite[SP_PLAY]._height - 20, 0),
					new Vector3D(SV.VirtualScreenSize._x - _sprite[SP_PREV]._width - 10, 480, 0),
					new Vector3D(20, 480, 0),
					new Vector3D(1000, 140, 0),
					new Vector3D(1000, 220, 0),
			};
			
			for (int i = 0; i < pos.length; i++) {
				_sprite[i]._pos = pos[i];
			}
		}
		
		//puzzlecontroller
		{
			floor fm = new floor();
			int tf [] = {
					R.drawable.tex_floor,
					R.drawable.tex_floor2,
					R.drawable.tex_floor3,
			};
			fm.setTexture(gl, context.getResources(), tf[GV.TextureQuality]);
			_pc.setFloorModel(fm);
			
			block b [] = new block[4];
			for (int i = 0; i < b.length; i++) {
				b[i] = new block();
				b[i].setTexture(gl, context.getResources(), R.drawable.tex_black);
			}
			_pc.setBlockModels(b);
			
			_pc.initStage(GV.StageData[GV.SelectLevel][GV.SelectIndex-1].data);
		}
		
		//text
		{
			if(resume && _text != null){
				_text.remake(gl);
			}else{
				_text = new SpriteText();
				_text.setSize(gl, 60);
				_text._color = new Vector4D(0, 0, 0, 1);
				
			}
		}
		
		//se
		{
			if(_se != null)_se.release();
			_se = MediaPlayer.create(context, R.raw.se_switch);
		}
		
		//CubeMap
		{
			int ts [] = {
					R.drawable.skybox_sunny,
					R.drawable.skybox_sunny2,
					R.drawable.skybox_sunny3,
			};
			_cubemap.setTexture(gl, context.getResources(), ts[GV.TextureQuality]);
			_cubemap._scale = new Vector3D(5.0f, 5.0f, 5.0f);
			_cubemap._pos._y = -140;
		}
		cam.setDist(80f);
	}

	@Override
	public void proccess(TouchManager tm, KeyManager km, Camera cam) {
		if(State.canInput){
			int touch = -1;
			if(tm._touch){
				_sprite[SP_PLAY].hitCheck(tm._pos);
				_sprite[SP_GOTITLE].hitCheck(tm._pos);
				_sprite[SP_NEXT].hitCheck(tm._pos);
				_sprite[SP_PREV].hitCheck(tm._pos);
			}else if(tm._old){
				if(_sprite[SP_PLAY].releaseCheck(tm._pos))touch = SP_PLAY;
				else if(_sprite[SP_GOTITLE].releaseCheck(tm._pos))touch = SP_GOTITLE;
				else if(_sprite[SP_NEXT].releaseCheck(tm._pos))touch = SP_NEXT;
				else if(_sprite[SP_PREV].releaseCheck(tm._pos))touch = SP_PREV;
			}
			
			if(touch != -1){
				switch(touch){
				case SP_PLAY:
					GV.HideAd();
					NextState = GV.STATE_PLAY;
					if(GV.IsPlaySE)_se.start();
					break;
				case SP_GOTITLE:
					NextState = GV.STATE_TITLE;
					if(GV.IsPlaySE)_se.start();
					break;
				case SP_NEXT:
					GV.SelectIndex++;
					if(GV.SelectIndex > GV.STAGE_MAX_NUM)GV.SelectIndex = GV.STAGE_MAX_NUM;
					else _pc.initStage(GV.StageData[GV.SelectLevel][GV.SelectIndex-1].data);
					if(GV.IsPlaySE)_se.start();
					break;
				case SP_PREV:
					GV.SelectIndex--;
					if(GV.SelectIndex < 1)GV.SelectIndex = 1;
					else _pc.initStage(GV.StageData[GV.SelectLevel][GV.SelectIndex-1].data);
					if(GV.IsPlaySE)_se.start();
					break;
				}
			}else if(km.isKeyUp(KeyEvent.KEYCODE_BACK)){
				NextState = GV.STATE_TITLE;
				if(GV.IsPlaySE)SV.PlaySE(_se);
			}
		}
		if(tm._touch && tm._pos._x > SV.VirtualScreenSize._x/2){
			float height = tm._pos._y - tm._oldPos._y;
			if(!tm._old)height = 0;
			cam.addRotXZ(height/20.0f, 70f, 10f);
		}
		cam.rotAddCameraDome(new Vector3D((_pc.getSizeX()-1)*5, 10f, (_pc.getSizeZ()-1)*5), 0.2f);
	}

	@Override
	public void draw(GL10 gl) {
		_cubemap.draw(gl);
		_pc.drawFloor(gl);
		_pc.drawBlock(gl);
		_sprite[SP_LOGO].draw(gl);
		_sprite[SP_PLAY].draw(gl, true);
		_sprite[SP_GOTITLE].draw(gl, true);
		_sprite[SP_NEXT].draw(gl, true);
		_sprite[SP_PREV].draw(gl, true);
		
		_text.setSize(gl, 60);
		_text.setText(gl, T_STAGE, GV.STRING_LEVEL[GV.SelectLevel]+" STAGE "+GV.SelectIndex, new Vector2D((SV.VirtualScreenSize._x - _text.getWidth(GV.STRING_LEVEL[GV.SelectLevel]+" STAGE "+GV.SelectIndex))/2, 180));
		if(GV.StageData[GV.SelectLevel][GV.SelectIndex-1].isCleared){
			_text.setSize(gl, 40);
			_text.setText(gl, T_SCORE, "HIGH SCORE :  "+String.format("%06d", GV.StageData[GV.SelectLevel][GV.SelectIndex-1].scoreRecord), new Vector2D(820, 80));
			_text.setText(gl, T_TIMER_CNT, String.format("%04d", GV.StageData[GV.SelectLevel][GV.SelectIndex-1].timeRecord), new Vector2D(1130, 152));
			_text.setText(gl, T_WALK_CNT, String.format("%04d", GV.StageData[GV.SelectLevel][GV.SelectIndex-1].walkRecord), new Vector2D(1130, 232));
			_text._color = new Vector4D(1, 1, 0, 1);
			_text.draw(gl, T_STAGE);
			_text._color = new Vector4D(0, 0, 0, 1);
			_text.draw(gl, T_SCORE);
			_text.draw(gl, T_TIMER_CNT);
			_text.draw(gl, T_WALK_CNT);
			_sprite[SP_TIMER].draw(gl);
			_sprite[SP_WALK].draw(gl);
		}else{
			_text._color = new Vector4D(0, 0, 1, 1);
			_text.draw(gl, T_STAGE);
		}
	}
}
