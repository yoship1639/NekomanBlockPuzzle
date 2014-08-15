package com.roxiga.hypermotion3d;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

public class SpriteText {

	class TextInfo {
		public int _size;
		public String _text;
		public Sprite2D _sprite = new Sprite2D();
	}
	
	private final Paint _paint = new Paint(); // 描画情報  
	private FontMetrics _font = _paint.getFontMetrics(); // フォントメトリクス
	public Vector4D _color = new Vector4D(1, 1, 1, 1);
	
	private ArrayList<TextInfo> _ti = new ArrayList<TextInfo>();
	
	public SpriteText(){}
	
	public SpriteText(GL10 gl, String [] text, Vector2D [] pos) {
		setText(gl, text, pos);
	}
	
	public void setText(GL10 gl, String text, Vector2D pos) {
		setText(gl, 0, text, pos);
	}
	
	public void setText(GL10 gl, String [] text, Vector2D [] pos) {
		for (int i = 0; i < text.length; i++) {
			setText(gl, i, text[i], pos[i]);
		}
	}
	
	public void setText(GL10 gl, int index, String text, Vector2D pos) {
		if(_ti.size() > index){
			if(_ti.get(index)._text.equals(text)){
				_ti.get(index)._sprite._pos = pos.toVector3D();
				return;
			}
			else {
				_ti.get(index)._sprite.releaseTexture(gl);
				_ti.set(index, make(gl, text, pos));
			}
		}else{
			int sub = index - _ti.size();
			for (int i = 0; i <= sub; i++) {
				if(i == sub)_ti.add(make(gl, text, pos));
				else _ti.add(make(gl, "", new Vector2D()));
			}
		}
	}
	
	public void setPosition(int index, Vector2D pos){
		if(_ti.size() > index){
			_ti.get(index)._sprite._pos = pos.toVector3D();
		}
	}
	
	public void remake(GL10 gl){
		if(!_ti.isEmpty()){
			int s = getSize();
			for(int i=0; i<_ti.size(); i++){
				_ti.get(i)._sprite.releaseTexture(gl);
				setSize(gl, _ti.get(i)._size);
				_ti.set(i, make(gl, _ti.get(i)._text, _ti.get(i)._sprite._pos.toVector2D()));
			}
			setSize(gl, s);
		}
	}
	
	private TextInfo make(GL10 gl, String text, Vector2D pos) {
		TextInfo ti = new TextInfo();
		ti._text = text;
		ti._size = getSize();
		ti._sprite._pos = pos.toVector3D();
		
		// 文字サイズの取得  
		int[] size = { getWidth(text), getHeight() };
		
		// 2の階乗サイズに変更  
		int[] power_size ={ 2, 2 };
		
		for(int i = 0; i < size.length; i++) {  
			while(power_size[i] < size[i]) {  
				power_size[i] <<= 1;  
			}  
		}
		
		// キャンバスとビットマップを共有化  
		Bitmap image = Bitmap.createBitmap(power_size[0], power_size[1], Config.ARGB_8888);  
		Canvas canvas = new Canvas(image);
		
		// 文字列をキャンバスに描画  
		_paint.setAntiAlias(true);
		_paint.setColor(Color.WHITE);
		canvas.drawText(text, 0, Math.abs(_font.ascent), _paint);
		
		ti._sprite.setTexture(gl, image);
		canvas = null;
		return ti;
	}
	
	public void draw(GL10 gl) {
		if(!_ti.isEmpty()){
			for (int i = 0; i < _ti.size(); i++) {
				draw(gl, i, _color);
			}
		}
	}
	
	public void draw(GL10 gl, int index) {
		draw(gl, index, _color);
	}
	
	public void draw(GL10 gl, int index, Vector4D color) {
		draw(gl, index, _color, null);
	}
	
	public void draw(GL10 gl, int index, Vector2D pos) {
		draw(gl, index, _color, pos);
	}
	
	public void draw(GL10 gl, int index, Vector4D color, Vector2D pos) {
		
		if(_ti.size() > index){
			if(pos == null){
				_ti.get(index)._sprite.draw(gl, color);
			}else{
				Vector3D t = _ti.get(index)._sprite._pos;
				_ti.get(index)._sprite._pos = pos.toVector3D();
				_ti.get(index)._sprite.draw(gl, color);
				_ti.get(index)._sprite._pos = t;
			}
		}
	}
	
	public void draw(GL10 gl, String text, Vector2D pos) {
		draw(gl, text, pos, _color);
	}
	
	public void draw(GL10 gl, String text, Vector2D pos, Vector4D color) {
		// 文字サイズの取得  
		int[] size = { getWidth(text), getHeight() };
		
		// 2の階乗サイズに変更  
		int[] power_size ={ 2, 2 };
		
		for(int i = 0; i < size.length; i++) {  
			while(power_size[i] < size[i]) {  
				power_size[i] <<= 1;  
			}  
		}
		
		// キャンバスとビットマップを共有化  
		Bitmap image = Bitmap.createBitmap(power_size[0], power_size[1], Config.ARGB_8888);  
		Canvas canvas = new Canvas(image);
		
		// 文字列をキャンバスに描画  
		_paint.setAntiAlias(true);
		_paint.setColor(Color.WHITE);
		canvas.drawText(text, 0, Math.abs(_font.ascent), _paint);  
	  
		// キャンバスデータからスプライトを作成  
		Sprite2D sprite = new Sprite2D();
		sprite.setTexture(gl, image);
		sprite._pos = pos.toVector3D();
		// スプライトの描画  
		sprite.draw(gl, color);
	  
		// 開放  
		sprite.releaseTexture(gl);  
		sprite = null;  
		canvas = null;
	}
	  
	/** 
	 * 文字サイズの設定 
	 * @param size 
	 */  
	public void setSize(GL10 gl, int size) {  
		_paint.setTextSize(size);
		_font = _paint.getFontMetrics(); // 文字サイズ更新後のメトリクスを取得
	}
	
	public int getSize() {
		return (int)_paint.getTextSize();
	}
	  
	/** 
	 * 文字列の幅の取得 
	 * @param text
	 * @return 文字列の幅 
	 */  
	public int getWidth(String text) {  
		return (int) (_paint.measureText(text) + 0.5f);  
	}  
	  
	/** 
	 * 文字列の高さを取得 
	 * @return 文字列の高 
	 */  
	public int getHeight() {  
		return (int) (Math.abs(_font.top) + Math.abs(_font.bottom) + 0.5f);  
	}
	
	public Vector2D getPosition(int index){
		if(index < _ti.size()){
			return _ti.get(index)._sprite._pos.toVector2D();
		}return new Vector2D();
	}
	
	public int getLength(){
		return _ti.size();
	}
}
