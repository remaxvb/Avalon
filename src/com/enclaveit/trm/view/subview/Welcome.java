package com.enclaveit.trm.view.subview;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.enclaveit.trm.R;
import com.enclaveit.trm.controller.MainController;
import com.enclaveit.trm.view.BaseActivity;
import com.enclaveit.trm.view.ViewConstant;

public class Welcome extends BaseActivity implements OnClickListener {
	private final String TAG = SignIn.class.getSimpleName();
	// The Controller
	private MainController mainController = MainController.getInstance();

	// Define all components
	private Button btnSignIn;
	private Button btnSignUp;

	/**
	 * Init all component
	 */
	private void initComponents() {
		try {
			btnSignIn = (Button) findViewById(R.id.btn_sign_in_welcome);
			btnSignUp = (Button) findViewById(R.id.btn_sign_up_welcome);
			btnSignIn.setOnClickListener(this);
			btnSignUp.setOnClickListener(this);
		} catch (Exception e) {
			Log.e(TAG, "Activity is not loaded!");
		}
	}

	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.welcome);
		initComponents();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sign_in_welcome: {
			mainController.changeActivity(ViewConstant.CHANGE_TO_SIGNIN, this);
		}

			break;
		case R.id.btn_sign_up_welcome: {
			mainController.changeActivity(ViewConstant.CHANGE_TO_SIGNUP, this);
		}
			break;
		}
	}

}
