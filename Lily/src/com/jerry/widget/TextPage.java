package com.jerry.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;

public   class  TextPage  extends  EditText {  
    private   int  off;  //�ַ�����ƫ��ֵ   
  
    public  TextPage(Context context) {  
        super (context);  
        initialize();  
    }  
  
    private   void  initialize() {  
        setGravity(Gravity.TOP);  
        setBackgroundColor(Color.WHITE);  
    }
    
    public TextPage(Context context, AttributeSet attrs) {  
    	super(context,attrs);
    }
    
    public TextPage(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
      
    @Override   
    protected   void  onCreateContextMenu(ContextMenu menu) {  
        //�����κδ���Ϊ����ֹ������ʱ�򵯳������Ĳ˵�   
    }  
      
    @Override   
    public   boolean  getDefaultEditable() {  
        return   false ;  
    }  
      
    @Override   
    public   boolean  onTouchEvent(MotionEvent event) {  
        int  action = event.getAction();  
        Layout layout = getLayout();  
        int  line =  0 ;  
        switch (action) {  
        case  MotionEvent.ACTION_DOWN:  
            line = layout.getLineForVertical(getScrollY()+ (int )event.getY());          
            off = layout.getOffsetForHorizontal(line, (int )event.getX());  
            Selection.setSelection(getEditableText(), off);  
            break ;  
        case  MotionEvent.ACTION_MOVE:  
        case  MotionEvent.ACTION_UP:  
            line = layout.getLineForVertical(getScrollY()+(int )event.getY());   
            int  curOff = layout.getOffsetForHorizontal(line, ( int )event.getX());              
            Selection.setSelection(getEditableText(), off, curOff);  
            break ;  
        }  
        return   true ;  
    }  
}  
