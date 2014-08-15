package com.yoship;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.KeyEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.roxiga.hypermotion3d.*;

@SuppressLint("FloatMath")
public final class NekomanBlockPuzzle implements GLSurfaceView.Renderer
{
	private int _nowState = GV.STATE_TITLE;
	//コンテクスト
    private Camera _cam = new Camera();
    private Light _light = new Light();
    public TouchManager _tm = new TouchManager();
    public KeyManager _km = new KeyManager();
    
    private final State [] _state = {
    		State_Title.getInstance(),
    		State_Setting.getInstance(),
            State_Tutorial.getInstance(),
            State_StageSelect.getInstance(),
            State_Play.getInstance(),
            State_Edit.getInstance(),
            State_Charange.getInstance(),
    };
    
    private Mask _mask = new Mask();
    private int _nextState;
    
    public FPSGetter _fps = new FPSGetter(1000);
    private SpriteText _text = new SpriteText();
    private Sprite2D _screen = new Sprite2D();
    
    private long skipNum = 0;
    
    @Override
    // 描画のために毎フレーム呼び出されるイベント
    public void onDrawFrame(GL10 gl)
    {
    	_tm.decideTouch();
    	_km.decideKey();
    	_state[_nowState].proccess(_tm, _km, _cam);
    	_mask.update();
    	
    	switch(GV.DrawingMethod){
    	case GV.DRAW_USUALLY:
    		// 描画用バッファをクリア
	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	        // モデルビュー行列を指定
		    gl.glMatrixMode(GL10.GL_MODELVIEW);
		    // 現在選択されている行列(モデルビュー行列)に、単位行列をセット
		    gl.glLoadIdentity();
		    // カメラ位置をセット
		    GLU.gluLookAt(gl, _cam._eye._x, _cam._eye._y, _cam._eye._z, _cam._look._x, _cam._look._y, _cam._look._z, _cam._up._x, _cam._up._y, _cam._up._z);
	        _light.setPosition(gl);
	     	_state[_nowState].draw(gl);
	    	
	     	if(GV.IsShowFPS){
	     		_text.setText(gl, _fps.toString(), new Vector2D(SV.VirtualScreenSize._x - 140, SV.VirtualScreenSize._y - _text.getSize() - 5));
	     		_text.draw(gl);
	     	}
	    	_mask.drawMask(gl);
	    	break;
    	case GV.DRAW_ONE_FRAME_SKIP:
    		if(_fps.getInterval() < 17){
    	    	// 描画用バッファをクリア
    	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    	        // モデルビュー行列を指定
    		    gl.glMatrixMode(GL10.GL_MODELVIEW);
    		    // 現在選択されている行列(モデルビュー行列)に、単位行列をセット
    		    gl.glLoadIdentity();
    		    // カメラ位置をセット
    		    GLU.gluLookAt(gl, _cam._eye._x, _cam._eye._y, _cam._eye._z, _cam._look._x, _cam._look._y, _cam._look._z, _cam._up._x, _cam._up._y, _cam._up._z);
    	        _light.setPosition(gl);
    	     	_state[_nowState].draw(gl);
    	    	
    	     	if(GV.IsShowFPS){
    	     		_text.setText(gl, _fps.toString(), new Vector2D(SV.VirtualScreenSize._x - 140, SV.VirtualScreenSize._y - _text.getSize() - 5));
    	     		_text.draw(gl);
    	     	}
    	    	_mask.drawMask(gl);
    	    	Bitmap bm = GV.getDrawingCache();
    	    	if(bm != null)_screen.setTexture(gl, bm);
    	    	GV.FrameSkip = 0;
        	}else{
        		GV.FrameSkip++;
        		_screen.drawInPixelCorrect(gl);
        	}
    	    break;
    	case GV.DRAW_ANY_FRAMES_SKIP:
    		if(skipNum == 0){
        		long s = _fps.getInterval();
        		if(s <= 3)skipNum = 0;
        		else skipNum =  (s-3)>>4;
        	}else{
        		skipNum--;
        	}
    	    if(skipNum == 0){
    	    	// 描画用バッファをクリア
    	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    	        // モデルビュー行列を指定
    		    gl.glMatrixMode(GL10.GL_MODELVIEW);
    		    // 現在選択されている行列(モデルビュー行列)に、単位行列をセット
    		    gl.glLoadIdentity();
    		    // カメラ位置をセット
    		    GLU.gluLookAt(gl, _cam._eye._x, _cam._eye._y, _cam._eye._z, _cam._look._x, _cam._look._y, _cam._look._z, _cam._up._x, _cam._up._y, _cam._up._z);
    	        _light.setPosition(gl);
    	     	_state[_nowState].draw(gl);
    	    	
    	     	if(GV.IsShowFPS){
    	     		_text.setText(gl, _fps.toString(), new Vector2D(SV.VirtualScreenSize._x - 140, SV.VirtualScreenSize._y - _text.getSize() - 5));
    	     		_text.draw(gl);
    	     	}
    	    	_mask.drawMask(gl);
    	    	Bitmap bm = GV.getDrawingCache();
    	    	if(bm != null)_screen.setTexture(gl, bm);
    	    	GV.FrameSkip = 0;
        	}else{
        		GV.FrameSkip++;
        		_screen.drawInPixelCorrect(gl);
        	}
    	    break;
    	}
    	
    	int n;
    	if((n = State.CheckNext()) == -2){
    		finish();
    	}else if(n >= 0){
    		_nextState = n;
    		_mask.setMaskTime(20);
    		State.canInput = false;
    	}
     	if(_mask.isJustEndFadeIn()){
     		_state[_nextState].init(gl, SV.Context, "false", _cam);
     		_nowState = _nextState;
     	}
     	if(!_mask.isMasking())State.canInput = true;
     	_fps.update();
    }

