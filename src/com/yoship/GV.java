package com.yoship;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.roxiga.hypermotion3d.SV;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

public final class GV {
	
	public static final int VERSION_TRIAL = 0;		//ëÃå±î≈
	public static final int VERSION_RELEASE = 1;	//êªïiî≈
	
	public static final int STAGE_MAX_NUM = 40;
	public static final int FORMAL_LEVEL_NUM = 5;
	public static final int TRIAL_AND_FORMAL_LEVEL_NUM = FORMAL_LEVEL_NUM + 1;
	
	public static final int LEVEL_TRIAL = 0;
	public static final int LEVEL_BEGINNER = 1;
	public static final int LEVEL_EASY = 2;
	public static final int LEVEL_NORMAL = 3;
	public static final int LEVEL_HARD = 4;
	public static final int LEVEL_LUNATIC = 5;
	public static final int LEVEL_ORIGINAL = 6;
	public static final int LEVEL_NETWORK = 7;
	
	public static final String STRING_LEVEL [] = {
		"TRIAL",
		"BEGINNER",
		"EASY",
		"NORMAL",
		"HARD",
		"LUNATIC",
		"ORIGINAL",
		"NETWORK",
	};
	
	public static final int STATE_TITLE = 0;
	public static final int STATE_SETTING = 1;
	public static final int STATE_TUTORIAL = 2;
	public static final int STATE_STAGE_SELECT = 3;
	public static final int STATE_PLAY = 4;
	public static final int STATE_EDIT = 5;
	public static final int STATE_CHARANGE = 6;
	public static final int STATE_MAX_NUM = 7;
	
	public static final int version = VERSION_TRIAL;
	
	public static StageData StageData[][];
	public static ArrayList<StageData> StageData_Origin;
	public static ArrayList<StageData> StageData_Network;
	public static StageData StageData_Tutorial;
	
	public static String versionName = "";
	
	public static boolean IsShowFPS = true;
	public static boolean IsPlaySE = true;
	public static int TextureQuality = 1;
	
	public static final int 
	DRAW_USUALLY = 0,
	DRAW_ONE_FRAME_SKIP = 1,
	DRAW_ANY_FRAMES_SKIP = 2;
	public static int DrawingMethod = DRAW_ONE_FRAME_SKIP;
	
	public static int SelectIndex = 1;
	public static int SelectLevel = 0;
	
	public static final int QUALITY_HIGH = 0;
	public static final int QUALITY_MIDDLE = 1;
	public static final int QUALITY_LOW = 2;
	
	public static int FrameSkip = 0;
	
	private GV(){}
	
	public static void Init(Context context){
		
		StageData = new StageData[TRIAL_AND_FORMAL_LEVEL_NUM][STAGE_MAX_NUM];
		for(int i=1; i<=STAGE_MAX_NUM; i++)StageData[0][i-1] = LoadStageData_Assets("stage/trial/trial"+i);
		StageData_Tutorial = LoadStageData_Assets("stage/tutorial/tutorial");
		if(version == VERSION_RELEASE){
			String level [] = {
					"beginner",
					"easy",
					"normal",
					"hard",
					"lunatic",
			};
			for(int i=1; i<=FORMAL_LEVEL_NUM; i++){
				for(int j=1; j<=STAGE_MAX_NUM; j++){
					StageData[i][j-1] = LoadStageData_Assets("stage/formal/"+level[i]+"/"+level[i]+j);
				}
			}
			StageData_Origin = new ArrayList<StageData>();
			StageData_Network = new ArrayList<StageData>();
		}
	}
	
