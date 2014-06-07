package com.xshare.xshareclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a register screen to the user, offering registration
 * as well.
 */
public class RegisterActivity extends Activity {

	public static String tagString = "registerActivity";

	/**
	 * Keep track of the register task to ensure we can cancel it if requested.
	 */
	private UserregisterTask mAuthTask = null;

	// data
	private String mName;
	private String mPassword;

	// views
	private EditText mNameView;
	private EditText mPasswordView;
	private EditText mPasswordConfirmView;
	private View mregisterFormView;
	private View mregisterStatusView;
	private TextView mregisterStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);

		mNameView = (EditText) findViewById(R.id.name);
		mNameView.setText(mName);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordConfirmView = (EditText) findViewById(R.id.confirm_password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.register || id == EditorInfo.IME_NULL) {
							attemptregister();
							return true;
						}
						return false;
					}
				});

		mregisterFormView = findViewById(R.id.register_form);
		mregisterStatusView = findViewById(R.id.register_status);
		mregisterStatusMessageView = (TextView) findViewById(R.id.register_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptregister();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the register
	 * form. If there are form errors (invalid Name, missing fields, etc.), the
	 * errors are presented and no actual register attempt is made.
	 */
	public void attemptregister() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mNameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the register attempt.
		mName = mNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (!mPassword.equals(mPasswordConfirmView.getText().toString())) {
			mPasswordConfirmView
					.setError(getString(R.string.error_same_password));
			focusView = mPasswordConfirmView;
			cancel = true;
		} else if (TextUtils.isEmpty(mPassword)) {
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
			// There was an error; don't attempt register and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user register attempt.
			mregisterStatusMessageView
					.setText(R.string.register_progress_signing_up);
			showProgress(true);
			mAuthTask = new UserregisterTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the register form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mregisterStatusView.setVisibility(View.VISIBLE);
			mregisterStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mregisterStatusView
									.setVisibility(show ? View.VISIBLE
											: View.GONE);
						}
					});

			mregisterFormView.setVisibility(View.VISIBLE);
			mregisterFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mregisterFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mregisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mregisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous register/registration task used to
	 * authenticate the user.
	 */
	public class UserregisterTask extends AsyncTask<Void, Void, Boolean> {

		JSONArray files;
		boolean isNetworkOK = true;
		boolean nameexist = false;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			// reset network status
			isNetworkOK = true;
			nameexist = false;
			Map<String, String> contentMap = new HashMap<String, String>();
			contentMap.put("username", mNameView.getText().toString());
			contentMap.put("password", mPasswordView.getText().toString());
			contentMap.put("confirmPassword", mPasswordView.getText()
					.toString());
			HttpResponse response = NetworkComponent.postRequest(
					NetworkComponent.REQUEST_URL + "signUp", contentMap);
			Log.e("REgis", NetworkComponent.REQUEST_URL + "signUp");
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
							Log.d(tagString, "registerSuccess");
							return true;
						} else if (jsonResult.getString("error_code").equals(
								"103")) {
							Log.d(tagString, "UserName exist");
							nameexist = true;
							return false;
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
				Toast.makeText(getApplicationContext(), "注册成功!",
						Toast.LENGTH_SHORT).show();

				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, FileListActivity.class);
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
				finish();
			} else {
				if (isNetworkOK) {

					if (nameexist) {
						mNameView
								.setError(getString(R.string.error_name_exist));
					} else {
						mPasswordView
								.setError(getString(R.string.error_incorrect_password));
					}
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
