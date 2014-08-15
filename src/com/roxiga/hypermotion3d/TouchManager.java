package com.roxiga.hypermotion3d;

public class TouchManager {
	
	private int _undecideTouch = -1;
	public boolean _touch = false;
	public boolean _old = false;
	private Vector2D _undecidePos = new Vector2D();
	public Vector2D _pos = new Vector2D();
	public Vector2D _oldPos = new Vector2D();
	
	public void actionDown(float x, float y)
	{
		_undecidePos = new Vector2D(x*SV.VirtualRatio._x, y*SV.VirtualRatio._y);
		_undecideTouch = 0;
	}
	
	public void actionMove(float x, float y)
	{
		_undecidePos = new Vector2D(x*SV.VirtualRatio._x, y*SV.VirtualRatio._y);
		_undecideTouch = 1;
	}

	public void actionUp(float x, float y)
	{
		_undecidePos = new Vector2D(x*SV.VirtualRatio._x, y*SV.VirtualRatio._y);
		_undecideTouch = 2;
 	}
	
	public void decideTouch()
	{
		_old = _touch;
		_oldPos = _pos;
		if(_undecideTouch != -1){
			if(_undecideTouch != 2)_touch = true;
			else _touch = false;
			_undecideTouch = -1;
			_pos = _undecidePos;
		}
	}
}
