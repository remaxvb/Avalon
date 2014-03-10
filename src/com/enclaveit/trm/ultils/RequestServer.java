package com.enclaveit.trm.ultils;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class RequestServer {

	private final String URL_HOST = "trm-rails.herokuapp.com";
	private final String URL_SIGN_IN = "/api/users/sign_in";
	private final String URL_SIGN_UP = "/api/users";
	private final String AUTH_USERNAME = "admin";
	private final String AUTH_PASSWORD = "admin";
	private final int AUTH_PORT = 80;
	private String urlBasePath;

	/* Begin Singleton part */
	private static RequestServer instance = null;

	protected RequestServer() {
		// do nothing
	}

	public static RequestServer getInstance() {
		if (instance == null) {
			instance = new RequestServer();
		}
		return instance;
	}
	
	public void LoginTask(Activity act) {
		
		new LoginAsyncTask(act).execute(new String[] {""});
	}
	
	class LoginAsyncTask extends AsyncTask<String, Void, String> {
		
		Activity act;
		public LoginAsyncTask(Activity act) {
			this.act = act;
		}
		private ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(act);
			dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
		}
		
	}

	/* End Singleton part */

	private JSONObject sendPost(JSONObject requestJson) {
		HttpClient httpClient = new DefaultHttpClient();
		AuthScope authScope = new AuthScope(this.URL_HOST, AUTH_PORT);
		UsernamePasswordCredentials userCreds = new UsernamePasswordCredentials(
				AUTH_USERNAME, AUTH_PASSWORD);
		((AbstractHttpClient) httpClient).getCredentialsProvider()
				.setCredentials(authScope, userCreds);
		BasicHttpContext localContext = new BasicHttpContext();
		BasicScheme basicAuth = new BasicScheme();
		localContext.setAttribute("preemptive-auth", basicAuth);
		HttpPost httpPost = new HttpPost(this.urlBasePath);
		try {
			StringEntity strEntity = new StringEntity(requestJson.toString());
			httpPost.setEntity(strEntity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			HttpResponse httpResponse = httpClient.execute(httpPost,
					localContext);
			String responeJson = EntityUtils.toString(httpResponse.getEntity());
			return new JSONObject(responeJson);
		} catch (Exception e) {
			Log.d("Request Server", e.getMessage());
		}
		return null;
	}

	private class SendingRequest {
		private Activity base;
		private JSONObject request;
		private String message;
		private boolean wait;

		public SendingRequest(Activity base, JSONObject request,
				String message, boolean wait) {
			this.base = base;
			this.request = request;
			this.message = message;
			this.wait = wait;
		}

		public Activity getSender() {
			return this.base;
		}

		public JSONObject getRequest() {
			return this.request;
		}

		public String getMessage() {
			return this.message;
		}

		public boolean isWait() {
			return this.wait;
		}
	}

	private class HttpExecute extends
			AsyncTask<SendingRequest, Void, JSONObject> {
		
		@Override
		protected JSONObject doInBackground(SendingRequest... params) {
//			ProgressDialog dialog = null;
//			if (params[0].isWait()) {
//				dialog = new ProgressDialog(params[0].getSender());
//				dialog.setMessage(params[0].getMessage());
//				dialog.show();
//			}
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			//JSONObject result = sendPost(params[0].getRequest());
//			if (dialog != null) {
//				dialog.dismiss();
//			}
//			return result;
			return null;
		}

	}

	public boolean signIn(Activity base, String userName, String userPass,
			String userFullName) {
		if (!userName.isEmpty() && !userPass.isEmpty()) {
			this.urlBasePath = "http://" + URL_HOST + URL_SIGN_IN;
			JSONObject requestJson = new JSONObject();
			JSONObject userInfoJson = new JSONObject();
			try {
				userInfoJson.accumulate("username", userName);
				userInfoJson.accumulate("password", userPass);
				requestJson.accumulate("user", userInfoJson);
				SendingRequest request = new SendingRequest(base, requestJson,
						"Checking account...", true);
				HttpExecute sender = new HttpExecute();
				sender.execute(request);
				JSONObject responeJson = sender.get();
				boolean isSuccess = responeJson.getBoolean("success");
				if (isSuccess) {
					userFullName = responeJson.getString("fullname");
				}
				return isSuccess;
			} catch (Exception e) {
				Log.d("Request Server", e.getMessage());
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean signUp(String userName, String userPass, String userEmail) {
		if (!userName.isEmpty() && !userPass.isEmpty() && !userEmail.isEmpty()) {
			this.urlBasePath = "http://" + this.URL_HOST + this.URL_SIGN_UP;
			JSONObject requestJson = new JSONObject();
			JSONObject userInfoJson = new JSONObject();
			try {
				userInfoJson.accumulate("username", userName);
				userInfoJson.accumulate("password", userPass);
				userInfoJson.accumulate("email", userEmail);
				requestJson.accumulate("user", userInfoJson);
				JSONObject responeJson = sendPost(requestJson);
				boolean isSuccess = responeJson.getBoolean("success");
				return isSuccess;
			} catch (Exception e) {
				Log.d("Request Server", e.getMessage());
				return false;
			}
		} else {
			return false;
		}
	}
}