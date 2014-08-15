package com.roxiga.hypermotion3d;

import javax.microedition.khronos.opengles.GL10;


public class PlayerController {
	
	public class Direction{
		public static final int 
		FRONT = 0,
		RIGHT = 1,
		BACK = 2,
		LEFT = 3;
	}
	
	public Model3D _model;
	
	public boolean _isMove = false, _oldMove = false;
	public int _dir = Direction.FRONT;
	public int time = -1;
	
	public Vector2I _relPos = new Vector2I();
	
	public PlayerController(){}
	
	public PlayerController(Model3D model){
		this(model, new Vector2I());
	}
	
	public PlayerController(Model3D model, Vector2I pos){
		setRelativePos(pos);
		setModel(model);
	}
	
	public void setModel(Model3D model){
		this._model = model;
		_model._pos = new Vector3D(_relPos._x*10.0f, 10.0f, _relPos._y*10.0f);
		_model.setAnimation(1);
	}
	
	public void setRelativePos(Vector2I pos){
		_relPos = pos;
	}
	
	public boolean update(int dir, boolean canMove){
		_oldMove = _isMove;
		if(time == -1){
			if(dir == -1){
				return false;
			}
			_dir = dir;
			_model._rotate._y = 180.0f - dir*90f;
			if(canMove){
				_isMove = true;
				_model.setAnimation(2);
				time++;
			}
		}
		if(time != -1){
			switch(_dir){
			case Direction.FRONT:
				_model._pos = new Vector3D(_relPos._x* 10.0f, 10.0f, (_relPos._y - time/30.0f) * 10.0f);
				break;
			case Direction.RIGHT:
				_model._pos = new Vector3D((_relPos._x + time/30.0f)* 10.0f, 10.0f, _relPos._y * 10.0f);
				break;
			case Direction.BACK:
				_model._pos = new Vector3D(_relPos._x* 10.0f, 10.0f, (_relPos._y + time/30.0f) * 10.0f);
				break;
			case Direction.LEFT:
				_model._pos = new Vector3D((_relPos._x - time/30.0f)* 10.0f, 10.0f, _relPos._y * 10.0f);
			}
			time++;
			if(time > 30){
				switch(_dir){
				case Direction.FRONT:
					_relPos = new Vector2I(_relPos._x, _relPos._y-1);
					break;
				case Direction.RIGHT:
					_relPos = new Vector2I(_relPos._x+1, _relPos._y);
					break;
				case Direction.BACK:
					_relPos = new Vector2I(_relPos._x, _relPos._y+1);
					break;
				case Direction.LEFT:
					_relPos = new Vector2I(_relPos._x-1, _relPos._y);
				}
				_isMove = false;
				_model.setAnimation(1);
				time = -1;
				return true;
			}
		}
		return false;
	}
	
	public void drawModel(GL10 gl, int skipNum){
		if(_model instanceof BoneModel3D){
			BoneModel3D bm = (BoneModel3D)_model;
			bm.draw(gl, skipNum);
		}else _model.draw(gl);
	}
}
