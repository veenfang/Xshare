package com.xshare.xshareclient;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xshare.xshareclient.HSVColorPickerDialog.OnColorSelectedListener;
import com.xshare.xshareclient.websocket.GapWebSocket;
import com.xshare.xshareclient.websocket.WebSocketFactory;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements View.OnClickListener{   //implements view.onclickListener

	private WebView contentWebView;
	private Button markButton;
	private Button pageButton;
	private Button pageBackward;
	private Button chooseColor;
	private FrameLayout mLayout;

	private static String tagString = "MainActivity";
	private GapWebSocket webSocket;
	
	

	private ArrayList<String> frameArrayList = new ArrayList<String>();
	private FrameLayout.LayoutParams params = 
			new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		FrameLayout ll = (FrameLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_main, null);
		
		//---
		contentWebView = new WebView(this); 
		mLayout = (FrameLayout) findViewById(R.id.frameLayout); 
		mLayout.addView(contentWebView);
		mLayout.addView(ll);
		//---
		markButton = (Button) ll.findViewById(R.id.btn_mark);
		markButton.setOnClickListener(this);
		
		
		final HSVColorPickerDialog cpd = new HSVColorPickerDialog( MainActivity.this, 0xFF4488CC, new OnColorSelectedListener() {
		    public void colorSelected(Integer color) {
		        // Do something with the selected color
		    	String colorString = "#"+Integer.toHexString(color).substring(2);
		    	Log.d("colorPick:", ""+colorString);
		    	contentWebView.loadUrl("javascript:changeColor('"+ colorString +"')");
		    	Log.d("colorPick:", "javascript:changeColor(\""+ colorString +"\')");
		    }
		});
		cpd.setTitle( "请选择颜色" );
		
		chooseColor  =(Button)ll.findViewById(R.id.btn_color);
		chooseColor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cpd.show();
			}
		});
		
		
		
		contentWebView.getSettings().setJavaScriptEnabled(true);
		contentWebView.getSettings().setBuiltInZoomControls(true);
		//contentWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
	
		contentWebView.setWebChromeClient(new WebChromeClient() {
			//print debug message
			public void onConsoleMessage(String message, int lineNumber, String sourceID) {
		        Log.d("MyAp	plication", message + " -- From line "
		                             + lineNumber + " of "
		                             + sourceID);
		      }
			
		});
		String lecture = this.getIntent().getExtras().getString("lecture");
		JSONObject json;
		contentWebView.addJavascriptInterface(new WebSocketFactory(
				contentWebView), "WebSocketFactory");
		contentWebView.addJavascriptInterface(this, "webview");
		try {
			json = new JSONObject(lecture);
			Log.d("jsonObject", json.getString("roomname"));		
			contentWebView.loadUrl(NetworkComponent.SERVER_URL + "index1.php?roomname="+json.getString("roomname"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		if (NetworkComponent.serverHost == null) {
			try {
				NetworkComponent.getServer(getApplicationContext());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void preventZoom() {
		contentWebView.getSettings().setSupportZoom(false);
		contentWebView.getSettings().setBuiltInZoomControls(false);
		contentWebView.getSettings().setJavaScriptEnabled(true);
		float factor = contentWebView.getScale();
		// Log.d("float", ""+factor);
		contentWebView.loadUrl("javascript:getZoomFactor('" + factor + "')");
	}

	public void permitZoom() {
		contentWebView.getSettings().setSupportZoom(true);
		contentWebView.getSettings().setBuiltInZoomControls(true);
	}

	public void JloadUrl() {
		String lecture = "";
		if (this.getIntent() != null) {
			lecture = this.getIntent().getExtras().getString("lecture");
			Log.d("Mainactivity", "javascript:loadLectureInformation('"
					+ lecture + "')");
			contentWebView.loadUrl("javascript:loadLectureInformation('"
					+ lecture + "')");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_mark:
			contentWebView.loadUrl("javascript:mark()");
			break;
		
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		Log.d("destroy", "destroy");
		//contentWebView.loadUrl("javascript:stopSocket()");
		GapWebSocket.instance.close();
		contentWebView.clearCache(true);
		mLayout.removeView(contentWebView);
		mLayout.removeView(markButton);
		contentWebView.removeAllViews();
		contentWebView.destroy();
		android.os.Process.killProcess(android.os.Process.myPid());
		this.finish();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

			if(contentWebView != null) contentWebView.loadUrl("javascript:pageForward()");

			return true;

		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

			if(contentWebView != null) contentWebView.loadUrl("javascript:pageBackward()");

			return true;

		} else {

			return super.onKeyDown(keyCode, event);

		}

	}
}