    // @Override
    // サーフェイスのサイズ変更時に呼ばれる
    public void onSurfaceChanged(
    		GL10 gl, int width, int height)
    {
    	SV.SetScreenSize(width, height);
    	// ビューポートをサイズに合わせてセットしなおす
        gl.glViewport(0, 0, width, height);
        // アスペクトレート
        float ratio = (float) width / height;
        // 射影行列を選択
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // 現在選択されている行列(射影行列)に、単位行列をセット
        gl.glLoadIdentity();
        /* Perspectiveの設定(視野角度, アスペクト比)、ニア、ファー*/
        GLU.gluPerspective(gl, 45.0f, ratio, 5f, 2000f);
    }

    // @Override
    //サーフェイスが生成または再生成される際に呼ばれる
    public void onSurfaceCreated(
    		GL10 gl, EGLConfig config)
    {
        // ディザを無効化
        gl.glDisable(GL10.GL_DITHER);
        // カラーとテクスチャ座標の補間精度を、最も効率的なものに指定
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
        		GL10.GL_FASTEST);
        // 片面表示を有効に
        gl.glEnable(GL10.GL_CULL_FACE);
        // カリング設定をCCWに
        gl.glFrontFace(GL10.GL_CCW);
        // 深度テストを有効に
        gl.glEnable(GL10.GL_DEPTH_TEST);
    	//テクスチャ機能ON
    	gl.glEnable(GL10.GL_TEXTURE_2D);
    	//背景色
    	gl.glClearColor(0.6f,0.8f,1,1);
    	// 頂点配列を使うことを宣言
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //テクスチャ
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        //テクスチャ読み込み
        Sprite2D m = new Sprite2D();
        m.setTexture(gl, SV.Context.getResources(), R.drawable.sprite_white);
        _mask.setSprite(m);
        _text.setSize(gl, 40);
        _text._color = new Vector4D(0, 1, 0, 1);
        
        _km.addCheckKey(KeyEvent.KEYCODE_BACK);
        
        _light.setColor(gl);
        
        onResume(gl);
        if(!_fps.isAlive())_fps.start();
    }
    
    public void onPause(){
    	SV.SavePref("state", _nowState+"");
    	GV.SaveSetting();
    }
    
    public void onResume(GL10 gl){
    	_nowState = SV.LoadPref_Int("state");
    	_state[_nowState].init(gl, SV.Context, "true", _cam);
    	if(_nowState == GV.STATE_SETTING || _nowState == GV.STATE_TUTORIAL || _nowState == GV.STATE_PLAY)GV.HideAd();
    	else GV.ShowAd();
    	if(GV.DrawingMethod == GV.DRAW_USUALLY)GV.setDrawingCacheEnable(false);
    	else GV.setDrawingCacheEnable(true);
    }
    
    public void onDestroy(){
    }
    
    public void finish(){
    	NekomanBlockPuzzleActivity act = (NekomanBlockPuzzleActivity)SV.Context;
		act.finish();
    }
}
