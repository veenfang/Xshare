package com.xshare.xshareclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class NetworkComponent {

	public static String serverHost = "222.200.185.66";
	public static int serverPort = 8888;

	public static String tagString = "NetworkComponent";
	public static String broadcastAddress = "255.255.255.255";

	private static String PHPSESSID = null;
	
	public static String ServerIP = "http://islider.sinaapp.com/getServer.php";

	public static final String REQUEST_URL_PATH = "/islider-mobile/control/requestHandler.php?controlType=";
	public static final String UPLOAD_URL_PATH = "/islider-mobile/control/upload.php";
	public static final String CHECK_UPLOAD_PATH = "/islider-mobile/control/checkProcess.php";
	

	public static String SERVER_URL = "http://" + serverHost
			+ "/islider-mobile/";
	public static String REQUEST_URL = "http://" + serverHost
			+ REQUEST_URL_PATH;
	public static String UPLOAD_URL = "http://" + serverHost + UPLOAD_URL_PATH;
	public static String CHECK_URL = "http://" + serverHost + CHECK_UPLOAD_PATH;

	
	public static void modifyUrl(){
		SERVER_URL = "http://" + serverHost
				+ "/islider-mobile/";
		REQUEST_URL = "http://" + serverHost
				+ REQUEST_URL_PATH;
		UPLOAD_URL = "http://" + serverHost + UPLOAD_URL_PATH;
	}
	/*
	 * get the server IP using UDP
	 */
	public static void getServer(final Context context) throws IOException {
		byte[] buffer = "getServer".getBytes();
		DatagramPacket dataPacket;
		final DatagramSocket udpSocket = new DatagramSocket();

		try {
			InetAddress address = InetAddress.getByName(broadcastAddress);
			dataPacket = new DatagramPacket(buffer, buffer.length, address,
					8000);

			udpSocket.send(dataPacket);
			final int port = udpSocket.getLocalPort();
			Log.d(tagString, "SendUdp Port: " + port);

			new Thread() {
				public void run() {
					boolean noServer = true;
					while (noServer) {
						try {
							byte data[] = new byte[1024];
							DatagramPacket packet = new DatagramPacket(data,
									data.length);

							udpSocket.receive(packet);
							String result = new String(packet.getData(),
									packet.getOffset(), packet.getLength());
							Log.d("log", "Server IP: " + result);
							URI tServer = new URI(result);
							serverHost = tServer.getHost();
							serverPort = tServer.getPort();
							REQUEST_URL = "http://" + serverHost
									+ REQUEST_URL_PATH;
							UPLOAD_URL = "http://" + serverHost
									+ UPLOAD_URL_PATH;
							noServer = false;
							udpSocket.close();
						} catch (Exception e) {
							// TODO: handle exception
							Log.e(tagString, "UDP Socket Exception");
							e.printStackTrace();
						}
					}
				}

			}.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.e(tagString, "UnKnown network Address, check if connected");
			e.printStackTrace();
		}
	}

	// the target port was 8000
	public static void broadcast(final Context context, String tContentString)
			throws IOException {

		byte[] buffer = tContentString.getBytes();
		DatagramPacket dataPacket;
		final DatagramSocket udpSocket = new DatagramSocket();

		try {
			InetAddress address = InetAddress.getByName(broadcastAddress);
			dataPacket = new DatagramPacket(buffer, buffer.length, address,
					8000);

			udpSocket.send(dataPacket);
			final int port = udpSocket.getLocalPort();
			Log.d(tagString, "SendUDP LocalPort: " + port);

			new Thread() {
				public void run() {

					try {
						byte data[] = new byte[1024];
						DatagramPacket packet = new DatagramPacket(data,
								data.length);
						udpSocket.receive(packet);
						String result = new String(packet.getData(),
								packet.getOffset(), packet.getLength());
						URI tServer = new URI(result);
						serverHost = tServer.getHost();
						serverPort = tServer.getPort();
						udpSocket.close();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}

			}.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.e(tagString, "UnKnown network Address, check if connected");
			e.printStackTrace();
		}
	}
	
	public static String checkUpload(String filename){
		Map<String, String> contentMap = new HashMap<String, String>();
		contentMap.put("filename", filename);
		StringBuilder builder = new StringBuilder();
		HttpResponse response = NetworkComponent.postRequest(CHECK_URL, contentMap);
		if (response != null) {
			StatusLine statusLine = response.getStatusLine();
			if (statusLine != null) {
				int responseCode = statusLine.getStatusCode();
				if (responseCode == HttpStatus.SC_OK) {
					// TO DO LIST				
					try {
						BufferedReader bufferedReader2 = new BufferedReader(
								new InputStreamReader(response.getEntity()
										.getContent()));
						for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2
								.readLine()) {
							builder.append(s);
						}
						return builder.toString();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return builder.toString();
	}

	public static HttpResponse postRequest(String url,
			Map<String, String> rawParams) {
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpPost post = new HttpPost(url);
			if (null != PHPSESSID) {
				post.setHeader("Cookie", "PHPSESSID=" + PHPSESSID);
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : rawParams.keySet()) {

				params.add(new BasicNameValuePair(key, rawParams.get(key)));
			}
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = httpClient.execute(post);

			CookieStore mCookieStore = ((AbstractHttpClient) httpClient)
					.getCookieStore();
			List<Cookie> cookies = mCookieStore.getCookies();
			for (int i = 0; i < cookies.size(); i++) {
				if ("PHPSESSID".equals(cookies.get(i).getName())) {
					PHPSESSID = cookies.get(i).getValue();
					break;
				}
			}

			return httpResponse;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return null;
	}

	// multipart method to upload file
	public static String uploadFile(String uploadurl, String filePath) {

		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		try {
			URL url = new URL(uploadurl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Cookie", "PHPSESSID="+PHPSESSID);
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedFile\"; filename=\""
					+ filePath.substring(filePath.lastIndexOf("/") + 1)
					+ "\""
					+ end);
			dos.writeBytes(end);
			FileInputStream fis = new FileInputStream(filePath);
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);

			}
			fis.close();
			Log.d(tagString, "file send to server............");
			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();

			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();
			dos.close();
			is.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Internal error";
	}
}
