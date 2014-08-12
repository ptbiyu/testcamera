package com.example.testcamera.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class DisplayUtil {
	
	

	/**
	 * 获取屏幕像素密度
	 * @param context
	 * @return
	 */
	public static Display getDefaultDisplay(Context context){
		WindowManager windowManager=(WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		
	//	Log.d("zby log", "windowdisplay-----"+windowManager.getDefaultDisplay().getWidth()+"---"+windowManager.getDefaultDisplay().getHeight()); 
		return windowManager.getDefaultDisplay();  
	}
	
	
	/**
	 * 获取屏幕宽度
	 * @param context
	 * @return
	 */
	public static float getSceenDensity(Context context){
		DisplayMetrics displayMetrics =new DisplayMetrics();
		getDefaultDisplay(context).getMetrics(displayMetrics);
	//	displayMetrics=context.getResources().getDisplayMetrics();
	//	Log.d("zby log","displayMetrics----"+displayMetrics.widthPixels+"----"+displayMetrics.heightPixels+"---"+displayMetrics.density);
		return displayMetrics.density;
	} 
}
