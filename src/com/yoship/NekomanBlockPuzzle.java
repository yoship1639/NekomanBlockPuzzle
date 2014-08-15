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
	//�R���e�N�X�g
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
    // �`��̂��߂ɖ��t���[���Ăяo�����C�x���g
    public void onDrawFrame(GL10 gl)
    {
    	_tm.decideTouch();
    	_km.decideKey();
    	_state[_nowState].proccess(_tm, _km, _cam);
    	_mask.update();
    	
    	switch(GV.DrawingMethod){
    	case GV.DRAW_USUALLY:
    		// �`��p�o�b�t�@���N���A
	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	        // ���f���r���[�s����w��
		    gl.glMatrixMode(GL10.GL_MODELVIEW);
		    // ���ݑI������Ă���s��(���f���r���[�s��)�ɁA�P�ʍs����Z�b�g
		    gl.glLoadIdentity();
		    // �J�����ʒu���Z�b�g
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
    	    	// �`��p�o�b�t�@���N���A
    	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    	        // ���f���r���[�s����w��
    		    gl.glMatrixMode(GL10.GL_MODELVIEW);
    		    // ���ݑI������Ă���s��(���f���r���[�s��)�ɁA�P�ʍs����Z�b�g
    		    gl.glLoadIdentity();
    		    // �J�����ʒu���Z�b�g
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
    	    	// �`��p�o�b�t�@���N���A
    	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    	        // ���f���r���[�s����w��
    		    gl.glMatrixMode(GL10.GL_MODELVIEW);
    		    // ���ݑI������Ă���s��(���f���r���[�s��)�ɁA�P�ʍs����Z�b�g
    		    gl.glLoadIdentity();
    		    // �J�����ʒu���Z�b�g
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
    // �T�[�t�F�C�X�̃T�C�Y�ύX���ɌĂ΂��
    public void onSurfaceChanged(
    		GL10 gl, int width, int height)
    {
    	SV.SetScreenSize(width, height);
    	// �r���[�|�[�g���T�C�Y�ɍ��킹�ăZ�b�g���Ȃ���
        gl.glViewport(0, 0, width, height);
        // �A�X�y�N�g���[�g
        float ratio = (float) width / height;
        // �ˉe�s���I��
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // ���ݑI������Ă���s��(�ˉe�s��)�ɁA�P�ʍs����Z�b�g
        gl.glLoadIdentity();
        /* Perspective�̐ݒ�(����p�x, �A�X�y�N�g��)�A�j�A�A�t�@�[*/
        GLU.gluPerspective(gl, 45.0f, ratio, 5f, 2000f);
    }

    // @Override
    //�T�[�t�F�C�X�������܂��͍Đ��������ۂɌĂ΂��
    public void onSurfaceCreated(
    		GL10 gl, EGLConfig config)
    {
        // �f�B�U�𖳌���
        gl.glDisable(GL10.GL_DITHER);
        // �J���[�ƃe�N�X�`�����W�̕�Ԑ��x���A�ł������I�Ȃ��̂Ɏw��
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
        		GL10.GL_FASTEST);
        // �Жʕ\����L����
        gl.glEnable(GL10.GL_CULL_FACE);
        // �J�����O�ݒ��CCW��
        gl.glFrontFace(GL10.GL_CCW);
        // �[�x�e�X�g��L����
        gl.glEnable(GL10.GL_DEPTH_TEST);
    	//�e�N�X�`���@�\ON
    	gl.glEnable(GL10.GL_TEXTURE_2D);
    	//�w�i�F
    	gl.glClearColor(0.6f,0.8f,1,1);
    	// ���_�z����g�����Ƃ�錾
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //�e�N�X�`��
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        //�e�N�X�`���ǂݍ���
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
