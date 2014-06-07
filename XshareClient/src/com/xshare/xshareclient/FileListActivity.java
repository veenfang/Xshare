package com.xshare.xshareclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import com.xshare.xshareclient.LoginActivity.UserLoginTask;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FileListActivity extends Activity implements View.OnClickListener {

	/*
	 * Constants
	 */
	public static String tagString = "FileListActivity";

	// Request Selected File from FileSelectActivity
	private final int ViewFileRequestCode = 0;

	// Handler Code
	private final int msgFinishProcessData = 1;

	private final int msgUploadSuccess = 2;
	// private FileCheckTask checkTask = null;

	/**
	 * Data
	 */
	// the list of uploaded files
	private ArrayList<String> fileArrayList;
	List<Map<String, Object>> fileMaps;
	SimpleAdapter mAdapter;

	/*
	 * Views
	 */
	private ListView fileListView;
	private ImageButton btnSelectUploadfileButton;
	private LinearLayout progressbarLinearLayout;

	/*
	 * Control
	 */
	private static Handler fileListActivityhHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_list);

		// Setup views
		btnSelectUploadfileButton = (ImageButton) findViewById(R.id.btn_selectUploadfile);
		btnSelectUploadfileButton.setOnClickListener(this);
		fileListView = (ListView) findViewById(R.id.listViewFileList);
		progressbarLinearLayout = (LinearLayout) findViewById(R.id.file_status);
		// handler
		fileListActivityhHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case msgFinishProcessData:
					Intent intent = new Intent();
					intent.setClass(FileListActivity.this, MainActivity.class);
					intent.putExtra("lecture",
							msg.getData().getString("content"));
					startActivity(intent);
					progressbarLinearLayout.setVisibility(View.GONE);
					break;
				case msgUploadSuccess:
					Toast.makeText(getApplicationContext(),
							"上传状态：" + msg.getData().getString("msg"),
							Toast.LENGTH_SHORT).show();
					String filename = msg.getData().getString("filename");
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("title", filename);
					map.put("info", "no detail");
					String prefix = filename.substring(
							filename.lastIndexOf(".") + 1);
					if (prefix.equals("pdf")) {
						map.put("icon", R.drawable.filetype_pdf);
					} else if (prefix.equals("xls") || prefix.equals("xlsx")) {
						map.put("icon", R.drawable.filetype_excel);
					} else if (prefix.equals("ppt") || prefix.equals("pptx")) {
						map.put("icon", R.drawable.filetype_ppt);
					} else if (prefix.equals("doc") || prefix.equals("docx")) {
						map.put("icon", R.drawable.filetype_word);
					} else {
						map.put("icon", R.drawable.filetype_pdf);
					}
					// status
					map.put("status", "ready");
					fileMaps.add(map);
					mAdapter.notifyDataSetChanged();
					progressbarLinearLayout.setVisibility(View.GONE);
				default:
					break;
				}
			}

		};

		/**********************************************************************
		 * Process existed extra data
		 **********************************************************************/

		if (this.getIntent() != null) {
			fileArrayList = this.getIntent().getExtras()
					.getStringArrayList("files");

			// construct data
			fileMaps = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < fileArrayList.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", fileArrayList.get(i));
				map.put("info", "no detail");
				String prefix = fileArrayList.get(i).substring(
						fileArrayList.get(i).lastIndexOf(".") + 1);
				if (prefix.equals("pdf")) {
					map.put("icon", R.drawable.filetype_pdf);
				} else if (prefix.equals("xls") || prefix.equals("xlsx")) {
					map.put("icon", R.drawable.filetype_excel);
				} else if (prefix.equals("ppt") || prefix.equals("pptx")) {
					map.put("icon", R.drawable.filetype_ppt);
				} else if (prefix.equals("doc") || prefix.equals("docx")) {
					map.put("icon", R.drawable.filetype_word);
				} else {
					map.put("icon", R.drawable.filetype_pdf);
				}

				// status
				map.put("status", "ready");

				fileMaps.add(map);
			}

			// setup adapter
			mAdapter = new SimpleAdapter(getApplicationContext(),
					fileMaps, R.layout.simple_list, new String[] { "title",
				"info", "icon", "status" }, new int[] { R.id.title,
				R.id.info, R.id.imageView1, R.id.status });
			fileListView.setAdapter(mAdapter); // assign
																			// callback
			fileListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					final Map<String, String> contentMap = new HashMap<String, String>();
					Log.d("filename", ((TextView) arg1.findViewById(R.id.title)).getText().toString());
					contentMap.put("filename", ((TextView) arg1.findViewById(R.id.title)).getText().toString());
					progressbarLinearLayout.setVisibility(View.VISIBLE);
					new Thread() {
						@Override
						public void run() {
							try {
								final HttpResponse response = NetworkComponent
										.postRequest(
												NetworkComponent.REQUEST_URL
														+ "getLectureInformation",
												contentMap);
								handleResponse(response);
							} catch (IllegalStateException e) { // TODO
																// Auto-generated
																// catch block
								e.printStackTrace();
							} catch (IOException e) { // TODO Auto-generated
								// catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							super.run();
						}
					}.start();
				}
			});

		} else {
			Log.e(tagString, "No file provide, check your call stack");
			finish();
		}
		/**********************************************************************
		 * Finish process existed extra data
		 *********************************************************************/
	}

	private boolean handleResponse(HttpResponse response)
			throws IllegalStateException, IOException, JSONException {
		if (response != null) {

			StatusLine statusLine = response.getStatusLine();
			if (statusLine != null) {
				int responseCode = statusLine.getStatusCode();
				if (responseCode == HttpStatus.SC_OK) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader2 = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));

					for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2
							.readLine()) {
						builder.append(s);
					}
					Bundle responseBundle = new Bundle();
					responseBundle.putString("content", builder.toString());
					Log.d(tagString, "frameResponse:" + builder.toString());
					Message msg = new Message();
					msg.setData(responseBundle);
					msg.what = msgFinishProcessData;
					fileListActivityhHandler.sendMessage(msg);
					return true;// succeed
				}
			}
		}
		return false;// fail processing
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_list, menu);
		return true;
	}

	// view onClick
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_selectUploadfile:
			Intent intent = new Intent(FileListActivity.this,
					FileSelectActivity.class);
			startActivityForResult(intent, ViewFileRequestCode);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && ViewFileRequestCode == requestCode) {
			final Bundle bundle = data.getExtras();
			if (data != null && bundle != null) {
				Log.d(tagString, bundle.getString("file"));
				progressbarLinearLayout.setVisibility(View.VISIBLE);
				new Thread() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Looper.prepare();
						String resultString = NetworkComponent.uploadFile(
								NetworkComponent.UPLOAD_URL,
								bundle.getString("file"));
						Log.d(tagString, "uploadResult: " + resultString);
						Message msg = new Message();
						msg.what = msgUploadSuccess;
						Bundle resultBundle = new Bundle();
						resultBundle.putString("msg", resultString);
						msg.setData(resultBundle);
						String info = "doing";
						String filename = bundle.getString("file");
						String[] filenames = filename.split("/");
						while(!info.equals("done")){
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							info = NetworkComponent.checkUpload(filenames[filenames.length-1]);
							Log.d("info", info);
						}
						resultBundle.putString("filename", filenames[filenames.length-1]);
						fileListActivityhHandler.sendMessage(msg);
					}

				}.start();

				// checkTask = new FileCheckTask();
				// checkTask.execute((Void) null);
			}
		}
	}

	/*
	 * /----------------------- public class FileCheckTask extends
	 * AsyncTask<Void, Void, Boolean> { boolean isNetworkOK = true;
	 * 
	 * @Override protected Boolean doInBackground(Void... params) { // TODO:
	 * attempt authentication against a network service. // reset network status
	 * isNetworkOK = true;
	 * 
	 * Map<String, String> contentMap = new HashMap<String, String>();
	 * contentMap.put("username", mNameView.getText().toString());
	 * contentMap.put("password", mPasswordView.getText().toString());
	 * HttpResponse response = NetworkComponent.postRequest(
	 * NetworkComponent.REQUEST_URL + "signIn", contentMap);
	 * 
	 * try { return handleResponse(response); } catch (IllegalStateException e)
	 * { // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace();
	 * } catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } isNetworkOK = false; return false;// unsuccess } /
	 */// -----------------------
}
