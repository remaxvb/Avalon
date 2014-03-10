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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ServiceCaller {

	/**
	 * Create Http asynctask, execute and return result as JSONObject
	 */

	private final String TAG = ServiceCaller.class.getSimpleName();

	private final String URL_HOST = "trm-rails.herokuapp.com";
	private final String URL_SIGN_IN = "/api/users/sign_in";
	private final String URL_SIGN_UP = "/api/users";
	private final String AUTH_USERNAME = "admin";
	private final String AUTH_PASSWORD = "admin";
	private final int AUTH_PORT = 80;
	private String urlBasePath;

	private JSONObject result;

	private ServiceCallback serviceCallback;
	
	private static ServiceCaller instance = null;

	public static synchronized ServiceCaller getInstance() {
		if (instance == null) {
			instance = new ServiceCaller();
		}
		return instance;

	}
	
	public void setServiceCallback(ServiceCallback sc) {
		this.serviceCallback = sc;
	}
	
	private ServiceCaller() {
		
	}

	/**
	 * Call sign in service
	 * @param context: UI thread call this service
	 * @param isShowProgress: Do you want to show the progress dialog during the task
	 * @param userName
	 * @param password
	 */
	public void callSignIn(Context context, boolean isShowProgress,String userName, String password) {
		JSONObject jsonRequest = new JSONObject();
		JSONObject jsonUserInfo = new JSONObject();
		try {
			jsonUserInfo.accumulate("username", userName);
			jsonUserInfo.accumulate("password", password);
			jsonRequest.accumulate("user", jsonUserInfo);
		} catch (JSONException e) {
			return;
		}
		
		executeHttp(context, isShowProgress, URL_HOST, URL_SIGN_IN, jsonRequest);
	}

	/**
	 * Get result of the last execute
	 * @return
	 */
	public JSONObject getResult() {
		return result;
	}

	/**
	 * Make a http POST
	 * @param context: UI thread call this service
	 * @param isShowProgress: Do you want to show the progress dialog during the task
	 * @param hostUrl: Which host you are running
	 * @param serviceUrl: Service address, exampe: /api/login
	 * @param data: Json data you want to post
	 */
	private void executeHttp(Context context, boolean isShowProgress, final String hostUrl,
			final String serviceUrl, JSONObject data) {
		urlBasePath = "http://" + hostUrl + serviceUrl;
		new HttpAsyncTask(context, isShowProgress).execute(new JSONObject[] { data });
	}

	/**
	 * Http POST task support
	 * @author hieu.t.vo
	 *
	 */
	class HttpAsyncTask extends AsyncTask<JSONObject, Void, JSONObject> {

		private Context context;
		boolean isShowPregress;

		public HttpAsyncTask(Context context, boolean isShowProgress) {
			this.context = context;
			this.isShowPregress = isShowProgress;
		}

		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			result = null;
			if(isShowPregress) {
				pd = new ProgressDialog(context);
				pd.show();
			}
			
		}

		@Override
		protected JSONObject doInBackground(JSONObject... params) {
			return sendPost(params[0]);
		}

		@Override
		protected void onPostExecute(JSONObject response) {
			result = null;
			result = response;
			if(isShowPregress) {
				pd.dismiss();
			}			
//			Toast.makeText(context,result.toString(), 
//	                Toast.LENGTH_LONG).show();
			serviceCallback.onLoginCallback(response);
		}

	}

	/**
	 * Support method: Make a http POST
	 * @param requestJson
	 * @return: Response as JSONObject
	 */
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
			Log.d(TAG, e.getMessage());
		}
		return null;
	}
	
	public interface ServiceCallback {
		public void onLoginCallback(Object data);
	}
}
