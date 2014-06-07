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
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	public static String tagString = "LoginActivity";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// data
	private String mName;
	private String mPassword;

	// views
	private EditText mNameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		//---------------------------------------------
		/*Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost post = new HttpPost(NetworkComponent.ServerIP);
				try {
					HttpResponse httpResponse = httpClient.execute(post);
					if (httpResponse != null) {

						StatusLine statusLine = httpResponse.getStatusLine();
						if (statusLine != null) {
							int responseCode = statusLine.getStatusCode();
							if (responseCode == HttpStatus.SC_OK) {
								StringBuilder builder = new StringBuilder();
								BufferedReader bufferedReader2 = new BufferedReader(
										new InputStreamReader(httpResponse.getEntity()
												.getContent()));
								for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2
										.readLine()) {
									builder.append(s);
								}
								NetworkComponent.serverHost = builder.toString();
								NetworkComponent.modifyUrl();
							}
						}
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});*/
		//thread.start();
		//---------------------------------------------
	
		
	//	NetworkComponent.serverHost = "222.200.185.66";
	//	NetworkComponent.modifyUrl();
		
		
		
		mNameView = (EditText) findViewById(R.id.name);
		mNameView.setText(mName);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		findViewById(R.id.to_register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent();
						intent.setClass(getApplicationContext(), RegisterActivity.class);
						startActivity(intent);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid Name, missing fields, etc.), the errors
	 * are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mNameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mName = mNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid Name address.
		if (TextUtils.isEmpty(mName)) {
			mNameView.setError(getString(R.string.error_field_required));
			focusView = mNameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		JSONArray files;
		boolean isNetworkOK = true;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			// reset network status
			isNetworkOK = true;

			Map<String, String> contentMap = new HashMap<String, String>();
			contentMap.put("username", mNameView.getText().toString());
			contentMap.put("password", mPasswordView.getText().toString());
			//--------------------
			SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			editor.putString("username", contentMap.get("username"));
			editor.putString("password", contentMap.get("password"));
			editor.commit();
			//--------------------
			HttpResponse response = NetworkComponent.postRequest(
					NetworkComponent.REQUEST_URL + "signIn", contentMap);

			try {
				return handleResponse(response);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isNetworkOK = false;
			return false;// unsuccess
		}

		private boolean handleResponse(HttpResponse response)
				throws IllegalStateException, IOException, JSONException {
			if (response != null) {

				StatusLine statusLine = response.getStatusLine();
				if (statusLine != null) {
					int responseCode = statusLine.getStatusCode();
					if (responseCode == HttpStatus.SC_OK) {
						// TO DO LIST
						StringBuilder builder = new StringBuilder();
						BufferedReader bufferedReader2 = new BufferedReader(
								new InputStreamReader(response.getEntity()
										.getContent()));
						for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2
								.readLine()) {
							builder.append(s);
						}
						Log.d(tagString, "response: " + builder.toString());
						JSONObject jsonResult = new JSONObject(
								builder.toString());
						files = jsonResult.getJSONArray("files");
						if (jsonResult.getString("error_code").equals("100")) {
							Log.d(tagString, "LoginSuccess");
							return true;
						}
					}
				}
			}
			isNetworkOK = false;
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, FileListActivity.class);
				ArrayList<String> filesList = new ArrayList<String>();
				for (int i = 0; i < files.length(); i++) {
					try {
						filesList.add(files.getString(i));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				intent.putExtra("files", filesList);
				startActivity(intent);
				//finish();
			} else {
				if (isNetworkOK) {
					mPasswordView
							.setError(getString(R.string.error_incorrect_password));
				} else {
					Toast.makeText(getApplicationContext(),
							"Check your network", Toast.LENGTH_SHORT).show();
				}
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
