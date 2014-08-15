package com.yoship;

import java.util.BitSet;
import java.util.Iterator;
import java.util.TreeMap;

import javax.microedition.khronos.opengles.GL10;

import android.media.MediaPlayer;

import com.roxiga.hypermotion3d.Model3D;
import com.roxiga.hypermotion3d.PlayerController;
import com.roxiga.hypermotion3d.PlayerController.Direction;
import com.roxiga.hypermotion3d.SV;
import com.roxiga.hypermotion3d.Vector2I;
import com.roxiga.hypermotion3d.Vector3D;

public class PuzzleController {
	
	public class StageLoad {
		public static final int KND_NOTHING = 0;
		public static final int KND_FLOOR = 1;
		public static final int KND_GREEN = 2;
		public static final int KND_RED = 3;
		public static final int KND_BLUE = 4;
		public static final int KND_YELLOW = 5;
		public static final int KND_START = 6;
		public static final int KND_DIS_GREEN = 7;
		public static final int KND_DIS_RED = 8;
		public static final int KND_DIS_BLUE = 9;
		public static final int KND_DIS_YELLOW = 10;
	}
	
	public static final int BLOCK_GREEN = 0;
	public static final int BLOCK_RED = 1;
	public static final int BLOCK_BLUE = 2;
	public static final int BLOCK_YELLOW = 3;
	public static final int BLOCK_NUM = 4;
	
	public static final int FLAG_MODEL_FLOOR = 0;
	public static final int FLAG_MODEL_BLOCK = 1;
	public static final int FLAG_MODEL_PLAYER = 2;
	public static final int FLAG_STAGE = 3;
	public static final int FLAG_END = 4;
	public static final int FLAG_NUM = 5;
	
	public static final int DISAPPEAR_TIME = 60;
	
	private byte _floor[][];
	private byte _data[][];
	private byte _disTime[][];
	public Model3D _floorModel;
	public Model3D _blockModel[];
	public PlayerController _pc;
	
	private static final int 
	SE_MODEL_MOVE = 0,
	SE_BLOCK_MOVE = 1,
	SE_BLOCK_DIS = 2;
	private MediaPlayer _se[] = new MediaPlayer[3];
	
	private boolean _moveBlock;
	private Vector2I _moveBlockPos;
	private int _moveBlockDir;
	
	BitSet _flag = new BitSet(FLAG_NUM);
	
	public PuzzleController(){}
	
	public PuzzleController(byte data[][][], Model3D player){
		init(data, player);
	}
	
	public void init(byte data[][][], Model3D player){
		_flag.set(0, FLAG_NUM, false);
		initStage(data);
		Vector2I pos = getData(StageLoad.KND_START);
		_data[pos._y][pos._x] = StageLoad.KND_NOTHING;
		initPlayer(player, pos);
	}
	
	public void initStage(byte data[][][]){
		_floor = new byte[data[0].length][data[0][0].length];
		_data = new byte[getSizeZ()][getSizeX()];
		_disTime = new byte[getSizeZ()][getSizeX()];
		for (int i = 0; i < getSizeZ(); i++) {
			for (int j = 0; j < getSizeX(); j++) {
				_floor[i][j] = data[0][i][j];
				_data[i][j] = data[1][i][j];
				_disTime[i][j] = 0;
			}
		}
		
		_flag.set(FLAG_STAGE);
	}
	
	public void initPlayer(Model3D player, Vector2I relPos){
		_pc = new PlayerController(player, relPos);
		_flag.set(FLAG_MODEL_PLAYER);
	}
	
	public void setModelMoveSE(int id){
		if(_se[SE_MODEL_MOVE] != null)_se[SE_MODEL_MOVE].release();
		_se[SE_MODEL_MOVE] = MediaPlayer.create(SV.Context, id);
	}
	
	public void setBlockDisSE(int id){
		if(_se[SE_BLOCK_DIS] != null)_se[SE_BLOCK_DIS].release();
		_se[SE_BLOCK_DIS] = MediaPlayer.create(SV.Context, id);
	}
	
	private Vector2I getData(int type){
		for (int i = 0; i < _data.length; i++) {
			for (int j = 0; j < _data[0].length; j++) {
				if(_data[i][j] == type)return new Vector2I(j, i);
			}
		}
		return new Vector2I();
	}
	
	public void setBlockModels(Model3D models[]){
		if(models.length != 4)return;
		_blockModel = models;
		_flag.set(FLAG_MODEL_BLOCK);
	}
	
	public void setBlockMoveSE(int id){
		if(_se[SE_BLOCK_MOVE] != null)_se[SE_BLOCK_MOVE].release();
		_se[SE_BLOCK_MOVE] = MediaPlayer.create(SV.Context, id);
	}
	
