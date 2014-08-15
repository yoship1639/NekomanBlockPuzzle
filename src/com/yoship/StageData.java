package com.yoship;

public class StageData {
	
	public String cleator;
	public String name;
	public String filename;
	public int version;
	public int year;
	public int month;
	public int date;
	
	public boolean isCleared = false;
	public int scoreRecord = 0;
	public int walkRecord = 9999;
	public int timeRecord = 0;
	
	public int difficulty = 0;
	
	public byte data[][][];
	
	public StageData(){}
	
}
