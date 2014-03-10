package com.enclaveit.trm.ultils.fayeclient;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Paint.Join;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.enclaveit.trm.model.UserInfo;
import com.enclaveit.trm.ultils.fayeclient.WebSocketClient.WebsocketListener;

public class FayeClient implements WebsocketListener {

	private static final String HANDSHAKE_CHANNEL = "/meta/handshake";
	private static final String CONNECT_CHANNEL = "/meta/connect";
	private static final String DISCONNECT_CHANNEL = "/meta/disconnect";
	private static final String SUBSCRIBE_CHANNEL = "/meta/subscribe";
	private static final String UNSUBSCRIBE_CHANNEL = "/meta/unsubscribe";

	private static final String KEY_CHANNEL = "channel";
	private static final String KEY_SUCCESS = "successful";
	private static final String KEY_CLIENT_ID = "clientId";
	private static final String KEY_VERSION = "version";
	private static final String KEY_MIN_VERSION = "minimumVersion";
	private static final String KEY_SUBSCRIPTION = "subscription";
	private static final String KEY_SUP_CONN_TYPES = "supportedConnectionTypes";
	private static final String KEY_CONN_TYPE = "connectionType";
	private static final String KEY_DATA = "data";
	private static final String KEY_ID = "id";
	private static final String VALUE_VERSION = "1.0";
	private static final String VALUE_MIN_VERSION = "1.0";
	private static final String VALUE_CONN_TYPE = "websocket";

	private static final long RECONNECT_WAIT = 10000;
	private static final int MAX_CONNECTION_ATTEMPTS = 3;

	private static final String TAG = FayeClient.class.getSimpleName();

	// Singleton
	private static FayeClient instance;

	public synchronized static FayeClient getInstance() {
		if (instance == null) {
			instance = new FayeClient();
		}
		return instance;
	}

	// :Singleton

	private Handler mHandler;

	private String mfayeClientID;
	private String mBaseURL;
	private String mfayeSubChannel;

	private boolean mConnected = false;

	private WebSocketClient mWSClient;

	private int mConnectionAttempts = 0;
	private boolean mRunning = false;
	private boolean mReconnecting = false;

	private FayeListener mfayeListener;

	private Queue<Object> mFayeEventQueue;
	private EventExecutor mEventExecutor;

	private Runnable mConnectionMonitor = new Runnable() {

		@Override
		public void run() {
			if (!mConnected) {
				openWebsocket();

				if (mConnectionAttempts < MAX_CONNECTION_ATTEMPTS) {
					mConnectionAttempts++;
					getHandler().postDelayed(this, RECONNECT_WAIT);
				}
			} else {
				getHandler().removeCallbacks(this);
				mRunning = false;
				mConnectionAttempts = 0;
				mReconnecting = false;
			}

		}
	};

	private void resetWebsocket() {
		if (!mConnected) {
			if (!mRunning) {
				getHandler().post(mConnectionMonitor);
			}
		}
	}

	public boolean isConnected() {
		return mConnected;
	}

	public void setHandler(final Handler handler) {
		this.mHandler = handler;
	}

	public void setBaseURL(final String baseURL) {
		this.mBaseURL = baseURL;
	}

	public void setListener(final FayeListener listener) {
		this.mfayeListener = listener;
	}

	public FayeClient() {
		mFayeEventQueue = new ArrayDeque<Object>();
	}

	private void openWebsocket() {
		if (mWSClient != null) {
			mWSClient.disconnect();
			mWSClient = null;
		}
		mWSClient = new WebSocketClient(URI.create(this.mBaseURL), this, null);
		mWSClient.connect();
	}

	private Handler getHandler() {
		return mHandler;
	}

	private void closeWebSoketConnection() {
		mWSClient.disconnect();
		// wsClient = null;
	}

	/**
	 * Bayeux connect
	 */
	private void fayeConnect() {
		try {
			// Fill connection format
			JSONObject json = new JSONObject();
			json.put(KEY_CHANNEL, CONNECT_CHANNEL);
			json.put(KEY_CLIENT_ID, mfayeClientID);
			json.put(KEY_CONN_TYPE, VALUE_CONN_TYPE);
			// Send request
			mWSClient.send(json.toString());
		} catch (JSONException ex) {
			// Failed
			mfayeListener.onError("Connection failed", ex);
		}
	}