	public void setFloorModel(Model3D model){
		_floorModel = model;
		_flag.set(FLAG_MODEL_FLOOR);
	}
	
	public void drawFloor(GL10 gl){
		if(!(_flag.get(FLAG_STAGE) && _flag.get(FLAG_MODEL_FLOOR)))return;
		for (int i = 0; i < getSizeZ(); i++) {
			for (int j = 0; j < getSizeX(); j++) {
				if(_floor[i][j] == StageLoad.KND_FLOOR){
					_floorModel._pos = new Vector3D(j * 10.0f, 0, i*10.0f);
					_floorModel.draw(gl);
				}
			}
		}
	}
	
	public boolean drawBlock(GL10 gl, Vector3D eye){
		if(!(_flag.get(FLAG_STAGE) && _flag.get(FLAG_MODEL_BLOCK)))return false;
		boolean endCheck = false;
		class blockPos{
			public int knd;
			public Vector2I pos;
		}
		TreeMap<Float, blockPos> tm = new TreeMap<Float, blockPos>();
		for (int i = 0; i < getSizeZ(); i++) {
			for (int j = 0; j < getSizeX(); j++) {
				int knd = -1;
				switch(_data[i][j]){
				case StageLoad.KND_NOTHING:
				case StageLoad.KND_FLOOR:
					break;
				case StageLoad.KND_GREEN:
				case StageLoad.KND_DIS_GREEN:
					knd = BLOCK_GREEN;
					break;
				case StageLoad.KND_RED:
				case StageLoad.KND_DIS_RED:
					knd = BLOCK_RED;
					break;
				case StageLoad.KND_BLUE:
				case StageLoad.KND_DIS_BLUE:
					knd = BLOCK_BLUE;
					break;
				case StageLoad.KND_YELLOW:
				case StageLoad.KND_DIS_YELLOW:
					knd = BLOCK_YELLOW;
					break;
				}
				if(knd == -1)continue;
				blockPos bp = new blockPos();
				float l = 0;
				if(_moveBlock && _moveBlockPos._x == j && _moveBlockPos._y == i){
					switch(_moveBlockDir){
					case Direction.FRONT:
						l = -eye.subtract(new Vector3D(j*10.0f, 10.0f, (i - (float)_pc.time/30.0f)*10.0f)).length();
						break;
					case Direction.RIGHT:
						l = -eye.subtract(new Vector3D((j + (float)_pc.time/30.0f)*10.0f, 10.0f, i*10.0f)).length();
						break;
					case Direction.BACK:
						l = -eye.subtract(new Vector3D(j*10.0f, 10.0f, (i + (float)_pc.time/30.0f)*10.0f)).length();
						break;
					case Direction.LEFT:
						l = -eye.subtract(new Vector3D((j - (float)_pc.time/30.0f)*10.0f, 10.0f, i*10.0f)).length();
						break;
					}
				}else l = -eye.subtract(new Vector3D(j*10.0f, 10.0f, i*10.0f)).length();
				bp.knd = knd;
				bp.pos = new Vector2I(j, i);
				if(tm.containsKey(l)){
					tm.put(l+0.0001f, bp);
				}else tm.put(l, bp);
			}
		}
		Iterator<Float> it = tm.keySet().iterator();
		while(it.hasNext()){
			blockPos o = tm.get(it.next());
			if(_moveBlock && _moveBlockPos._x == o.pos._x && _moveBlockPos._y == o.pos._y){
				switch(_moveBlockDir){
				case Direction.FRONT:
					_blockModel[o.knd]._pos = new Vector3D(o.pos._x*10.0f, 10.0f, (o.pos._y - (float)_pc.time/30.0f)*10.0f);
					break;
				case Direction.RIGHT:
					_blockModel[o.knd]._pos = new Vector3D((o.pos._x + (float)_pc.time/30.0f)*10.0f, 10.0f, o.pos._y*10.0f);
					break;
				case Direction.BACK:
					_blockModel[o.knd]._pos = new Vector3D(o.pos._x*10.0f, 10.0f, (o.pos._y + (float)_pc.time/30.0f)*10.0f);
					break;
				case Direction.LEFT:
					_blockModel[o.knd]._pos = new Vector3D((o.pos._x - (float)_pc.time/30.0f)*10.0f, 10.0f, o.pos._y*10.0f);
					break;
				}
			}else _blockModel[o.knd]._pos = new Vector3D(o.pos._x*10.0f, 10.0f, o.pos._y*10.0f);
			
			if(_disTime[o.pos._y][o.pos._x] > 0){
				int t = _disTime[o.pos._y][o.pos._x];
				int ti = DISAPPEAR_TIME - t;
				_blockModel[o.knd]._rotate._y = ti * 5;
				_blockModel[o.knd]._scale = new Vector3D((float)t/ DISAPPEAR_TIME, (float)t/ DISAPPEAR_TIME, (float)t/ DISAPPEAR_TIME);
				_blockModel[o.knd].draw(gl);
				_blockModel[o.knd]._rotate._y = 0;
				_blockModel[o.knd]._scale = new Vector3D(1, 1, 1);
				_disTime[o.pos._y][o.pos._x] -= GV.FrameSkip+1;
				if(_disTime[o.pos._y][o.pos._x] < 0)_disTime[o.pos._y][o.pos._x] = 0;
				if(_disTime[o.pos._y][o.pos._x] == 0){
					_data[o.pos._y][o.pos._x] = StageLoad.KND_NOTHING;
					endCheck = true;
				}
			}else _blockModel[o.knd].draw(gl);
		}
		if(endCheck){
			boolean end = true;
			for (int i = 0; i < getSizeZ(); i++) {
				for (int j = 0; j < getSizeX(); j++) {
					if(_data[i][j] >= StageLoad.KND_GREEN && _data[i][j] <= StageLoad.KND_YELLOW)end = false;
				}
			}
			if(end){
				_flag.set(FLAG_END);
				_pc._model.setAnimation(3);
			}
			return end;
		}
		return false;
	}
	
