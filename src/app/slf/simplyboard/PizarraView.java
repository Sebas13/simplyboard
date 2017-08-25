package app.slf.simplyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PizarraView extends View {
	private static final float TOLERANCIA_AL_TOQUE = 10;
	private Bitmap bitmap;//bitmap2; //Área de dibujo para enseñar o guardar
	private Canvas bitmapCanvas;//Usado para dibujar en el bitmap
	private Paint pincelPantalla; //El pincel que usaremos para pintar el bitmap en la pantalla
	private Paint pincelLinea;//Usado para dibujar lineas en el bitmap
	private HashMap<Integer,Path> mapaTrayectoria;//Trayectorias actuales siendo dibujados
	private HashMap<Integer,Point> mapaPuntoAnterior;//Puntos actuales
	public PizarraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		 
		pincelPantalla= new Paint();
		pincelLinea=new Paint();
		pincelLinea.setAntiAlias(true);
		pincelLinea.setStyle(Paint.Style.STROKE);
		pincelLinea.setStrokeCap(Paint.Cap.ROUND);
		pincelLinea.setStrokeWidth(5);
		mapaTrayectoria=new HashMap<Integer,Path>();
		mapaPuntoAnterior=new HashMap<Integer,Point>();
	}
	public void AsignarGoma()
	{
		pincelLinea.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	}
	@Override
	public void onSizeChanged(int w,int h,int oldW,int oldH)
	{
		bitmap= Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);//Creamos el bitMap
		bitmapCanvas=new Canvas(bitmap);//Creamos el canvas con el fondo del bitmap creado anteriormente	
	bitmap.eraseColor(Color.TRANSPARENT);
	}
	public void borrarTodo()
	{
		mapaTrayectoria.clear();
		mapaPuntoAnterior.clear();
		bitmap.eraseColor(Color.TRANSPARENT);
		invalidate();	
	}
	public void setColorLinea(int color)
	{
		pincelLinea.setColor(color);
		if(pincelLinea.getXfermode()!=null)
		{
			pincelLinea.setXfermode(null);
		}
	}
	public int getColorLinea()
	{
		return pincelLinea.getColor();
	}
	public void enviarGrosorLinea(int grosor)
	{
		pincelLinea.setStrokeWidth(grosor);
	}
	public void setAplha(int alpha)
	{
		pincelLinea.setAlpha(alpha);
	}
	public int recibirGrosorLinea()
	{
		return (int) pincelLinea.getStrokeWidth();
	}
	@Override
	protected void onDraw (Canvas canvas)
	{
		canvas.drawBitmap(bitmap, 0, 0,pincelPantalla);
			for(Integer key:mapaTrayectoria.keySet())
			{
				canvas.drawPath(mapaTrayectoria.get(key), pincelLinea);
			}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int accion=event.getActionMasked();
		int indiceAccion=event.getActionIndex();
		if(accion==MotionEvent.ACTION_DOWN||accion==MotionEvent.ACTION_POINTER_DOWN)
		{
				toqueEmpezado(event.getX(indiceAccion),event.getY(indiceAccion),
						event.getPointerId(indiceAccion));
		}
		else if(accion==MotionEvent.ACTION_UP||accion==MotionEvent.ACTION_POINTER_UP)
		{
			toqueTerminado(event.getPointerId(indiceAccion));					
		}
		else 
		{
				toqueMovido(event);											
		}
		invalidate();	
		return true;
	}
	private void toqueEmpezado(float x, float y,int lineID)
	{
		Path camino;
		Point punto;
		if(mapaTrayectoria.containsKey(lineID))
		{
			camino=mapaTrayectoria.get(lineID);
			camino.reset();
			punto= mapaPuntoAnterior.get(lineID);
		}
		else
			{
				camino=new Path();
				mapaTrayectoria.put(lineID, camino);
				punto = new Point();
				mapaPuntoAnterior.put(lineID, punto);
			}		
		camino.moveTo(x, y);
		punto.x=(int)x;
		punto.y=(int)y;	
	}	
	private void toqueMovido(MotionEvent event)
	{
		for(int i=0;i<event.getPointerCount();i++)
		{
			int punteroID=event.getPointerId(i);
			int punteroIndice=event.findPointerIndex(punteroID);
			if(mapaTrayectoria.containsKey(punteroID))
			{
				float newX= event.getX(punteroIndice);
				float newY=event.getY(punteroIndice);
				Path trayectoria= mapaTrayectoria.get(punteroID);
				Point puntoAnterior=mapaPuntoAnterior.get(punteroID);
				float deltaX=Math.abs(newX-puntoAnterior.x);
				float deltaY=Math.abs(newY-puntoAnterior.y);
				if(deltaX>=TOLERANCIA_AL_TOQUE || deltaY>=TOLERANCIA_AL_TOQUE)
				{
					trayectoria.quadTo(puntoAnterior.x, puntoAnterior.y, 
							(newX+puntoAnterior.x)/2, (newY+puntoAnterior.y)/2);
					puntoAnterior.x=(int) newX;
					puntoAnterior.y=(int) newY;
				}
				
			}	
		}
	}
	private void toqueTerminado(int lineID)
	{
			Path trayectoria=mapaTrayectoria.get(lineID);
			bitmapCanvas.drawPath(trayectoria, pincelLinea);
			trayectoria.reset();			
	}
}
