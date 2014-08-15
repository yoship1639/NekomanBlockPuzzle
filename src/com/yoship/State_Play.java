package com.yoship;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.KeyEvent;

import com.roxiga.hypermotion3d.Camera;
import com.roxiga.hypermotion3d.Counter;
import com.roxiga.hypermotion3d.KeyManager;
import com.roxiga.hypermotion3d.SV;
import com.roxiga.hypermotion3d.Sprite2D;
import com.roxiga.hypermotion3d.SpriteText;
import com.roxiga.hypermotion3d.State;
import com.roxiga.hypermotion3d.TouchManager;
import com.roxiga.hypermotion3d.Vector2D;
import com.roxiga.hypermotion3d.Vector2I;
import com.roxiga.hypermotion3d.Vector3D;
import com.roxiga.hypermotion3d.PlayerController.Direction;
import com.roxiga.hypermotion3d.Vector4D;
import com.roxiga.models.block;
import com.roxiga.models.cubemap;
import com.roxiga.models.floor;
import com.roxiga.models.nekoman;

public final class State_Play extends State {
	
	private static State_Play instance = new State_Play();
	private State_Play(){}
	
	public static State_Play getInstance(){
		return instance;
	}
	
	private static final int 
	SP_ARROW_UP = 0,
	SP_ARROW_RIGHT = 1,
	SP_ARROW_DOWN = 2,
	SP_ARROW_LEFT = 3,
	SP_RESUME = 4,
	SP_RETRY = 5,
	SP_GO_STAGESELECT = 6,
	SP_GO_TITLE = 7,
	SP_GO_NEXT = 8,
	SP_IN = 9,
	SP_OUT = 10,
	SP_CAMERA = 11,
	SP_CLEAR_LOGO = 12,
	SP_RESULT_LOGO = 13,
	SP_PAUSE_LOGO = 14,
	SP_OK = 15,
	SP_TIMER = 16,
	SP_WALK = 17,
	SPRITE_MAX_NUM = 18;
	
	private static final int 
	S_READY = 0,
	S_GO = 1,
	S_PLAY = 2,
	S_PAUSE = 3,
	S_CLEAR = 4,
	S_RESULT = 5,
	S_NEXT = 6;
	
	private static final int
	T_STAGE = 0,
	T_READY = 1,
	T_GO = 2,
	T_SCORE = 3,
	T_SCORE_CNT = 4,
	T_WALK_CNT = 5,
	T_TIME_CNT = 6;
	
	private static final int 
	SE_BUTTON = 0,
	SE_APPLAUSE = 1,
	SE_MAX_NUM = 2;
	
	private static final int 
	CNT_READY = 0,
	CNT_GO = 1,
	CNT_CAMROT = 2,
	CNT_CLEAR = 3,
	CNT_TIMEADD = 4,
	CNT_TIME = 5,
	CNT_WALK = 6,
	CNT_ROTPIBOT = 7,
	CNT_MAX_NUM = 8;
	
	private Sprite2D _sprite [] = new Sprite2D[SPRITE_MAX_NUM];
	private SpriteText _text;
	private cubemap _cubemap = new cubemap();
	private MediaPlayer _se[] = new MediaPlayer[SE_MAX_NUM];
	private Counter counter[] = new Counter[CNT_MAX_NUM];
	
	private PuzzleController _pc;
	
	private Camera _cam = new Camera();
	private int camDir;
	private int rotDir;
	private Vector2D rotPibot;
	
	private int score;
	private int _state;

