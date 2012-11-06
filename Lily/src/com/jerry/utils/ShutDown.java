package com.jerry.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.widget.Toast;

public class ShutDown {

	private static Boolean isExit = false;  
	private static Boolean hasTask = false;
	
	private static Timer tExit = new Timer();  
	private static TimerTask task = new TimerTask() {  
		@Override  
		public void run() {  
			isExit = false;  
			hasTask = true;  
		}  
	}; 
	
	public static void shutDownActivity(Activity activity) {
		if(isExit == false ) {  
			isExit = true;  
			Toast.makeText(activity, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
			if(!hasTask) {  
				tExit.schedule(task, 2000);  
			}
		} else {
			activity.finish();  
			System.exit(0);  
		}  
	}
}
