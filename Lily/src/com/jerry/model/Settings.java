package com.jerry.model;

public class Settings {

	private String sign;
	private boolean isLogin;
	private boolean isShowPic;
	private boolean isSavePic;
	private boolean isSendMail;
	
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public boolean isLogin() {
		return isLogin;
	}
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	public boolean isShowPic() {
		return isShowPic;
	}
	public void setShowPic(boolean isShowPic) {
		this.isShowPic = isShowPic;
	}
	public boolean isSavePic() {
		return isSavePic;
	}
	public void setSavePic(boolean isSavePic) {
		this.isSavePic = isSavePic;
	}
	public boolean isSendMail() {
		return isSendMail;
	}
	public void setSendMail(boolean isSendMail) {
		this.isSendMail = isSendMail;
	}

}
