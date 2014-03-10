package com.enclaveit.trm.view.subview;

import org.json.JSONObject;

import com.enclaveit.trm.R;
import com.enclaveit.trm.controller.MainController;
import com.enclaveit.trm.view.BaseActivity;
import com.enclaveit.trm.view.IObserver;
import com.enclaveit.trm.view.ViewConstant;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SignUp extends BaseActivity implements OnClickListener, IObserver {

	// The Controller
	private MainController mainController = MainController.getInstance();
	
	// Begin getting components part
	// define variables
	private Button btnSignUp;
	private Button btnBackSignIn;
	private EditText txtUserName;
	private EditText txtPassword;
	private EditText txtRePassword;
	private EditText txtFullName;
	private EditText txtEmail;

	// init components
	private void initComponents() {
		try {
			btnSignUp = (Button) findViewById(R.id.buttonSignUp);
			btnBackSignIn = (Button) findViewById(R.id.buttonBackToSignIn);
			txtUserName = (EditText) findViewById(R.id.textSUUserName);
			txtPassword = (EditText) findViewById(R.id.textSUPassword);
			txtRePassword = (EditText) findViewById(R.id.textSURePassword);
			txtFullName = (EditText) findViewById(R.id.textSUFullName);
			txtEmail = (EditText) findViewById(R.id.textSUEmail);
			btnSignUp.setOnClickListener(this);
			btnBackSignIn.setOnClickListener(this);
		} catch (Exception e) {
			Log.e("Activity", "Activity is not loaded!");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);
		initComponents();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonSignUp: {
			mainController.changeActivity(ViewConstant.CHANGE_TO_SIGNIN, this);
		}
			break;
		case R.id.buttonBackToSignIn: {
			mainController.changeActivity(ViewConstant.CHANGE_TO_SIGNIN, this);
		}
			break;
		}
	}
	
	@Override
	public void updateView(Object data) {
		
	}

}
