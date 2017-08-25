package app.slf.simplyboard;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.doubleclick.DfpInterstitialAd;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Pizarra extends Activity implements AdListener{
private RadioButton radioNegro, radioGris,radioBlanco, radioRojo, radioNaranja, radioAmarillo;
private RadioButton radioVerde, radioVerdeBotella, radioAzul, radioBorrador,radioMas;
private Button botonMenu, botonMenuColores;
private Button botonBorrar, botonGuardar, botonFoto, botonCompartir;
private HorizontalScrollView svlayout,svMenu;
private SeekBar seekbarAlfa, seekbarSize;
private PizarraView pizarra;
private SharedPreferences preferencias;
private Dialog dialogo;
private static final int FOTO_FONDO=1;
private static final int FOTO_GALERIA=2;
private Tracker myTracker;
private GoogleAnalytics mGAInstance;
private int contadorAnuncio;
private int salirAnuncio=5;
private DfpInterstitialAd anuncioGrande;
private String ID_ANUNCIO="a151965ce2bb20a";
private TextView textAlfa, textSize;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pizarra_layout);
		preferencias=getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
		//Botones y demás views
		radioNegro=(RadioButton) findViewById(R.id.radioNegro);
		radioGris=(RadioButton) findViewById(R.id.radioGris);
		radioBlanco=(RadioButton) findViewById(R.id.radioBlanco);
		radioRojo=(RadioButton) findViewById(R.id.radioRojo);
		radioNaranja=(RadioButton) findViewById(R.id.radioNaranja);
		radioAmarillo=(RadioButton) findViewById(R.id.radioAmarillo);
		radioVerde=(RadioButton) findViewById(R.id.radioVerde);
		radioVerdeBotella=(RadioButton) findViewById(R.id.radioVerdeBotella);
		radioAzul=(RadioButton) findViewById(R.id.radioAzul);
		radioBorrador=(RadioButton) findViewById(R.id.radioBorrador);
		radioMas=(RadioButton) findViewById(R.id.radioMasColores);
		botonMenu=(Button)findViewById(R.id.buttonMenu);
		botonMenuColores=(Button)findViewById(R.id.buttonMenuColores);
		svlayout=(HorizontalScrollView) findViewById(R.id.HorizontalScrollViewColores);
		svMenu=(HorizontalScrollView) findViewById(R.id.horizontalScrollViewMenu);
		botonBorrar=(Button) findViewById(R.id.buttonBorrar);
		botonGuardar=(Button) findViewById(R.id.buttonGuardar);
		botonFoto=(Button) findViewById(R.id.buttonFoto);
		botonCompartir=(Button) findViewById(R.id.buttonCompartir);
		seekbarAlfa=(SeekBar)findViewById(R.id.seekBarAlpha);
		seekbarSize=(SeekBar)findViewById(R.id.seekBarSize);
		pizarra=(PizarraView)findViewById(R.id.pizarraView);
		textAlfa=(TextView) findViewById(R.id.textViewAlpha);
		textSize=(TextView) findViewById(R.id.textViewSize);
		PrepararBarras();
		//Para layout-portrait, ((layout))
		if(botonMenuColores!=null)
		{
			botonMenuColores.setOnClickListener(listenerMenuColores);
		}
		botonMenu.setOnClickListener(listenerMenu);
		radioNegro.setOnClickListener(listenerRadios);
		radioGris.setOnClickListener(listenerRadios);
		radioBlanco.setOnClickListener(listenerRadios);
		radioRojo.setOnClickListener(listenerRadios);
		radioNaranja.setOnClickListener(listenerRadios);
		radioAmarillo.setOnClickListener(listenerRadios);
		radioVerde.setOnClickListener(listenerRadios);
		radioVerdeBotella.setOnClickListener(listenerRadios);
		radioAzul.setOnClickListener(listenerRadios);
		radioBorrador.setOnClickListener(listenerRadios);
		radioMas.setOnClickListener(listenerRadios);
		seekbarAlfa.setOnSeekBarChangeListener(listenerBarraAlfa);
		seekbarSize.setOnSeekBarChangeListener(listenerBarraSize);
		botonBorrar.setOnClickListener(listenerbotonesMenu);
		botonGuardar.setOnClickListener(listenerbotonesMenu);
		botonFoto.setOnClickListener(listenerbotonesMenu);
		botonCompartir.setOnClickListener(listenerbotonesMenu);
		mGAInstance= GoogleAnalytics.getInstance(this);
		myTracker=mGAInstance.getTracker("UA-40070644-3");
		myTracker.sendView("PantallaPrincipal");
		contadorAnuncio=preferencias.getInt("contadorAnuncio", 0);
		Log.i("ContadorAnuncioCreate",""+contadorAnuncio);
	}//End OnCreate
	private OnClickListener listenerbotonesMenu=new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.buttonFoto:
				mostrarDialogoFoto();
				break;
			case R.id.buttonGuardar:
				guardarImagen();
				break;
			case R.id.buttonCompartir:
				compartirImagen();
				break;
			case R.id.buttonBorrar:
				ejecutarDialogoBorrar();
				break;
			}
		}
		
	};
	private OnClickListener listenerMenuColores=new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(svlayout.getVisibility()==View.GONE)
			{
				svlayout.setVisibility(View.VISIBLE);
				svMenu.setVisibility(View.GONE);				
			}
			else
			{
				svlayout.setVisibility(View.GONE);
			}
		}
		
	};
	private OnClickListener listenerMenu= new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(svMenu.getVisibility()==View.GONE)
			{
				svMenu.setVisibility(View.VISIBLE);
			}
			else
			{
				svMenu.setVisibility(View.GONE);
			}
		}
		
	};
	private OnClickListener listenerRadios=new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.radioNegro:
				pizarra.setColorLinea(Color.argb(seekbarAlfa.getProgress(), 0,0, 0));
				comprobarAnuncio();
				break;
			case R.id.radioBlanco:
				pizarra.setColorLinea(Color.argb(seekbarAlfa.getProgress(), 128, 32	, 229));
				comprobarAnuncio();
				break;
			case R.id.radioGris:
				pizarra.setColorLinea(Color.argb(seekbarAlfa.getProgress(),216, 33, 169));
				comprobarAnuncio();
				break;
			case R.id.radioRojo:
				pizarra.setColorLinea(Color.argb(seekbarAlfa.getProgress(),245, 101, 69));
				comprobarAnuncio();
				break;
			case R.id.radioNaranja:
				pizarra.setColorLinea(Color.argb(seekbarAlfa.getProgress(),255, 187, 34));
				comprobarAnuncio();
				break;
			case R.id.radioAmarillo:
				pizarra.setColorLinea(Color.argb(seekbarAlfa.getProgress(),238, 238, 34));
				comprobarAnuncio();
				break;
			case R.id.radioAzul:
				pizarra.setColorLinea(Color.argb(seekbarAlfa.getProgress(),102, 204, 221));
				comprobarAnuncio();
				break;
			case R.id.radioVerde:
				pizarra.setColorLinea(Color.argb(seekbarAlfa.getProgress(),187, 229, 53));
				comprobarAnuncio();
				break;
			case R.id.radioVerdeBotella:
				pizarra.setColorLinea(Color.argb(seekbarAlfa.getProgress(),119, 221, 187));
				comprobarAnuncio();
				break;
			case R.id.radioBorrador:
				pizarra.AsignarGoma();
				comprobarAnuncio();
				break;
			case R.id.radioMasColores:
				ejecutarDialogo();
				comprobarAnuncio();
				break;
			}
		}
		
	};
	private OnSeekBarChangeListener listenerBarraAlfa=new OnSeekBarChangeListener()
	{
		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub	
			
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub		
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			pizarra.setAplha(seekBar.getProgress());
			comprobarAnuncio();
		}
		
	};
	private OnSeekBarChangeListener listenerBarraSize= new OnSeekBarChangeListener()
	{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {	
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			pizarra.enviarGrosorLinea(seekBar.getProgress());
			comprobarAnuncio();
		}
		
	};
