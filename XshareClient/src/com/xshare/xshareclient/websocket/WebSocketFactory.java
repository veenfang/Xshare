package com.xshare.xshareclient.websocket;

import java.net.URISyntaxException;

import com.xshare.xshareclient.NetworkComponent;

import android.util.Log;
import android.webkit.WebView;

public class WebSocketFactory {

	WebView mView;

	public WebSocketFactory(WebView view) {
		mView = view;
	}

	public WebSocket getNew(String url) throws URISyntaxException {
		String serverString = "ws://" + NetworkComponent.serverHost + ":"
				+ NetworkComponent.serverPort;
		Log.d("abc", serverString);
		return new GapWebSocket(mView, serverString);
	}

}
