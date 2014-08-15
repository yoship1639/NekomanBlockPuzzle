package com.yoship;

import java.util.List;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.roxiga.hypermotion3d.SV;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


public final class NekomanBlockPuzzleActivity extends Activity implements LocationListener  {
	//
	private GLSurfaceView _glSurfaceView;
	private NekomanBlockPuzzle _renderer;
	private AdView adView;
	private AdRequest adRequest;
	private LocationManager lm;
	private Handler handler = new Handler();

	// @Override
	// 作成
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		_glSurfaceView = (GLSurfaceView) this.findViewById(R.id.glSurface);
		_renderer = new NekomanBlockPuzzle();
		_glSurfaceView.setRenderer(_renderer);
		SV.Context = this;
		
		_glSurfaceView.setDrawingCacheEnabled(true);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Display display = getWindowManager().getDefaultDisplay();
		SV.SetScreenSize(display.getWidth(), display.getHeight());
		GV.Init(this);

		adView = (AdView) this.findViewById(R.id.adView);
		adRequest = new AdRequest();
		adRequest.addTestDevice("FE11B712480A44E05DF5703AC4BD3509");
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		adView.loadAd(adRequest);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            GV.versionName = packageInfo.versionName;
       } catch (NameNotFoundException e) {
            e.printStackTrace();
       }
	}

	// @Override
	// 画面がタッチされたときの処理
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			_renderer._tm.actionDown(x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			_renderer._tm.actionMove(x, y);
			break;
		case MotionEvent.ACTION_UP:
			_renderer._tm.actionUp(x, y);
			break;
		}
		return true;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			_renderer._km.onKeyDown(keyCode);
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			_renderer._km.onKeyUp(keyCode);
			break;
		default:
			return super.onKeyUp(keyCode, event);
		}
		return false;
	}

	// @Override
	// フォーカスが再開したとき
	protected void onResume() {
		Log.i("Activity", "onResume()");
		GV.LoadSetting();
		// 再開
		super.onResume();
		// 再開
		_glSurfaceView.onResume();
		if (lm != null){
			List<String> provs = lm.getProviders(true);
			for(String prov : provs){
				lm.requestLocationUpdates(prov, 0, 0, this);
			}
		}
	}

	// @Override
	// フォーカスを失ったとき
	protected void onPause() {
		Log.i("Activity", "onPause()");
		_renderer.onPause();
		// 一時停止
		super.onPause();
		// 一時停止
		_glSurfaceView.onPause();
		if (lm != null)lm.removeUpdates(this);
	}
	
	protected void onDestroy() {
		Log.i("Activity", "onDestroy()");
		if (adView != null)adView.destroy();
		super.onDestroy();
		_renderer.onDestroy();
	}
	
	

	public void showAd() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						adView.setVisibility(View.VISIBLE);
					}
				});
			}
		}).start();
	}
	
	public void hideAd() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						adView.setVisibility(View.INVISIBLE);
					}
				});
			}
		}).start();
	}

	@Override
	public void onLocationChanged(Location location) {
		if(adRequest != null)adRequest.setLocation(location);
		if(adView != null)adView.loadAd(adRequest);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	public void showToast(final String str, final int duration){
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), str, duration).show();
					}
				});
			}
		}).start();
	}
	
	public Bitmap getGLViewDrawingCache(){
		return _glSurfaceView.getDrawingCache();
	}
	
	public void setGLViewDrawingCache(boolean flag){
		_glSurfaceView.setDrawingCacheEnabled(flag);
	}
	
	public void showAlertDialog(final String title, final String text){
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(SV.Context)
						.setTitle(title)
						.setMessage(text)
						.setPositiveButton("OK", null)
						.show();
					}
				});
			}
		}).start();
		
	}
}
