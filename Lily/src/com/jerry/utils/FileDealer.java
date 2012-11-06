package com.jerry.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileDealer {

	public static final void createDir() {
		String root = getHomeDirPath(); 
		String pic = getPicDirPath();
		String photo = getPhotoDirPath();
		File fileRoot = new File(root);
		File filePic = new File(pic);
		File filePhoto = new File(photo);
		if (!fileRoot.exists()) {
			fileRoot.mkdir();
		}
		if (!filePic.exists()) {
			filePic.mkdir();
		}
		if (!filePhoto.exists()) {
			filePhoto.mkdir();
		}
	}

	private static String getHomeDirPath() {
		return Environment.getExternalStorageDirectory().getPath() + "/Lily";
	}
	
	public static void clearPicCache() {
		File filePic = new File(getPicDirPath());
		if(filePic.exists()) {
			filePic.delete();
		}
		
	}

	public static String getPhotoDirPath() {
		return getHomeDirPath() + "/photo";
	}

	public static String getPicDirPath() {
		return getHomeDirPath() + "/pic";
	}

	public static final Bitmap getDiskBitmap(String pathString)  
	{  
		Bitmap bitmap = null;  
		try  
		{  
			File file = new File(pathString);  
			if(file.exists())  
			{  
				bitmap = BitmapFactory.decodeFile(pathString);  
			}  
		} catch (Exception e)  
		{  
		}  


		return bitmap;  
	} 

	public static final void writeBitmap(Bitmap bitmap,String path) {
		if(bitmap == null) {
			return;
		}
		File file = null;
		FileOutputStream fOut = null;
		file = new File(path);
		try {
			fOut = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
			fOut.flush();
			fOut.close();
			bitmap.recycle();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final Bitmap downloadBitmap(String strUrl) {
		Bitmap bitmap = null;
		URL imageUrl = null;
		try {
			imageUrl = new URL(strUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) imageUrl.openConnection();
			conn.connect();
			is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

}