	public boolean drawBlock(GL10 gl){
		if(!(_flag.get(FLAG_STAGE) && _flag.get(FLAG_MODEL_BLOCK)))return false;
		boolean endCheck = false;
		for (int i = 0; i < getSizeZ(); i++) {
			for (int j = 0; j < getSizeX(); j++) {
				int knd = -1;
				switch(_data[i][j]){
				case StageLoad.KND_NOTHING:
				case StageLoad.KND_FLOOR:
					break;
				case StageLoad.KND_GREEN:
					knd = BLOCK_GREEN;
					break;
				case StageLoad.KND_RED:
					knd = BLOCK_RED;
					break;
				case StageLoad.KND_BLUE:
					knd = BLOCK_BLUE;
					break;
				case StageLoad.KND_YELLOW:
					knd = BLOCK_YELLOW;
					break;
				}
				if(knd == -1)continue;
				if(_moveBlock && _moveBlockPos._x == j && _moveBlockPos._y == i){
					switch(_moveBlockDir){
					case Direction.FRONT:
						_blockModel[knd]._pos = new Vector3D(j*10.0f, 10.0f, (i - (float)_pc.time/30.0f)*10.0f);
						break;
					case Direction.RIGHT:
						_blockModel[knd]._pos = new Vector3D((j + (float)_pc.time/30.0f)*10.0f, 10.0f, i*10.0f);
						break;
					case Direction.BACK:
						_blockModel[knd]._pos = new Vector3D(j*10.0f, 10.0f, (i + (float)_pc.time/30.0f)*10.0f);
						break;
					case Direction.LEFT:
						_blockModel[knd]._pos = new Vector3D((j - (float)_pc.time/30.0f)*10.0f, 10.0f, i*10.0f);
						break;
					}
				}else _blockModel[knd]._pos = new Vector3D(j*10.0f, 10.0f, i*10.0f);
				
				if(_disTime[i][j] > 0){
					int t = _disTime[i][j];
					int ti = DISAPPEAR_TIME - t;
					_blockModel[knd]._rotate._y = ti * 5;
					_blockModel[knd]._scale = new Vector3D((float)t/ DISAPPEAR_TIME, (float)t/ DISAPPEAR_TIME, (float)t/ DISAPPEAR_TIME);
					_blockModel[knd].draw(gl);
					_blockModel[knd]._rotate._y = 0;
					_blockModel[knd]._scale = new Vector3D(1, 1, 1);
					_disTime[i][j] -= GV.FrameSkip+1;
					if(_disTime[i][j] < 0)_disTime[i][j] = 0;
					if(_disTime[i][j] == 0){
						_data[i][j] = StageLoad.KND_NOTHING;
						endCheck = true;
					}
				}else _blockModel[knd].draw(gl);
			}
		}
		if(endCheck){
			boolean end = true;
			for (int i = 0; i < getSizeZ(); i++) {
				for (int j = 0; j < getSizeX(); j++) {
					if(_data[i][j] >= StageLoad.KND_GREEN && _data[i][j] <= StageLoad.KND_YELLOW)end = false;
				}
			}
			if(end){
				_flag.set(FLAG_END);
				_pc._model.setAnimation(3);
			}
			return end;
		}
		return false;
	}
	
	
	public void drawModel(GL10 gl, int skipNum){
		if(!_flag.get(FLAG_MODEL_PLAYER))return;
		_pc.drawModel(gl, skipNum);
	}
	