	public static StageData LoadStageData_Assets(String path){
		StageData sd = new StageData();
		
		int slash = path.lastIndexOf('/');
		if(slash == -1){
			Log.e("LoadStageData_Assets", "path is not exist '/'...");
			return null;
		}
		String filename = path.substring(slash+1);

		sd.isCleared = SV.LoadPref_Boolean(filename+"_clear");
		sd.scoreRecord = SV.LoadPref_Int(filename+"_score");
		sd.timeRecord = SV.LoadPref_Int(filename+"_time");
		if(sd.timeRecord == 0)sd.timeRecord = 9999;
		sd.walkRecord = SV.LoadPref_Int(filename+"_walk");
		if(sd.walkRecord == 0)sd.walkRecord = 9999;
		
		sd.filename = filename;
		
		AssetManager as = SV.Context.getResources().getAssets();
	    
        InputStream is = null;
        BufferedReader br = null;

        try{
            try {
                is = as.open(path+".txt");
                br = new BufferedReader(new InputStreamReader(is));
                
                sd.difficulty = Integer.parseInt(br.readLine());
    			int x = Integer.parseInt(br.readLine());
    			int z = Integer.parseInt(br.readLine());
    			sd.data = new byte[2][z][x];
    			for(int k=0; k<2; k++){
    				for(int i=0; i<z; i++){
    					String str = br.readLine();
    					for(int j=0; j<x; j++)sd.data[k][i][j] = Byte.parseByte(String.valueOf(str.charAt(j)));
    				}
    			}
            } finally {  
                if (br != null) br.close();  
            }  
        } catch (IOException e) {  
        	Log.i("LoadStageData_Assets", "Not exist file: "+path);
            return null;
        }
        Log.i("LoadStageData_Assets", "Success read: "+path);
        return sd;
	}
	
	public static StageData LoadStageData_Local(String filename){
		StageData sd = new StageData();
		
		sd.isCleared = SV.LoadPref_Boolean(filename+"_clear");
		sd.scoreRecord = SV.LoadPref_Int(filename+"_score");
		sd.timeRecord = SV.LoadPref_Int(filename+"_time");
		sd.walkRecord = SV.LoadPref_Int(filename+"_walk");

		BufferedReader br = null;
		
		try{
			FileInputStream fis = SV.Context.openFileInput(filename+".txt");
			br = new BufferedReader(new InputStreamReader(fis));
			sd.filename = filename;
			sd.cleator = br.readLine();
			sd.name = br.readLine();
			sd.year = Integer.parseInt(br.readLine());
			sd.month = Integer.parseInt(br.readLine());
			sd.date = Integer.parseInt(br.readLine());
			sd.version = Integer.parseInt(br.readLine());
			sd.difficulty = Integer.parseInt(br.readLine());
			int x = Integer.parseInt(br.readLine());
			int z = Integer.parseInt(br.readLine());
			sd.data = new byte[2][z][x];
			for(int k=0; k<2; k++){
				for(int i=0; i<z; i++){
					String str = br.readLine();
					for(int j=0; j<x; j++)sd.data[k][i][j] = Byte.parseByte(String.valueOf(str.charAt(j)));
				}
			}
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
		
		return sd;
	}
	
	public static void SaveStageResult(StageData stageData){
		SV.SavePref(stageData.filename+"_clear", stageData.isCleared+"");
		SV.SavePref(stageData.filename+"_score", stageData.scoreRecord+"");
		SV.SavePref(stageData.filename+"_time", stageData.timeRecord+"");
		SV.SavePref(stageData.filename+"_walk", stageData.walkRecord+"");
	}
	
	public static void LoadSetting(){
		IsShowFPS = SV.LoadPref_Boolean("ShowFPS");
		IsPlaySE = SV.LoadPref_Boolean("PlaySE");
		TextureQuality = SV.LoadPref_Int("TexQuality");
		DrawingMethod = SV.LoadPref_Int("DrawingMethod");
	}
	
	public static void SaveSetting(){
		SV.SavePref("ShowFPS", IsShowFPS+"");
		SV.SavePref("PlaySE", IsPlaySE+"");
		SV.SavePref("TexQuality", TextureQuality+"");
		SV.SavePref("DrawingMethod", DrawingMethod+"");
	}
	
	
	public static void ShowAd(){
		NekomanBlockPuzzleActivity act = (NekomanBlockPuzzleActivity)SV.Context;
		act.showAd();
	}
	
	public static void HideAd(){
		NekomanBlockPuzzleActivity act = (NekomanBlockPuzzleActivity)SV.Context;
		act.hideAd();
	}
	
	public static Bitmap getDrawingCache(){
		NekomanBlockPuzzleActivity act = (NekomanBlockPuzzleActivity)SV.Context;
		return act.getGLViewDrawingCache();
	}
	
	public static void setDrawingCacheEnable(boolean flag){
		NekomanBlockPuzzleActivity act = (NekomanBlockPuzzleActivity)SV.Context;
		act.setGLViewDrawingCache(flag);
	}
	
	public static void ShowAlertDialog(String title, String text){
		NekomanBlockPuzzleActivity act = (NekomanBlockPuzzleActivity)SV.Context;
		act.showAlertDialog(title, text);
	}
}