	/**
	 * Bayeux handshake
	 */
	private void fayeHandShake() {
		try {
			// Supported connection types
			JSONArray connTypes = new JSONArray();
			connTypes.put("long-polling");
			connTypes.put("callback-polling");
			connTypes.put("iframe");
			connTypes.put("websocket");
			// Fill the handshake request format
			JSONObject json = new JSONObject();
			json.put(KEY_CHANNEL, HANDSHAKE_CHANNEL);
			json.put(KEY_VERSION, VALUE_VERSION);
			json.put(KEY_MIN_VERSION, VALUE_MIN_VERSION);
			json.put(KEY_SUP_CONN_TYPES, connTypes);
			// Send request by web socket
			mWSClient.send(json.toString());
		} catch (JSONException ex) {
			// Failed
			mfayeListener.onError("Handshake failed", ex);
		}
	}

	/**
	 * Bayeux disconnect
	 */
	private void fayeDisconnect() {
		if (mConnected) {
			try {
				// Fill disconnection format
				JSONObject json = new JSONObject();
				json.put(KEY_CHANNEL, DISCONNECT_CHANNEL);
				json.put(KEY_CLIENT_ID, mfayeClientID);
				// Send request
				mWSClient.send(json.toString());
			} catch (JSONException ex) {
				// Failed
				mfayeListener.onError("Disconnection failed", ex);
			}
		}
	}

	/**
	 * Bayeux subscribe
	 * 
	 * @param fayeSubChannel
	 *            : Channel to subscribe
	 */
	private void fayeSubscribe(final String fayeSubChannel) {
		try {
			// Fill Subscribe format
			JSONObject json = new JSONObject();
			json.put(KEY_CHANNEL, SUBSCRIBE_CHANNEL);
			json.put(KEY_CLIENT_ID, mfayeClientID);
			json.put(KEY_SUBSCRIPTION, fayeSubChannel);
			mWSClient.send(json.toString());

			this.mfayeSubChannel = fayeSubChannel;
		} catch (JSONException ex) {
			mfayeListener.onError("Subscribe failed", ex);
		}
	}

	/**
	 * Bayeux unsubscribe
	 * 
	 * @param fayeSubChannel
	 *            : Channel to unsubscribe
	 */
	private void fayeUnSubscribe(final String fayeSubChannel) {
		try {
			// Fill Subscribe format
			JSONObject json = new JSONObject();
			json.put(KEY_CHANNEL, UNSUBSCRIBE_CHANNEL);
			json.put(KEY_CLIENT_ID, mfayeClientID);
			json.put(KEY_SUBSCRIPTION, fayeSubChannel);
			mWSClient.send(json.toString());

			// Log.d(TAG, "Unsubcribed: " + fayeSubChannel);
		} catch (JSONException ex) {
			mfayeListener.onError("Unsubscribe failed", ex);
		}
	}

	/**
	 * Bayeux publish
	 * 
	 * @param message
	 *            : Message content
	 * @param subChannel
	 *            : Channel to publish
	 */
	private void fayePublishMessage(final String message,
			final String subChannel) {
		String channel = subChannel;
		long number = (new Date()).getTime();
		String messageId = String.format("msg_%d_%d", number, 1);
		try {
			JSONObject json = new JSONObject();
			json.put(KEY_CHANNEL, channel);
			json.put(KEY_CLIENT_ID, mfayeClientID);
			json.put(KEY_DATA, message);
			json.put(KEY_ID, messageId);
			mWSClient.send(json.toString());
		} catch (JSONException ex) {
			mfayeListener.onError("Sending message failed", ex);
		}
	}