	public Vector2I playerUpdate(int dir){
		if(!(_flag.get(FLAG_STAGE) && _flag.get(FLAG_MODEL_PLAYER) && !_flag.get(FLAG_END)))return new Vector2I();
		boolean canMove = true;
		Vector2I dis = new Vector2I();
		if(dir >= Direction.FRONT && !_pc._isMove){
			switch(dir){
			case Direction.FRONT:
				if(_pc._relPos._y-1 >= 0){
					boolean y2 = (_pc._relPos._y-2 >= 0);
					if(_floor[_pc._relPos._y-1][_pc._relPos._x] == 0)canMove = false;
					else if(_data[_pc._relPos._y-1][_pc._relPos._x] >= StageLoad.KND_GREEN && _data[_pc._relPos._y-1][_pc._relPos._x] < StageLoad.KND_START){
						if(y2){
							if(_data[_pc._relPos._y-2][_pc._relPos._x] == 0 && _floor[_pc._relPos._y-2][_pc._relPos._x] == StageLoad.KND_FLOOR){
								_moveBlockPos = new Vector2I(_pc._relPos._x, _pc._relPos._y-1);
								_moveBlockDir = Direction.FRONT;
								_moveBlock = true;
								if(GV.IsPlaySE)SV.PlaySE(_se[SE_BLOCK_MOVE]);
							}else canMove = false;
						}else canMove = false;
					}
				}else canMove = false;
				break;
			case Direction.RIGHT:
				if(_pc._relPos._x+1 < getSizeX()){
					boolean x2 = (_pc._relPos._x+2 < getSizeX());
					if(_floor[_pc._relPos._y][_pc._relPos._x+1] == 0)canMove = false;
					else if(_data[_pc._relPos._y][_pc._relPos._x+1] >= StageLoad.KND_GREEN && _data[_pc._relPos._y][_pc._relPos._x+1] < StageLoad.KND_START){
						if(x2){
							if(_data[_pc._relPos._y][_pc._relPos._x+2] == 0 && _floor[_pc._relPos._y][_pc._relPos._x+2] == StageLoad.KND_FLOOR){
								_moveBlockPos = new Vector2I(_pc._relPos._x+1, _pc._relPos._y);
								_moveBlockDir = Direction.RIGHT;
								_moveBlock = true;
								if(GV.IsPlaySE)SV.PlaySE(_se[SE_BLOCK_MOVE]);
							}else canMove = false;
						}else canMove = false;
					}
				}else canMove = false;
				break;
			case Direction.BACK:
				if(_pc._relPos._y+1 < getSizeZ()){
					boolean y2 = (_pc._relPos._y+2 < getSizeZ());
					if(_floor[_pc._relPos._y+1][_pc._relPos._x] == 0)canMove = false;
					else if(_data[_pc._relPos._y+1][_pc._relPos._x] >= StageLoad.KND_GREEN && _data[_pc._relPos._y+1][_pc._relPos._x] < StageLoad.KND_START){
						if(y2){
							if(_data[_pc._relPos._y+2][_pc._relPos._x] == 0 && _floor[_pc._relPos._y+2][_pc._relPos._x] == StageLoad.KND_FLOOR){
								_moveBlockPos = new Vector2I(_pc._relPos._x, _pc._relPos._y+1);
								_moveBlockDir = Direction.BACK;
								_moveBlock = true;
								if(GV.IsPlaySE)SV.PlaySE(_se[SE_BLOCK_MOVE]);
							}else canMove = false;
						}else canMove = false;
					}
				}else canMove = false;
				break;
			case Direction.LEFT:
				if(_pc._relPos._x-1 >= 0){
					boolean x2 = (_pc._relPos._x-2 >= 0);
					if(_floor[_pc._relPos._y][_pc._relPos._x-1] == 0)canMove = false;
					else if(_data[_pc._relPos._y][_pc._relPos._x-1] >= StageLoad.KND_GREEN &&  _data[_pc._relPos._y][_pc._relPos._x-1] < StageLoad.KND_START){
						if(x2){
							if(_data[_pc._relPos._y][_pc._relPos._x-2] == 0 && _floor[_pc._relPos._y][_pc._relPos._x-2] == StageLoad.KND_FLOOR){
								_moveBlockPos = new Vector2I(_pc._relPos._x-1, _pc._relPos._y);
								_moveBlockDir = Direction.LEFT;
								_moveBlock = true;
								if(GV.IsPlaySE)SV.PlaySE(_se[SE_BLOCK_MOVE]);
							}else canMove = false;
						}else canMove = false;
					}
				}else canMove = false;
				break;
			}
		}
		if(!_pc._isMove && canMove && dir != -1){
			if(GV.IsPlaySE)SV.PlaySE(_se[SE_MODEL_MOVE]);
		}
		if(_pc.update(dir, canMove)){
			if(_moveBlock){
				switch(_moveBlockDir){
				case Direction.FRONT:
					_data[_moveBlockPos._y-1][_moveBlockPos._x] = _data[_moveBlockPos._y][_moveBlockPos._x];
					_data[_moveBlockPos._y][_moveBlockPos._x] = StageLoad.KND_NOTHING;
					dis = checkBlockDisappears(_moveBlockPos._x, _moveBlockPos._y-1);
					break;
				case Direction.RIGHT:
					_data[_moveBlockPos._y][_moveBlockPos._x+1] = _data[_moveBlockPos._y][_moveBlockPos._x];
					_data[_moveBlockPos._y][_moveBlockPos._x] = StageLoad.KND_NOTHING;
					dis = checkBlockDisappears(_moveBlockPos._x+1, _moveBlockPos._y);
					break;
				case Direction.BACK:
					_data[_moveBlockPos._y+1][_moveBlockPos._x] = _data[_moveBlockPos._y][_moveBlockPos._x];
					_data[_moveBlockPos._y][_moveBlockPos._x] = StageLoad.KND_NOTHING;
					dis = checkBlockDisappears(_moveBlockPos._x, _moveBlockPos._y+1);
					break;
				case Direction.LEFT:
					_data[_moveBlockPos._y][_moveBlockPos._x-1] = _data[_moveBlockPos._y][_moveBlockPos._x];
					_data[_moveBlockPos._y][_moveBlockPos._x] = StageLoad.KND_NOTHING;
					dis = checkBlockDisappears(_moveBlockPos._x-1, _moveBlockPos._y);
					break;
				}
				_moveBlock = false;
				if(dis._x != 0){
					if(GV.IsPlaySE)SV.PlaySE(_se[SE_BLOCK_DIS]);
				}
			}
		}
		return dis;
	}
	
