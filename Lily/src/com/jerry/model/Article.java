package com.jerry.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable{
	private String title;
	private String contentUrl;
	private String authorName;
	private String authorUrl;
	private String board;
	private String boardUrl;
	private int reply;
	private int view;
	
	public static final Parcelable.Creator<Article> CREATOR = new Creator<Article>() {  
        public Article createFromParcel(Parcel source) {  
        	Article myArticle = new Article();  
        	myArticle.title = source.readString();
        	myArticle.contentUrl = source.readString();
        	myArticle.authorName = source.readString();
        	myArticle.authorUrl = source.readString();
        	myArticle.board = source.readString();
        	myArticle.boardUrl = source.readString();
        	myArticle.reply = source.readInt();
        	myArticle.view = source.readInt();
            return myArticle;  
        }  
        public Article[] newArray(int size) {  
            return new Article[size];  
        }  
    };  
      
    public int describeContents() {  
        return 0;  
    }  
    public void writeToParcel(Parcel parcel, int flags) {  
        parcel.writeString(title);  
        parcel.writeString(contentUrl);
        parcel.writeString(authorName);
        parcel.writeString(authorUrl);
        parcel.writeString(board);
        parcel.writeString(boardUrl);
        parcel.writeInt(reply);
        parcel.writeInt(view);
    }  
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContentUrl() {
		return contentUrl;
	}
	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getAuthorUrl() {
		return authorUrl;
	}
	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
	public String getBoard() {
		return board;
	}
	public void setBoard(String board) {
		this.board = board;
	}
	public String getBoardUrl() {
		return boardUrl;
	}
	public void setBoardUrl(String boardUrl) {
		this.boardUrl = boardUrl;
	}
	public int getReply() {
		return reply;
	}
	public void setReply(int reply) {
		this.reply = reply;
	}
	public int getView() {
		return view;
	}
	public void setView(int view) {
		this.view = view;
	}
}