	/**
	 * Send connect message to Faye
	 * 
	 * @param delay
	 *            : How long you want to delay (milisecond) :
	 */
	private void replyFayeServer(final int delay) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				fayeConnect();
			}
		});
		t.start();
	}

	/**
	 * Parse message from Faye server
	 * 
	 * @param messageArray
	 */
	private void parseFayeMessage(final JSONArray messageArray) {
		// Fetch message
		for (int i = 0; i < messageArray.length(); i++) {
			// Convert message to json format
			JSONObject jsonMessageElement = messageArray.optJSONObject(i);
			// Skip null message
			if (jsonMessageElement == null) {
				continue;
			}

			// Get channel
			String channel = jsonMessageElement.optString(KEY_CHANNEL);
			// If successful
			if (channel.equals(HANDSHAKE_CHANNEL)) {
				boolean isSuccessed = jsonMessageElement
						.optBoolean(KEY_SUCCESS);
				if (!isSuccessed) {// Unsuccess
					if (mfayeListener != null) {
						mfayeListener.onError(jsonMessageElement.toString(),
								null);
						// TODO: Rehandshake
						Log.d(TAG, "Handshake fail! Rehandshake...");
						fayeHandShake(); // Rehandshake
					}

					return;
				}
				// Success
				Log.d(TAG, "Handshake success!");
				mfayeClientID = jsonMessageElement.optString(KEY_CLIENT_ID);

				mConnected = true;
				if (mfayeListener != null) {
					mfayeListener.onConnected();
				}
				// Send connect message after success handshake
				fayeConnect();

				return;
			}

			if (channel.equals(CONNECT_CHANNEL)) {
				boolean isSuccessed = jsonMessageElement
						.optBoolean(KEY_SUCCESS);
				if (!isSuccessed) {
					mConnected = false;
					// TODO: Connect error
					fayeConnect();// Reconnect
					return;
				}

				// Success
				mConnected = true;
				// Send connect message to Faye after delay (milisecond)
				replyFayeServer(calDelay(jsonMessageElement));
				return;
			}

			if (channel.equals(SUBSCRIBE_CHANNEL)) {
				boolean isSuccessed = jsonMessageElement
						.optBoolean(KEY_SUCCESS);
				if (!isSuccessed) {
					// TODO: Subscribe error
					Log.d(TAG, "Subscribe fail! Resubscribe...");
					fayeSubscribe(mfayeSubChannel); //Resubscribe
					return;
				}

				// Success
				Log.d(TAG, "Subscribe success!");
				String subChannel = jsonMessageElement
						.optString(KEY_SUBSCRIPTION);
				if (mfayeListener != null) {
					mfayeListener.onSubscribed(subChannel);
				}

				return;
			}

			if (channel.equals(DISCONNECT_CHANNEL)) {
				boolean isSuccessed = jsonMessageElement
						.optBoolean(KEY_SUCCESS);
				if (!isSuccessed) {
					// TODO: Disconnect error
					return;
				}

				// Success
				Log.d(TAG, "Disconnect success!");
				mConnected = false;
				closeWebSoketConnection();
				if (mfayeListener != null) {
					mfayeListener.onDisconnected();
				}

				return;
			}

			if (channel.equals(UNSUBSCRIBE_CHANNEL)) {
				boolean isSuccessed = jsonMessageElement
						.optBoolean(KEY_SUCCESS);
				if (!isSuccessed) {
					// TODO: Unsubscribe error
				}

				// Success
				Log.d(TAG, "UnSubscribe success!");
				String subChannel = jsonMessageElement
						.optString(KEY_SUBSCRIPTION);
				if (mfayeListener != null) {
					mfayeSubChannel = null;
					mfayeListener.onUnSubscribed(subChannel);
				}

				return;
			}

			if (channel.equals("/message")) {
				boolean isSuccessed = jsonMessageElement
						.optBoolean(KEY_SUCCESS);
				if (isSuccessed) {// Skip success notify message
					Log.d(TAG,
							"Publish success: " + jsonMessageElement.toString());
					return;
				}
				JSONObject jsonMessage;
				try {
					String messageStr = jsonMessageElement.getString(KEY_DATA);
					//TODO: Skip echo message mine
					JSONObject tmp = new JSONObject(messageStr);
					String userName = tmp.getString("user");
					if(userName.equals(UserInfo.userName)) {//Skip message
						return;
					}
					if(messageStr!= "" && messageStr!=null) {
						mfayeListener.onMessage(messageStr);
					}
				} catch (JSONException e) {
					Log.d(TAG, e.getMessage());
				}
			}
			return;

		}
	}

	/**
	 * Cal delay time
	 * 
	 * @param jsonMessageElement
	 *            : Message element from fayeMessageArray
	 * @return : miliseconds
	 */
	private int calDelay(final JSONObject jsonMessageElement) {
		// Caculate delay time
		int delay = 5000;
		try {
			JSONObject advice = jsonMessageElement.getJSONObject("advice");
			String timeout = advice.optString("timeout");
			if (timeout != null) {
				delay = Integer.valueOf(timeout);
				delay /= 2;
			}

		} catch (JSONException e) {
			delay = 5000;
		}
		return delay;
	}

	// Test
	public void addEvent(final Object obj) {
		mEventExecutor.getHandler().sendMessage(
				mEventExecutor.getHandler().obtainMessage(1, obj));
	}

	//

	/**
	 * Event Queue Executor
	 * 
	 * @author hieu
	 * 
	 */
	class EventExecutor extends Thread {
		HandlerThread handlerThread;
		Handler handler;

		public EventExecutor() {
			handlerThread = new HandlerThread("Executor Handler Thread");
			handlerThread.start();
			handler = new Handler(handlerThread.getLooper()) {
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 1) { // queue new event
						synchronized (mFayeEventQueue) {
							mFayeEventQueue.add(msg.obj);
						}
						Log.d(TAG, mFayeEventQueue.toString());
					}
				}
			};
		}

		public Handler getHandler() {
			return handler;
		}

		@Override
		public void run() {
			while (true) {
				synchronized (mFayeEventQueue) {
					Object obj = mFayeEventQueue.poll();
					if (obj != null) {
						Log.d(TAG, "Executed: " + obj.toString());
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void startFayeClient() {
		new StartFayeClientTask().execute();
	}

	class StartFayeClientTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			openWebsocket();
		}

		@Override
		protected Void doInBackground(Void... params) {
			fayeHandShake();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	class SubcribeAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!mConnected) {
				new StartFayeClientTask().execute();
			}
		}

		@Override
		protected Void doInBackground(String... channel) {
			fayeSubscribe(channel[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

	}

	public void stopFayeClient() {
		if (mConnected) {
			fayeUnSubscribe(mfayeSubChannel);
			fayeDisconnect();
			closeWebSoketConnection();
			mConnected = false;
		}
	}

	
	
	//===============================================================
	// Begin: Test function
	public void openWS() {
		openWebsocket();
	}

	public void handShake() {
		fayeHandShake();
	}

	public void connect() {
		fayeConnect();
	}

	public void subscribe(String channel) {
		this.mfayeSubChannel = channel;
		fayeSubscribe(channel);
	}

	public void publish(String message, String channel) {
		fayePublishMessage(message, channel);
	}

	// Finish: Test function
	//==================================================================

	/**
	 * Interface function Call from UI thread to subscribe to a channel
	 * 
	 * @param channel
	 */
	public void subscribeChannel(final String channel) {
		this.mfayeSubChannel = channel;
		new SubcribeAsyncTask().execute(new String[] { channel });
	}

	/**
	 * Interface function Call from UI thread to unsubscribe to a channel
	 * 
	 * @param channel
	 */
	public void unSubscribeChannel(final String channel) {
		fayeUnSubscribe(channel);
	}

	// Begin: Websocket callback
	@Override
	public void onConnect() {
		// Do nothing here
	}

	@Override
	public void onMessage(final String message) {
		try {
			JSONArray messageArray = new JSONArray(message);
			parseFayeMessage(messageArray);
		} catch (JSONException e) {
			Log.d(TAG, "Invaild faye message array! " + e.getMessage());
		}

	}

	@Override
	public void onMessage(byte[] data) {
		// Do nothing here
	}

	@Override
	public void onDisconnect(int code, String reason) {
		mConnected = false;
	}

	@Override
	public void onError(Exception error) {
		Log.d(TAG, "Faye client error!" + error.getMessage());
	}
	// Finish: Websocket callback

}
