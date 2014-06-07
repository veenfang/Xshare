package com.xshare.xshareclient.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.MultiAutoCompleteTextView.CommaTokenizer;

/**
 * An implementation of a WebSocket protocol client. designed for being used in
 * Android platform.
 * 
 * @author freakdev
 */
public class WebSocket extends WebSocketBase {

	public final static int CONNECTING = 0; // The connection has not yet been
											// established.
	public final static int OPEN = 1; // The WebSocket connection is established
										// and communication is possible.
	public final static int CLOSING = 2; // The connection is going through the
											// closing handshake.
	public final static int CLOSED = 3; // The connection has been closed or
										// could not be opened.

	public int readyState = 0; // according to w3c specifications, should be
								// "read-only"

	Thread connectThread = new Thread(new ConnectRunnable());

	private final WebSocket instance;

	protected Handler _messageHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(!msg.equals(""))
				onmessage((String) msg.obj);
		}

	};

	/**
	 * as defined in the specification new object will automatically try to
	 * connect
	 * 
	 * @param url
	 * @throws URISyntaxException
	 */
	public WebSocket(String url) throws URISyntaxException {
		super(new URI(url));
		this.instance = this;
		connectThread.start();
	}

	// event methods
	// these methods are called when an event is raised you should overrides
	// their behavior to match your need

	protected void onopen() {
	}

	protected void onmessage(String data) {
	}

	protected void onerror() {
	}

	protected void onclose() {
	}

	public void _send(String data) {
		try {
			super.send(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		Log.d("closehere", "closehere");
		
		//if (WebSocket.CLOSING == this.readyState
			//	|| WebSocket.CLOSED == this.readyState) {
			this.readyState = WebSocket.CLOSING;
			super.close();
			super.connected = false;
			this.readyState = WebSocket.CLOSED;
		//}
	}

	@Override
	public void send(final String data) {
		// new thread
		new Thread(new Runnable() {
			public void run() {
				if (WebSocket.OPEN == instance.readyState) {
					try {
						instance._send(data);
					} catch (Exception e) {
						Log.w("WebSocket", "[send] " + e.getMessage());
						instance.onerror();
					}
				} else {
					// throw invalid state exception
				}
			}
		}).start();
	}

	protected void WaitForDataLoop() {

		Log.i("Thread Info", Thread.currentThread().getName());
		try {
			Log.i("WebSocket", "waiting for data");
			while (WebSocket.CLOSING > readyState) {
				String response = recv();
				_messageHandler.sendMessage(_messageHandler.obtainMessage(1,
						response));
			}
		} catch (IOException e) {
			if (WebSocket.CLOSING > readyState) {
				Log.w("WebSocket", "[WaitForDataLoop] " + e.getMessage());
				onerror();
			}
		}

	}

	private class ConnectRunnable implements Runnable {

		// Override
		public void run() {
			Log.i("Thread Info", Thread.currentThread().getName());
			try {
				if (WebSocket.OPEN != readyState) {
					readyState = WebSocket.CONNECTING;
					connect();
					readyState = WebSocket.OPEN;
					onopen();
					Log.i("WebSocket", "status Connected");

					WaitForDataLoop();
				}

			} catch (IOException e) {
				Log.w("WebSocket", "[Connect.run] " + e.getMessage());

				try {
					close();
				} catch (IOException e1) {
					Log.w("WebSocket", "[Connect.run |Connection fallback] "
							+ e.getMessage());
				}
			}

		}

	}
}