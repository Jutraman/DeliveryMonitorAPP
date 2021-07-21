package com.myapp.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;


public class GLFont {
    /*
     * Ĭ�ϲ��ð�ɫ���壬�������ּӴ�
     */
	public static Bitmap getImage(int width, int height ,String mString, int size) {
		return getImage(width, height, mString, size, Color.RED, Typeface.create("����",Typeface.BOLD));
	}
	
	public static Bitmap getImage(int width, int height ,String mString,int size ,int color) {
		return getImage(width, height, mString, size, color, Typeface.create("����",Typeface.BOLD));
	}
	
	public static Bitmap getImage(int width, int height ,String mString,int size ,int color, String familyName) {
		return getImage(width, height, mString, size, color, Typeface.create(familyName,Typeface.BOLD));
	}
	
	public static Bitmap getImage(int width, int height ,String mString,int size, int color, Typeface font) {
		int x = width;
		int y = height;
		
		Bitmap bmp = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888); 
		//ͼ���СҪ�������ִ�С����,�Ժ��ı����ȶ�Ӧ 
		Canvas canvasTemp = new Canvas(bmp); 
		canvasTemp.drawColor(Color.BLUE); 
		Paint p = new Paint(); 
		p.setColor(color);
		p.setTypeface(font);
		p.setAntiAlias(true);//ȥ�����
		p.setFilterBitmap(true);//��λͼ�����˲�����
		p.setTextSize(scalaFonts(size));
		float tX = (x - getFontlength(p, mString))/2;
		float tY = (y - getFontHeight(p))/2+getFontLeading(p);	
		canvasTemp.drawText(mString,tX,tY,p); 

		return bmp;
	}

	/**
	 * ������Ļϵ��������ȡ���ִ�С
	 * @return
	 */
	private static float scalaFonts(int size) {
		//��δʵ��
		return size;
	}

	/**
	 * @return ����ָ���ʺ�ָ���ַ����ĳ���
	 */
	public static float getFontlength(Paint paint, String str) {
		return paint.measureText(str);
	}
	/**
	 * @return ����ָ���ʵ����ָ߶�
	 */
	public static float getFontHeight(Paint paint)  {  
	    FontMetrics fm = paint.getFontMetrics(); 
	    return fm.descent - fm.ascent;  
	} 
	/**
	 * @return ����ָ���������ֶ����Ļ�׼����
	 */
	public static float getFontLeading(Paint paint)  {  
	    FontMetrics fm = paint.getFontMetrics(); 
	    return fm.leading- fm.ascent;  
	} 
	
}