	private Vector2I checkBlockDisappears(int x, int y){
		int disNum = _data[y][x], knd = _data[y][x];
		int num = 1;
		byte checked [][] = new byte[getSizeZ()][getSizeX()];
		for (int i = 0; i < checked.length; i++) {
			for (int j = 0; j < checked[0].length; j++) {
				if(_data[i][j] == knd)checked[i][j] = 1;
				else checked[i][j] = 0;
			}
		}
		checked[y][x] = 2;
		num += findDisappears(checked, knd, x, y);
		if(num >= disNum){
			for (int i = 0; i < checked.length; i++) {
				for (int j = 0; j < checked[0].length; j++) {
					if(checked[i][j] == 2){
						_disTime[i][j] = DISAPPEAR_TIME;
						_data[i][j] += 5;
					}
				}
			}
			return new Vector2I(knd, num);
		}
		return new Vector2I();
	}
	
	private int findDisappears(byte checked[][], int knd, int x, int y){
		int num = 0;
		
		if(y != 0){
			if(_data[y-1][x] == knd){
				if(checked[y-1][x] == 1){
					checked[y-1][x] = 2;
					num += findDisappears(checked, knd, x, y-1)+1;
				}
			}
		}
		if(x != getSizeX()-1){
			if(_data[y][x+1] == knd){
				if(checked[y][x+1] == 1){
					checked[y][x+1] = 2;
					num += findDisappears(checked, knd, x+1, y)+1;
				}
			}
		}
		
		if(y != getSizeZ()-1){
			if(_data[y+1][x] == knd){
				if(checked[y+1][x] == 1){
					checked[y+1][x] = 2;
					num += findDisappears(checked, knd, x, y+1)+1;
				}
			}
		}
		if(x != 0){
			if(_data[y][x-1] == knd){
				if(checked[y][x-1] == 1){
					checked[y][x-1] = 2;
					num += findDisappears(checked, knd, x-1, y)+1;
				}
			}
		}
		return num;
	}
	
	public int getSizeX(){
		if(_floor == null)return 0;
		return this._floor[0].length;
	}
	
	public int getSizeZ(){
		if(_floor == null)return 0;
		return this._floor.length;
	}
	
}
