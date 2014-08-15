package com.roxiga.hypermotion3d;

public class FPSGetter extends Thread{
	
	private float fps = 0.0f;
	private long time;
	private int count = 0;
	private int updateMillis;
	private long htime, old;
	
	public FPSGetter(){
		this.updateMillis = 1000;
	}
	
	public FPSGetter(int updateMillis){
		this.updateMillis = updateMillis;
	}
	
	public void start(){
		time = System.currentTimeMillis();
		super.start();
	}
	
	public void run(){
		while(true){
			try {
				Thread.sleep(updateMillis - (System.currentTimeMillis() - time));
			} catch (InterruptedException e){}
			time = System.currentTimeMillis();
			long h = htime - time;
			fps = (count * 1000.0f / updateMillis) + (count / (float)updateMillis)*h;
			count = 0;
		}
	}
	
	public void update(){
		count++;
		old = htime;
		htime = System.currentTimeMillis();
	}
	
	public float getFPS(){
		return fps;
	}
	
	public long getInterval(){
		if(htime - old < 100000)return htime - old;
		return 0;
	}
	
	public String toString(){
		return String.format("%.1ffps", fps);
	}
}
