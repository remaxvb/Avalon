package com.enclaveit.trm.view.subview;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.enclaveit.trm.R;
import com.enclaveit.trm.controller.MainController;
import com.enclaveit.trm.model.UserInfo;
import com.enclaveit.trm.ultils.fayeclient.FayeClient;
import com.enclaveit.trm.ultils.fayeclient.FayeListener;
import com.enclaveit.trm.view.BaseActivity;
import com.enclaveit.trm.view.IObserver;
import com.enclaveit.trm.view.ViewConstant;

public class ChatBox extends BaseActivity implements OnClickListener,
		IObserver, FayeListener {

	// The Controller
	private MainController mainController = MainController.getInstance();

	// Begin getting components part
	// define variables
	private Button btnSend;
	private EditText txtMessage;
	private ListView lstMessage;
	
	private Handler mHandler;
	
	private ArrayAdapter<String> chatDataAdapter;
	private ArrayList<String> chatDataArrayList;

	// init components
	private void initComponents() {
		btnSend = (Button) findViewById(R.id.buttonSend);
		txtMessage = (EditText) findViewById(R.id.textMessage);
		lstMessage = (ListView) findViewById(R.id.listChatBox);
		btnSend.setOnClickListener(this);
		
		chatDataArrayList = new ArrayList<String>();
		chatDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,chatDataArrayList);
		lstMessage.setAdapter(chatDataAdapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_box);

		initComponents();
		
		FayeClient.getInstance().setListener(this);
		FayeClient.getInstance().subscribeChannel("/message");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonSend:
			String message = txtMessage.getText().toString().trim();
			chatDataAdapter.add("Me: " + message);
			chatDataAdapter.notifyDataSetChanged();
			
			//For test
			JSONObject jsonMessage = new JSONObject();
			try {
				jsonMessage.putOpt("user", UserInfo.userName);
				jsonMessage.putOpt("message", message);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			FayeClient.getInstance().publish(jsonMessage.toString(), "/message");
			txtMessage.setText("");
			// :For test
			
			break;
		}
	}

	@Override
	public void updateView(Object data) {

	}

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSubscribed(String channel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnSubscribed(String channel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(String error, Exception e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(final String message) {
		lstMessage.post(new Runnable() {

			@Override
			public void run() {
				String msg = "";
				try {
					JSONObject jsonObject = new JSONObject(message);
					msg += jsonObject.getString("user") + ": ";
					msg += jsonObject.getString("message");

				} catch (JSONException e) {

				}
				chatDataAdapter.add(msg);
				chatDataAdapter.notifyDataSetChanged();
			}
		});

	}

}
