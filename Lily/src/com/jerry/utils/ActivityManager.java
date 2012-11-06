package com.jerry.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityManager {
	public static List<Activity> activityList = new ArrayList<Activity> ();
	public static void removeAllActivity() {
		if (activityList.size() == 0) {
			return;
		}
		for (int i = 0 ; i < activityList.size(); i ++) {
			activityList.get(i).finish();
		}
		activityList.clear();
	}

}
