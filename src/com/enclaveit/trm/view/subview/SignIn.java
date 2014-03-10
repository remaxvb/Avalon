package com.enclaveit.trm.view.subview;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.enclaveit.trm.R;
import com.enclaveit.trm.controller.MainController;
import com.enclaveit.trm.controller.subcontroller.ServiceController;
import com.enclaveit.trm.controller.subcontroller.ServiceController.ServiceControllerCallback;
import com.enclaveit.trm.model.UserInfo;
import com.enclaveit.trm.ultils.ServiceCaller;
import com.enclaveit.trm.ultils.fayeclient.FayeClient;
import com.enclaveit.trm.view.BaseActivity;
import com.enclaveit.trm.view.IObserver;
import com.enclaveit.trm.view.ViewConstant;

public class SignIn extends BaseActivity implements OnClickListener, IObserver,
		ServiceControllerCallback {

	private final String TAG = SignIn.class.getSimpleName();
	// The Controller
	private MainController mainController = MainController.getInstance();

	// Begin getting components part
	// define variables
	private Button btnSignIn;
	private TextView lblMoveToSignUp;
	private EditText txtUserName;
	private EditText txtPassword;

	// init components
	private void initComponents() {
		try {
			btnSignIn = (Button) findViewById(R.id.buttonSignIn);
			lblMoveToSignUp = (TextView) findViewById(R.id.buttonMoveSignUp);
			txtUserName = (EditText) findViewById(R.id.textSIUserName);
			txtPassword = (EditText) findViewById(R.id.textSIPassword);
			btnSignIn.setOnClickListener(this);
			lblMoveToSignUp.setOnClickListener(this);
		} catch (Exception e) {
			Log.e(TAG, "Activity is not loaded!");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in);
		initComponents();

		FayeClient.getInstance().setBaseURL("ws://192.168.56.1:9292/bayeux");
		FayeClient.getInstance().startFayeClient();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonSignIn: {
			MainController.getInstance().getServiceController().setServiceControllerCallback(this);
			
			UserInfo.userName = txtUserName.getText().toString().trim();
			UserInfo.password = txtPassword.getText().toString().trim();

			MainController.getInstance().signIn(this, UserInfo.userName, UserInfo.password);
		}
			break;
		case R.id.buttonMoveSignUp: {
			mainController.changeActivity(ViewConstant.CHANGE_TO_SIGNUP, this);
		}
			break;
		}
	}

	@Override
	public void updateView(Object data) {
		// JSONObject json = (JSONObject) data;
		// try {
		// boolean result = json.getBoolean("success");
		// if (result) {
		// MainController.getInstance().changeActivity(
		// ViewConstant.CHANGE_TO_CHATBOX, this);
		// } else {
		// Toast.makeText(this, "Your account is not corrected!",
		// Toast.LENGTH_SHORT).show();
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void onLoginSuccess() {
		MainController.getInstance().changeActivity(ViewConstant.CHANGE_TO_CHATBOX, this);
	}

	@Override
	public void onLoginFail(String message) {
		Toast.makeText(this, message,
				Toast.LENGTH_LONG).show();

	}

}
