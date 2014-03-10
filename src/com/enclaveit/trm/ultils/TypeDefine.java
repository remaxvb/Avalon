package com.enclaveit.trm.ultils;

import org.json.JSONObject;

import android.content.Context;

public class TypeDefine {

	/* Begin Singleton part */
	private static TypeDefine instance = null;

	public TypeDefine() {
		// do nothing
	}

	public static TypeDefine getInstace() {
		if (instance == null) {
			instance = new TypeDefine();
		}
		return instance;
	}

	/* End Singleton part */

	public class HttpRequestType {
		private Context context;
		private boolean isShowProgress;
		private String hostUrl;
		private String serviceUrl;
		private JSONObject data;

		public HttpRequestType(Context context, boolean isShowProgress,
				String hostUrl, String serviceUrl, JSONObject data) {
			this.context = context;
			this.isShowProgress = isShowProgress;
			this.hostUrl = hostUrl;
			this.serviceUrl = serviceUrl;
			this.data = data;
		}

		public Context getContext() {
			return this.context;
		}

		public boolean isShowProgress() {
			return this.isShowProgress;
		}

		public String getHostUrl() {
			return this.hostUrl;
		}

		public String getServiceUrl() {
			return this.serviceUrl;
		}

		public JSONObject getData() {
			return this.data;
		}
	}

	public HttpRequestType createHttpRequest(Context context,
			boolean isShowProgress, String hostUrl, String serviceUrl,
			JSONObject data) {
		return new HttpRequestType(context, isShowProgress, hostUrl,
				serviceUrl, data);
	}
}