	@Override
	public void init(GL10 gl, Context context, String str, Camera cam) {
		boolean resume = Boolean.parseBoolean(str);
		//puzzlecontroller
		{
			if(str != null){
				if(GV.SelectLevel <= GV.LEVEL_LUNATIC){
					if(!resume || _pc == null){
						_state = S_READY;
						score = 0;
						camDir = 0;
						_text = new SpriteText();
						nekoman player = new nekoman();
						player._scale = new Vector3D(1.3f, 1.3f, 1.3f);
						_pc = new PuzzleController(GV.StageData[GV.SelectLevel][GV.SelectIndex-1].data, player);
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
			}
		}
		
		//Sprite
		{
			float vsx = SV.VirtualScreenSize._x;
			for (int i = 0; i < SPRITE_MAX_NUM; i++) {
				_sprite[i] = new Sprite2D();
			}
			int index = SP_ARROW_UP;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_up);
			_sprite[index]._pos = new Vector3D(10 + 140*1, 300 + 140*0, 0);
			
			index = SP_ARROW_RIGHT;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_right);
			_sprite[index]._pos = new Vector3D(10 + 140*2, 300 + 140*1, 0);
			
			index = SP_ARROW_DOWN;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_down);
			_sprite[index]._pos = new Vector3D(10 + 140*1, 300 + 140*2, 0);
			
			index = SP_ARROW_LEFT;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_left);
			_sprite[index]._pos = new Vector3D(10 + 140*0, 300 + 140*1, 0);
			
