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
import com.roxiga.hypermotion3d.Vector3D;
import com.roxiga.hypermotion3d.PlayerController.Direction;
import com.roxiga.hypermotion3d.Vector4D;
import com.roxiga.models.block;
import com.roxiga.models.cubemap;
import com.roxiga.models.floor;
import com.roxiga.models.nekoman;

public final class State_Tutorial extends State {
	
	private static State_Tutorial instance = new State_Tutorial();
	private State_Tutorial(){}
	
	public static State_Tutorial getInstance(){
		return instance;
	}
	
	private static final int
	SP_ARROW_UP = 0,
	SP_ARROW_RIGHT = 1,
	SP_ARROW_DOWN = 2,
	SP_ARROW_LEFT = 3,
	SP_CAMERA_LOGO = 4,
	SP_CAMERA_IN = 5,
	SP_CAMERA_OUT = 6,
	SP_LOGO = 7,
	SP_RETURN = 8,
	SPRITE_MAX_NUM = 9;
	
	private Sprite2D _sprite[] = new Sprite2D[SPRITE_MAX_NUM];
	private SpriteText _text;
	private MediaPlayer _se;
	private cubemap _cubemap = new cubemap();
	
	private PuzzleController _pc;
	
	private int camDir;
	private int rotDir;
	private Vector2D rotPibot = new Vector2D();
	private Camera _cam = new Camera();
	
	private static final int
	CNT_CAMROT = 0,
	CNT_ROTPIBOT = 1;
	private Counter counter [] = new Counter[2];

