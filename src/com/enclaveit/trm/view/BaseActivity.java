package com.enclaveit.trm.view;

import android.app.Activity;
import android.os.Bundle;
import com.enclaveit.trm.controller.MainController;

/**
 * All activity must extends this class
 * @author thanh.t.nguyen
 *
 */
public class BaseActivity extends Activity implements IObserver {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initObserver();
	}

	@Override
	protected void onDestroy() {
		removeObserver();
		super.onDestroy();
	}

	// Register to View Array
	private void initObserver() {
		MainController.getInstance().getActivityController().registerViewObserver(this);
	}

	// Remove from View Array
	private void removeObserver() {
		MainController.getInstance().getActivityController().removeViewObserver(this);
	}

	@Override
	public void updateView(Object data) {
		// TODO Auto-generated method stub

	}

}