			index = SP_RESUME;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_resume);
			_sprite[index]._pos = new Vector3D((vsx - _sprite[index]._width)/2, 200, 0);
			
			index = SP_RETRY;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_retry);
			_sprite[index]._pos = new Vector3D((vsx - _sprite[index]._width)/2, 300, 0);
			
			index = SP_GO_STAGESELECT;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_stageselect);
			_sprite[index]._pos = new Vector3D((vsx - _sprite[index]._width)/2, 400, 0);
			
			index = SP_GO_TITLE;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_gotitle);
			_sprite[index]._pos = new Vector3D((vsx - _sprite[index]._width)/2, 500, 0);
			
			index = SP_GO_NEXT;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_nextstage);
			_sprite[index]._pos = new Vector3D((vsx - _sprite[index]._width)/2, 200, 0);
			
			index = SP_IN;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_plus);
			_sprite[index]._pos = new Vector3D(vsx - ((_sprite[index]._width+16)*2), 20, 0);
			
			index = SP_OUT;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_minus);
			_sprite[index]._pos = new Vector3D(vsx - ((_sprite[index]._width+16)*1), 20, 0);
			
			index = SP_CAMERA;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_camera);
			_sprite[index]._pos = new Vector3D(vsx - ((_sprite[index]._width+16)*3), 20, 0);
			
			index = SP_CLEAR_LOGO;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_clear);
			_sprite[index]._pos = new Vector3D((vsx - _sprite[index]._width)/2, 200, 0);
			
			index = SP_RESULT_LOGO;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_result);
			_sprite[index]._pos = new Vector3D((vsx - _sprite[index]._width)/2, 50, 0);
			
			index = SP_PAUSE_LOGO;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_logo_pause);
			_sprite[index]._pos = new Vector3D((vsx - _sprite[index]._width)/2, 20, 0);
			
			index = SP_OK;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_ok);
			_sprite[index]._pos = new Vector3D((vsx - _sprite[index]._width)/2, 500, 0);
			
			index = SP_TIMER;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_timer);
			_sprite[index]._pos = new Vector3D(20, 100, 0);
			
			index = SP_WALK;
			_sprite[index].setTexture(gl, context.getResources(), R.drawable.sprite_walk);
			_sprite[index]._pos = new Vector3D(20, 180, 0);
			
		}
		
		//CubeMap
		{
			final int ts [][] = {
					{
						R.drawable.skybox_sunny,
						R.drawable.skybox_sunny2,
						R.drawable.skybox_sunny3,
					},
					{
						R.drawable.skybox_noon,
						R.drawable.skybox_noon2,
						R.drawable.skybox_noon3,
					},
					{
						R.drawable.skybox_dawndusk,
						R.drawable.skybox_dawndusk2,
						R.drawable.skybox_dawndusk3,
					},
					{
						R.drawable.skybox_eerie,
						R.drawable.skybox_eerie2,
						R.drawable.skybox_eerie3,
					},
					{
						R.drawable.skybox_gensou,
						R.drawable.skybox_gensou2,
						R.drawable.skybox_gensou3,
					},
			};
			if(GV.SelectLevel == 0){
				_cubemap.setTexture(gl, context.getResources(), ts[GV.StageData[GV.SelectLevel][GV.SelectIndex-1].difficulty][GV.TextureQuality]);
			}else _cubemap.setTexture(gl, context.getResources(), ts[GV.SelectLevel-1][GV.TextureQuality]);
			_cubemap._scale = new Vector3D(5.0f, 5.0f, 5.0f);
			_cubemap._pos._y = -140;
		}
		
		//text
		{
			if(resume && _text != null){
				_text.remake(gl);
			}else{
				_text = new SpriteText();
				_text.setSize(gl, 50);
				_text.setText(gl, T_STAGE, "["+GV.STRING_LEVEL[GV.SelectLevel]+"]  STAGE "+GV.SelectIndex, new Vector2D(20, 20));
				_text.setSize(gl, 60);
				_text.setText(gl, T_READY, "Ready...", new Vector2D((SV.VirtualScreenSize._x - _text.getWidth("Ready..."))/2, 200));
				_text.setText(gl, T_GO, "GO!", new Vector2D((SV.VirtualScreenSize._x - _text.getWidth("GO!"))/2, 200));
				_text.setSize(gl, 40);
				_text.setText(gl, T_SCORE, "SCORE:", new Vector2D(450, 80));
			}
			if(_state == S_RESULT){
				_text.setPosition(T_SCORE, new Vector2D(450, 250));
				_text.setPosition(T_SCORE_CNT, new Vector2D(600, 250));
				_sprite[SP_TIMER]._pos = new Vector3D(450, 330, 0);
				_text.setPosition(T_TIME_CNT, new Vector2D(600, 342));
				_sprite[SP_WALK]._pos = new Vector3D(450, 410, 0);
				_text.setPosition(T_WALK_CNT, new Vector2D(600, 422));
			}
		}
		
		//se
		{
			_pc.setBlockMoveSE(R.raw.se_block);
			_pc.setBlockDisSE(R.raw.se_dis);
			_pc.setModelMoveSE(R.raw.se_jump);
			if(_se[SE_BUTTON] != null)_se[SE_BUTTON].release();
			_se[SE_BUTTON] = MediaPlayer.create(context, R.raw.se_switch);
			if(_se[SE_APPLAUSE] != null)_se[SE_APPLAUSE].release();
			_se[SE_APPLAUSE] = MediaPlayer.create(context, R.raw.se_applause);
		}
		
		if(_state == S_READY){
			for (int i = 0; i < counter.length; i++) {
				counter[i] = new Counter();
			}
			counter[CNT_READY].set(150, false);
			counter[CNT_TIMEADD].set(60, true);
			counter[CNT_TIME].set(9999, false);
			counter[CNT_WALK].set(9999, false);
			score = 0;
			rotPibot = new Vector2D();
			cam.setDist(60f);
			cam.setRotXZ(30);
		}
	}
	

	@Override
	public void proccess(TouchManager tm, KeyManager km, Camera cam) {
		int dir = -1;
		float rotY = -90*camDir;
		switch(_state){
		
		case S_READY:
			if(counter[CNT_READY].incAndCheck()){
				counter[CNT_GO] = new Counter(60, false);
				_state = S_GO;
			}
			cam.rotCameraDome(
					_pc._pc._model._pos, 
					(float)Math.sin((counter[0].getValue()*0.6f*Math.PI)/180.0f)*360.0f, 
					80f - (counter[0].getValue()/3f), 
					110f - (counter[0].getValue()/3f));
			break;
			
		case S_GO:
			if(counter[CNT_GO].incAndCheck())_state = S_PLAY;
			
		case S_PLAY:
			if(counter[CNT_TIMEADD].incAndCheck()){
				counter[CNT_TIME].incAndCheck();
			}
			if(State.canInput){
				int touch = -1;
				if(tm._touch){
					for(int i=0; i<=SP_ARROW_LEFT; i++){
						if(_sprite[i].hitCheck(tm._pos))touch = i;
					}
					for (int i = SP_IN; i <= SP_OUT; i++) {
						if(_sprite[i].hitCheck(tm._pos))touch = i;
					}
				}else if(tm._old){
					for(int i=0; i<=SP_ARROW_LEFT; i++){
						_sprite[i].releaseCheck(tm._pos);
					}
					for (int i = SP_IN; i <= SP_OUT; i++) {
						_sprite[i].releaseCheck(tm._pos);
					}
				}
				if(touch >= SP_ARROW_UP && touch <= SP_ARROW_LEFT){
					if(!counter[CNT_CAMROT].isVisible()){
						dir = camDir + touch;
						if(dir > Direction.LEFT)dir -= 4;
					}
				}else if(touch == SP_IN){
					cam.addDist(-0.5f, 100f, 30f);
				}else if(touch == SP_OUT){
					cam.addDist(0.5f, 100f, 30f);
				}else if(km.isKeyUp(KeyEvent.KEYCODE_BACK)){
					_state = S_PAUSE;
					if(GV.IsPlaySE)_se[SE_BUTTON].start();
				}
				
				if(tm._touch && tm._pos._x > SV.VirtualScreenSize._x/2){
					float height = tm._pos._y - tm._oldPos._y;
					if(!tm._old)height = 0;
					cam.addRotXZ(height/20f, 80, 10);
					if(!tm._old){
						counter[CNT_ROTPIBOT].set(9999, false);
						rotPibot = tm._pos;
					}else if(counter[CNT_ROTPIBOT].getValue() < 30){
						if(rotPibot._x - tm._pos._x > 300){
							counter[CNT_CAMROT].set(40, false);
							rotDir = -1;
						}else if(rotPibot._x - tm._pos._x < -300){
							counter[CNT_CAMROT].set(40, false);
							rotDir = 1;
						}
					}
				}
			}
			counter[CNT_ROTPIBOT].incAndCheck();
			Vector2I s;
			if(!(s = _pc.playerUpdate(dir)).equals(new Vector2I())){
				int p = (int) Math.pow(2, s._y);
				score += s._x * p * 100;
			}
			if(!_pc._pc._isMove && _pc._pc._oldMove){
				counter[CNT_WALK].incAndCheck();
			}
			if(counter[CNT_CAMROT].isVisible()){
				if(rotDir == 1)rotY = -90*camDir - (float)Math.sin((((counter[CNT_CAMROT].getValue()*90.0f)/counter[CNT_CAMROT].getMaxValue())*Math.PI)/180.0f)*90;
				else rotY = -90*camDir + (float)Math.sin((((counter[CNT_CAMROT].getValue()*90.0f)/counter[CNT_CAMROT].getMaxValue())*Math.PI)/180.0f)*90;
			}
			if(counter[CNT_CAMROT].incAndCheck()){
				counter[CNT_CAMROT].setVisible(false);
				camDir+=rotDir;
				if(camDir > Direction.LEFT)camDir -= 4;
				else if(camDir < 0)camDir += 4;
			}
			cam.rotCameraDome(_pc._pc._model._pos, rotY);
			break;
			
		case S_PAUSE:
			if(State.canInput){
				int touch = -1;
				if(tm._touch){
					for(int i=SP_RESUME; i<=SP_GO_TITLE; i++){
						_sprite[i].hitCheck(tm._pos);
					}
				}else if(tm._old){
					for(int i=SP_RESUME; i<=SP_GO_TITLE; i++){
						if(_sprite[i].releaseCheck(tm._pos))touch = i;
					}
				}
				if(touch != -1){
					switch(touch){
					case SP_RESUME:
						_state = S_PLAY;
						break;
					case SP_RETRY:
						State.NextState = GV.STATE_PLAY;
						break;
					case SP_GO_STAGESELECT:
						GV.ShowAd();
						State.NextState = GV.STATE_STAGE_SELECT;
						break;
					case SP_GO_TITLE:
						GV.ShowAd();
						State.NextState = GV.STATE_TITLE;
						break;
					}
					if(GV.IsPlaySE)SV.PlaySE(_se[SE_BUTTON]);
				}else if(km.isKeyUp(KeyEvent.KEYCODE_BACK)){
					_state = S_PLAY;
					if(GV.IsPlaySE)SV.PlaySE(_se[SE_BUTTON]);
				}
			}
			break;
			
		case S_CLEAR:
			if(counter[3].incAndCheck()){
				StageData sd = GV.StageData[GV.SelectLevel][GV.SelectIndex-1];
				sd.isCleared = true;
				if(score > sd.scoreRecord)sd.scoreRecord = score;
				if(counter[CNT_WALK].getValue() < sd.walkRecord)sd.walkRecord = counter[CNT_WALK].getValue();
				if(counter[CNT_TIME].getValue() < sd.timeRecord)sd.timeRecord = counter[CNT_TIME].getValue();
				GV.SaveStageResult(sd);
				_text.setPosition(T_SCORE, new Vector2D(450, 250));
				_text.setPosition(T_SCORE_CNT, new Vector2D(600, 250));
				_sprite[SP_TIMER]._pos = new Vector3D(450, 330, 0);
				_text.setPosition(T_TIME_CNT, new Vector2D(600, 342));
				_sprite[SP_WALK]._pos = new Vector3D(450, 410, 0);
				_text.setPosition(T_WALK_CNT, new Vector2D(600, 422));
				NekomanBlockPuzzleActivity act = (NekomanBlockPuzzleActivity)SV.Context;
				act.showToast(act.getString(R.string.play_data_save), 0);
				_state = S_RESULT;
			}
			cam.rotAddCamera(_pc._pc._model._pos, 0.2f, 30, 30);
			break;
			
		case S_RESULT:
			if(State.canInput){
				boolean touch = false;
				if(tm._touch)_sprite[SP_OK].hitCheck(tm._pos);
				else if(tm._old){
					if(_sprite[SP_OK].releaseCheck(tm._pos))touch = true;
				}
				if(touch){
					_state = S_NEXT;
					if(GV.IsPlaySE)SV.PlaySE(_se[SE_BUTTON]);
				}
			}
			cam.rotAddCamera(_pc._pc._model._pos, 0.2f, 30, 30);
			break;
			
		case S_NEXT:
			int touch = -1;
			if(tm._touch){
				if(GV.SelectIndex != GV.STAGE_MAX_NUM)_sprite[SP_GO_NEXT].hitCheck(tm._pos);
				for(int i=SP_RETRY; i<=SP_GO_TITLE; i++){
					_sprite[i].hitCheck(tm._pos);
				}
			}else if(tm._old){
				if(GV.SelectIndex != GV.STAGE_MAX_NUM)if(_sprite[SP_GO_NEXT].releaseCheck(tm._pos))touch = SP_GO_NEXT;
				for(int i=SP_RETRY; i<=SP_GO_TITLE; i++){
					if(_sprite[i].hitCheck(tm._pos))touch = i;
				}
			}
			if(touch != -1){
				switch(touch){
				case SP_GO_NEXT:
					GV.SelectIndex++;
					State.NextState = GV.STATE_PLAY;
					break;
				case SP_RETRY:
					State.NextState = GV.STATE_PLAY;
					break;
				case SP_GO_STAGESELECT:
					GV.ShowAd();
					State.NextState = GV.STATE_STAGE_SELECT;
					break;
				case SP_GO_TITLE:
					GV.ShowAd();
					State.NextState = GV.STATE_TITLE;
					break;
				}
				if(GV.IsPlaySE)SV.PlaySE(_se[SE_BUTTON]);
			}
			cam.rotAddCamera(_pc._pc._model._pos, 0.2f, 30, 30);
			break;
		}
		_cam = cam;
	}

	@Override
	public void draw(GL10 gl) {
		_cubemap.draw(gl);
		_pc.drawFloor(gl);
		_pc.drawModel(gl, GV.FrameSkip);
		if(_pc.drawBlock(gl, _cam._eye)){
			counter[CNT_CLEAR].set(120, false);
			_state = S_CLEAR;
			if(GV.IsPlaySE)SV.PlaySE(_se[SE_APPLAUSE]);
		}
		_text._color = new Vector4D(0, 0, 0, 1);
		_text.draw(gl, T_STAGE);
		switch(_state){
		
		case S_READY:
			_text.draw(gl, T_READY);
			break;
			
		case S_GO:
			_text.draw(gl, T_GO);
			
		case S_PLAY:
			for (int i = SP_ARROW_UP; i <= SP_ARROW_LEFT; i++) {
				_sprite[i].draw(gl, true);
			}
			_sprite[SP_IN].draw(gl, true);
			_sprite[SP_OUT].draw(gl, true);
			_sprite[SP_CAMERA].draw(gl);
			_sprite[SP_TIMER].draw(gl);
			_sprite[SP_WALK].draw(gl);
			
			_text.setSize(gl, 40);
			_text.setText(gl, T_SCORE_CNT, String.format("%06d", score), new Vector2D(620, 80));
			_text.setText(gl, T_WALK_CNT, String.format("%04d", counter[CNT_WALK].getValue()), new Vector2D(20 + 100, 192));
			_text.setText(gl, T_TIME_CNT, String.format("%04d", counter[CNT_TIME].getValue()), new Vector2D(20 + 100, 112));
			_text.draw(gl, T_SCORE);
			_text.draw(gl, T_SCORE_CNT);
			_text.draw(gl, T_WALK_CNT);
			_text.draw(gl, T_TIME_CNT);
			break;
			
		case S_PAUSE:
			_sprite[SP_PAUSE_LOGO].draw(gl);
			_sprite[SP_RESUME].draw(gl, true);
			_sprite[SP_RETRY].draw(gl, true);
			_sprite[SP_GO_STAGESELECT].draw(gl, true);
			_sprite[SP_GO_TITLE].draw(gl, true);
			break;
			
		case S_CLEAR:
			_sprite[SP_CLEAR_LOGO].draw(gl);
			break;
			
		case S_RESULT:
			_sprite[SP_RESULT_LOGO].draw(gl);
			_sprite[SP_TIMER].draw(gl);
			_sprite[SP_WALK].draw(gl);
			_text.setSize(gl, 50);
			_text.setText(gl, T_SCORE_CNT, String.format("%06d", score), new Vector2D(600, 250));
			_text.setText(gl, T_TIME_CNT, String.format("%04d", counter[CNT_TIME].getValue()), new Vector2D(600, 344));
			_text.setText(gl, T_WALK_CNT, String.format("%04d", counter[CNT_WALK].getValue()), new Vector2D(600, 424));
			_text.draw(gl, T_SCORE);
			_text.draw(gl, T_SCORE_CNT);
			_text.draw(gl, T_WALK_CNT);
			_text.draw(gl, T_TIME_CNT);
			_sprite[SP_OK].draw(gl, true);
			break;
			
		case S_NEXT:
			if(GV.SelectIndex == GV.STAGE_MAX_NUM)_sprite[SP_GO_NEXT].draw(gl, new Vector4D(0.5f, 0.5f, 0.5f, 1));
			else _sprite[SP_GO_NEXT].draw(gl, true);
			_sprite[SP_RETRY].draw(gl, true);
			_sprite[SP_GO_STAGESELECT].draw(gl, true);
			_sprite[SP_GO_TITLE].draw(gl, true);
		}
		
	}
}