	@Override
	public void init(GL10 gl, Context context, String str, Camera cam) {
		boolean resume = Boolean.parseBoolean(str);
		
		//puzzlecontroller
		{
			if(!resume || _pc == null){
				
				nekoman player = new nekoman();
				player._scale = new Vector3D(1.3f, 1.3f, 1.3f);
				_pc = new PuzzleController(GV.StageData_Tutorial.data, player);
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
			_pc.setModelMoveSE(R.raw.se_jump);
			_pc.setBlockMoveSE(R.raw.se_block);
			_pc.setBlockDisSE(R.raw.se_dis);
		}
		
		//sprite
		{
			final int drawable [] = {
					R.drawable.sprite_up,
					R.drawable.sprite_right,
					R.drawable.sprite_down,
					R.drawable.sprite_left,
					R.drawable.sprite_camera,
					R.drawable.sprite_plus,
					R.drawable.sprite_minus,
					R.drawable.sprite_logo_howtoplay,
					R.drawable.sprite_return,
			};
			
			for (int i = 0; i < drawable.length; i++) {
				if(_sprite[i] != null)_sprite[i].releaseTexture(gl);
				_sprite[i] = new Sprite2D();
				_sprite[i].setTexture(gl, context.getResources(), drawable[i]);
			}
			
			final Vector3D pos [] = {
					new Vector3D(10 + 140*1, 300 + 140*0, 0),
					new Vector3D(10 + 140*2, 300 + 140*1, 0),
					new Vector3D(10 + 140*1, 300 + 140*2, 0),
					new Vector3D(10 + 140*0, 300 + 140*1, 0),
					new Vector3D(SV.VirtualScreenSize._x - ((_sprite[SP_CAMERA_LOGO]._width+16)*3), 20, 0),
					new Vector3D(SV.VirtualScreenSize._x - ((_sprite[SP_CAMERA_IN]._width+16)*2), 20, 0),
					new Vector3D(SV.VirtualScreenSize._x - ((_sprite[SP_CAMERA_OUT]._width+16)*1), 20, 0),
					new Vector3D(40, 10, 0),
					new Vector3D((SV.VirtualScreenSize._x - _sprite[SP_RETURN]._width)/2, SV.VirtualScreenSize._y - _sprite[SP_RETURN]._height - 20, 0),
			};
			
			for (int i = 0; i < pos.length; i++) {
				_sprite[i]._pos = pos[i];
			}
		}
		
		//text
		{
			if(_text == null){
				_text = new SpriteText();
				
				final int text [] = {
						R.string.howtoplay_target,
						R.string.howtoplay_player_move,
						R.string.howtoplay_camera_height,
						R.string.howtoplay_camera_rot,
						R.string.howtoplay_block_dis,
						R.string.howtoplay_block_dis2,
						R.string.howtoplay_level,
						R.string.houtoplay_end,
				};
				_text.setSize(gl, 24);
				for (int i = 0; i < text.length; i++) {
					_text.setText(gl, i, context.getString(text[i]), new Vector2D(10, 120 + 30*i));
				}
			}else{
				_text.remake(gl);
			}
			_text._color = new Vector4D(0, 0, 0, 1);
		}
		
		//se
		{
			if(_se != null)_se.release();
			_se = MediaPlayer.create(context, R.raw.se_switch);
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
		
		if(!resume){
			camDir = 0;
			cam.setDist(60);
			cam.setRotXZ(30);
		}
		if(!resume || counter[CNT_CAMROT] == null){
			counter[CNT_CAMROT] = new Counter();
		}
		if(!resume || counter[CNT_ROTPIBOT] == null){
			counter[CNT_ROTPIBOT] = new Counter();
		}
	}

	@Override
	public void proccess(TouchManager tm, KeyManager km, Camera cam) {
		int dir = -1;
		float rotY = -90*camDir;
		
		if(State.canInput){
			int touch = -1;
			if(tm._touch){
				for (int i = 0; i < 4; i++) {
					if(_sprite[i].hitCheck(tm._pos))touch = i;
				}
				if(_sprite[SP_CAMERA_IN].hitCheck(tm._pos))touch = SP_CAMERA_IN;
				if(_sprite[SP_CAMERA_OUT].hitCheck(tm._pos))touch = SP_CAMERA_OUT;
				_sprite[SP_RETURN].hitCheck(tm._pos);
			}else if(tm._old){
				for (int i = 0; i < 4; i++) {
					_sprite[i].releaseCheck(tm._pos);
				}
				if(_sprite[SP_RETURN].releaseCheck(tm._pos))touch = SP_RETURN;
				_sprite[SP_CAMERA_IN].releaseCheck(tm._pos);
				_sprite[SP_CAMERA_OUT].releaseCheck(tm._pos);
			}
			
			if(touch >= SP_ARROW_UP && touch <= SP_ARROW_LEFT){
				if(!counter[CNT_CAMROT].isVisible()){
					dir = camDir + touch;
					if(dir > Direction.LEFT)dir -= 4;
				}
			}else if(touch == SP_CAMERA_IN){
				cam.addDist(-0.5f, 100f, 30f);
			}else if(touch == SP_CAMERA_OUT){
				cam.addDist(0.5f, 100f, 30f);
			}else if(km.isKeyUp(KeyEvent.KEYCODE_BACK) || touch == SP_RETURN){
				NextState = GV.STATE_TITLE;
				GV.ShowAd();
				if(GV.IsPlaySE)SV.PlaySE(_se);
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
		_pc.playerUpdate(dir);
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
		_cam = cam;
	}

	@Override
	public void draw(GL10 gl) {
		_cubemap.draw(gl);
		_pc.drawFloor(gl);
		_pc.drawModel(gl, GV.FrameSkip);
		_pc.drawBlock(gl, _cam._eye);
		for (int i = 0; i < 4; i++) {
			_sprite[i].draw(gl, true);
		}
		_sprite[SP_CAMERA_IN].draw(gl, true);
		_sprite[SP_CAMERA_OUT].draw(gl, true);
		_sprite[SP_RETURN].draw(gl, true);
		_sprite[SP_CAMERA_LOGO].draw(gl);
		_sprite[SP_LOGO].draw(gl);
		for (int i = 0; i < _text.getLength(); i++) {
			_text._color = new Vector4D(1,1,1,1);
			_text.draw(gl, i, _text.getPosition(i).add(new Vector2D(2,2)));
		}
		_text._color = new Vector4D(0,0,0,1);
		_text.draw(gl);
	}
}