private void PrepararBarras()
{
	seekbarAlfa.setMax(255);
	seekbarSize.setMax(50);
	seekbarAlfa.setProgress(preferencias.getInt("progresoAlfa", 255));
    seekbarSize.setProgress(preferencias.getInt("progresoSize", 5));
     int grosor=seekbarSize.getProgress();
     int alfa=seekbarAlfa.getProgress();
    pizarra.setAplha(alfa);
	pizarra.enviarGrosorLinea(grosor);
}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pizarra, menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.compartir:
			compartirImagen();
			break;
		case R.id.guardar:
			guardarImagen();
			break;
		case R.id.ocultarCosas:
			ocultarCosas();
			break;
		default:
			return true;
		}
		return true;		
	}
	private void ocultarCosas()
	{
	if(botonMenuColores!=null)
	{
		if(seekbarAlfa.getVisibility()==View.VISIBLE)
		{
			seekbarAlfa.setVisibility(View.GONE);
			seekbarSize.setVisibility(View.GONE);
			botonMenuColores.setVisibility(View.GONE);
			botonMenu.setVisibility(View.GONE);
			textSize.setVisibility(View.GONE);
			textAlfa.setVisibility(View.GONE);
		}
		else
		{
			seekbarAlfa.setVisibility(View.VISIBLE);
			seekbarSize.setVisibility(View.VISIBLE);
			botonMenuColores.setVisibility(View.VISIBLE);
			botonMenu.setVisibility(View.VISIBLE);
			textSize.setVisibility(View.VISIBLE);
			textAlfa.setVisibility(View.VISIBLE);
		}
	}
	else 
	{
		if(seekbarAlfa.getVisibility()==View.VISIBLE)
		{
			svlayout.setVisibility(View.GONE);
			seekbarAlfa.setVisibility(View.GONE);
			seekbarSize.setVisibility(View.GONE);
			botonMenu.setVisibility(View.GONE);
			textSize.setVisibility(View.GONE);
			textAlfa.setVisibility(View.GONE);
		}
		else
		{
			svlayout.setVisibility(View.VISIBLE);
			seekbarAlfa.setVisibility(View.VISIBLE);
			seekbarSize.setVisibility(View.VISIBLE);
			botonMenu.setVisibility(View.VISIBLE);
			textSize.setVisibility(View.VISIBLE);
			textAlfa.setVisibility(View.VISIBLE);
		}
	}
	}
	@Override
	public void onPause()
	{
		super.onPause();
		Editor preferenciasEditor=preferencias.edit();
		preferenciasEditor.putInt("progresoAlfa", seekbarAlfa.getProgress());
		preferenciasEditor.putInt("progresoSize", seekbarSize.getProgress());
		preferenciasEditor.putInt("contadorAnuncio", contadorAnuncio);
		preferenciasEditor.commit();
		Log.i("ContadorAnuncio",""+contadorAnuncio);
	}
	public void crearToastpositivo()
	{
		Context context=getApplicationContext();
		int duration=Toast.LENGTH_LONG;
		Toast toast=Toast.makeText(context, "a", duration);
		LinearLayout ll=new LinearLayout(context);
		ll.setOrientation(LinearLayout.VERTICAL);
		toast.setView(ll.inflate(context, R.layout.toast_positivo_layout, ll));	
		toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}
	public void crearToastNegativo()
	{
		Context context=getApplicationContext();
		int duration=Toast.LENGTH_LONG;
		Toast toast=Toast.makeText(context, "", duration);
		LinearLayout ll=new LinearLayout(context);
		ll.setOrientation(LinearLayout.VERTICAL);
		toast.setView(ll.inflate(context, R.layout.toast_negativo_layout, ll));
		toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();		
	}
	public void guardarImagen()
	{
		contadorAnuncio++;
		pizarra.setDrawingCacheEnabled(true);
		Bitmap b=pizarra.getDrawingCache();
		try
		{
			String nombreArchivo="SimplyBoard"+System.currentTimeMillis(); 
			ContentValues values=new ContentValues();
			values.put(Images.Media.TITLE, nombreArchivo);
			values.put(Images.Media.DATE_ADDED,System.currentTimeMillis());
			values.put(Images.Media.MIME_TYPE, "image/jpg");
			values.put(Images.Media.ORIENTATION, 0);
			Uri uri=this.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI,values);
				OutputStream outStream=this.getContentResolver().openOutputStream(uri);
				b.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				outStream.flush();
				outStream.close();
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse
		        		("file://" + Environment.getExternalStorageDirectory())));	
				crearToastpositivo();
		}
		catch(FileNotFoundException e)
		{
			crearToastNegativo();
		}
		catch(IOException e)
		{
			crearToastNegativo();
		}
		pizarra.setDrawingCacheEnabled(false);
	}
	public void compartirImagen()
	{
		contadorAnuncio++;
		pizarra.setDrawingCacheEnabled(true);
		Bitmap b=pizarra.getDrawingCache();
		ContentValues values=new ContentValues();
		values.put(Images.Media.DATE_ADDED,System.currentTimeMillis());
		values.put(Images.Media.MIME_TYPE, "image/jpg");
		values.put(Images.Media.ORIENTATION, 0);
		Uri uri=this.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI,values);
		try
		{
			OutputStream outStream=this.getContentResolver().openOutputStream(uri);
			b.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
		}
		catch(FileNotFoundException e)
		{
			crearToastNegativo();//Lo sujeta un handler aparte
		}
		catch(IOException e)
		{
			crearToastNegativo();//Lo sujeta un handler aparte
		}
		ArrayList <Uri> imageuris=new ArrayList <Uri>();
		imageuris.add(uri);
		pizarra.setDrawingCacheEnabled(false);
		Intent shareIntent=new Intent(Intent.ACTION_SEND_MULTIPLE);
		shareIntent.setData(uri);
		shareIntent.setType("image/jpg");
		shareIntent.putExtra(Intent.EXTRA_STREAM,imageuris);
		this.startActivity(Intent.createChooser(shareIntent, "Share"));
	}
	private void mostrarDialogoFoto()
	{
	    dialogo=new Dialog(this);
		dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogo.setContentView(R.layout.layout_foto);
		final Button botonGaleria=(Button) dialogo.findViewById(R.id.buttonGaleriaDialogo);
		final Button botonFotoDialogo=(Button) dialogo.findViewById(R.id.buttonFotoDialogo);
		botonGaleria.setOnClickListener(listenerbotonGaleria);
		botonFotoDialogo.setOnClickListener(listenerbotonFotoDialogo);
		dialogo.show();
	}
	private OnClickListener listenerbotonGaleria=new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i=new Intent(Intent.ACTION_PICK);
			i.setType("image/*");
			startActivityForResult(i,FOTO_GALERIA);
			dialogo.dismiss();
		}
		
	};
	private OnClickListener listenerbotonFotoDialogo=new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			recogerFoto();
			dialogo.dismiss();
		}
		
	};
	private void ejecutarDialogo()
	{
		dialogo=new Dialog(this);
		dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogo.setContentView(R.layout.layout_color_personalizado);
		final SeekBar barraRoja=(SeekBar) dialogo.findViewById(R.id.seekBarRojoColorPersonalizado);
		final SeekBar barraVerde=(SeekBar) dialogo.findViewById(R.id.seekBarVerdeColorPersonalizado);
		final SeekBar barraAzul=(SeekBar) dialogo.findViewById(R.id.seekBarAzulColorPersonalizado);
		final View viewFondo=(View) dialogo.findViewById(R.id.viewMostrarColor);
		barraRoja.setOnSeekBarChangeListener(listenerBarrasColor);
		barraVerde.setOnSeekBarChangeListener(listenerBarrasColor);
		barraAzul.setOnSeekBarChangeListener(listenerBarrasColor);
		barraRoja.setMax(255);
		barraVerde.setMax(255);
		barraAzul.setMax(255);
		barraRoja.setProgress(preferencias.getInt("BarraRojo", 75));
		barraAzul.setProgress(preferencias.getInt("BarraAzul", 80));
		barraVerde.setProgress(preferencias.getInt("BarraVerde", 220));
		viewFondo.setBackgroundColor(Color.rgb(barraRoja.getProgress(), barraVerde.getProgress(),
				barraAzul.getProgress()));
		viewFondo.setOnClickListener(listenerViewFondo);
		dialogo.setCancelable(false);
		dialogo.show();
	}
	private OnSeekBarChangeListener listenerBarrasColor=new OnSeekBarChangeListener()
	{
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub		
			SeekBar barraRoja=(SeekBar) dialogo.findViewById(R.id.seekBarRojoColorPersonalizado);
			 SeekBar barraVerde=(SeekBar) dialogo.findViewById(R.id.seekBarVerdeColorPersonalizado);
			 SeekBar barraAzul=(SeekBar) dialogo.findViewById(R.id.seekBarAzulColorPersonalizado);
			 View viewFondo=(View) dialogo.findViewById(R.id.viewMostrarColor);
			 viewFondo.setBackgroundColor(Color.rgb(barraRoja.getProgress(), barraVerde.getProgress(),
					 barraAzul.getProgress()));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub		
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		
		}
		
	};
	private OnClickListener listenerViewFondo= new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SeekBar barraRoja=(SeekBar) dialogo.findViewById(R.id.seekBarRojoColorPersonalizado);
			 SeekBar barraVerde=(SeekBar) dialogo.findViewById(R.id.seekBarVerdeColorPersonalizado);
			 SeekBar barraAzul=(SeekBar) dialogo.findViewById(R.id.seekBarAzulColorPersonalizado);
			pizarra.setColorLinea(Color.argb(seekbarAlfa.getProgress(),barraRoja.getProgress(), barraVerde.getProgress(), 
					barraAzul.getProgress()));
			Editor preferenciasEditor= preferencias.edit();
			preferenciasEditor.putInt("BarraRojo", barraRoja.getProgress());
			preferenciasEditor.putInt("BarraVerde", barraVerde.getProgress());
			preferenciasEditor.putInt("BarraAzul", barraAzul.getProgress());
			preferenciasEditor.commit();
			dialogo.dismiss();
		}
		
	};
	public void ejecutarDialogoBorrar()
	{
		dialogo=new Dialog(this);
		dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogo.setContentView(R.layout.layout_dialogo_borrar);
		final ImageButton botonAceptar=(ImageButton) dialogo.findViewById(R.id.buttonAceptar);
		final ImageButton botonRechazar=(ImageButton) dialogo.findViewById(R.id.buttonCancelar);
		botonAceptar.setOnClickListener(listenerbotonAceptar);
		botonRechazar.setOnClickListener(listenerBotonRechazar);
		dialogo.show();
	}
	private OnClickListener listenerbotonAceptar=new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			pizarra.borrarTodo();
			dialogo.dismiss();
		}
		
	};
	private OnClickListener listenerBotonRechazar=new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dialogo.dismiss();
		}
		
	};
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_CANCELED)
		{
			crearToastNegativo();
		}
		else if(resultCode==Activity.RESULT_OK)
		{
			switch(requestCode)
			{
			case FOTO_GALERIA:
				Uri fotoUri= data.getData();
				try
				{
					Bitmap fotogaleria= Media.getBitmap(getContentResolver(), fotoUri);
					colocarFondo(fotogaleria);
				}
				catch(Exception e)
				{
					crearToastNegativo();
				}
			case FOTO_FONDO:
				try
				{
					Bitmap b=(Bitmap) data.getExtras().get("data");
					colocarFondo(b);
				}
				catch(Exception e)
				{
					;
				}
			}
		}
	}
	private void colocarFondo(Bitmap b)
	{
		Drawable d=new BitmapDrawable(b);
		d.setFilterBitmap(true);
		pizarra.setBackgroundDrawable(d);
	}
	private void recogerFoto()
	{
		
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
        startActivityForResult(cameraIntent, FOTO_FONDO);
	}
	private void comprobarAnuncio()
	{
		if(contadorAnuncio>=salirAnuncio)
		{
			LocationManager locationManager;
	        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
	        String provider=LocationManager.NETWORK_PROVIDER;
	        Log.w("Provider", provider);
	      
	    	   Location l= locationManager.getLastKnownLocation(provider);
	    	   if (l!=null)
	    	   {
	    		   Double latitud=l.getLatitude();
	               Double longitud=l.getLongitude();
	               Log.w("Latitud", ""+latitud);
	               Log.w("Longitud", ""+longitud);
	               anuncioGrande=new DfpInterstitialAd(this, ID_ANUNCIO);
	              AdRequest adRequest= new AdRequest();
	               adRequest.setLocation(l);   
	               anuncioGrande.loadAd(adRequest);
	               anuncioGrande.setAdListener(this);
	               //anuncioGrande.show();	               
	    	   }
	    	   else
	    	   {
	    		   Log.w("LastKnowLocation","null");
	    		   anuncioGrande=new DfpInterstitialAd(this, ID_ANUNCIO);
	               AdRequest adRequest= new AdRequest();
	               adRequest.addTestDevice("63ACE90E082F4C4E41F19211E5365700");
	               adRequest.addTestDevice("SH18LTR14402");   
	               anuncioGrande.loadAd(adRequest);
	               anuncioGrande.setAdListener(this);
	       	 }}
}
	    @Override
		public void onDismissScreen(Ad arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onLeaveApplication(Ad arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onPresentScreen(Ad arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onReceiveAd(Ad ad) {
			// TODO Auto-generated method stub
			if(ad==anuncioGrande)
			{
				anuncioGrande.show();
			}
			contadorAnuncio=0;
		}
	
}

