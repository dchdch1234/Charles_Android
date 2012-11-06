package com.jerry.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.os.Bundle;

import com.jerry.model.LoginInfo;

public class LoginHelper {
	private static LoginInfo loginInfo;
	private static long currentTime;
	private static String ip;

	public static final LoginInfo getInstance(Context context) {
		if(loginInfo == null) {
			Bundle bundle = DatabaseDealer.query(context);
			loginInfo = DocParser.login(bundle);
			if(loginInfo == null) {
				return null;
			}
			currentTime = System.currentTimeMillis();
			ip = getLocalIpAddress();
		} else if(System.currentTimeMillis() - currentTime > 600000 || !ip.equals(getLocalIpAddress())){
			Bundle bundle = DatabaseDealer.query(context);
			loginInfo = DocParser.login(bundle);
			if(loginInfo == null) {
				return null;
			}
			currentTime = System.currentTimeMillis();
			ip = getLocalIpAddress();
		} else {
			currentTime = System.currentTimeMillis();
		}
		return loginInfo;
	}

	public static final LoginInfo getInstance(Bundle bundle) {
		if(loginInfo == null) {
			loginInfo = DocParser.login(bundle);
			if(loginInfo == null) {
				return null;
			}
			currentTime = System.currentTimeMillis();
			ip = getLocalIpAddress();
		} else if(System.currentTimeMillis() - currentTime > 600000 || !ip.equals(getLocalIpAddress())){
			loginInfo = DocParser.login(bundle);
			if(loginInfo == null) {
				return null;
			}
			currentTime = System.currentTimeMillis();
			ip = getLocalIpAddress();
		} else {
			currentTime = System.currentTimeMillis();
		}
		return loginInfo;
	}
	
	public static final LoginInfo resetLoginInfo(Context context) {
		loginInfo = null;
		return getInstance(context);
	}

	private static String getLocalIpAddress() {
		try { 
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
				NetworkInterface intf = en.nextElement(); 
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) { 
					InetAddress inetAddress = enumIpAddr.nextElement(); 
					if (!inetAddress.isLoopbackAddress()) { 
						return inetAddress.getHostAddress().toString(); 
					} 
				} 
			} 
		} catch (SocketException ex) { 
		} 
		return null; 

	} 
}
