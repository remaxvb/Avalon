package com.enclaveit.trm.controller.subcontroller;

import java.util.ArrayList;

import android.content.Intent;

import com.enclaveit.trm.controller.ISubject;
import com.enclaveit.trm.view.BaseActivity;
import com.enclaveit.trm.view.IObserver;
import com.enclaveit.trm.view.ViewConstant;
import com.enclaveit.trm.view.subview.ChatBox;
import com.enclaveit.trm.view.subview.SignIn;
import com.enclaveit.trm.view.subview.SignUp;

public class ActivityController implements ISubject {

	public ActivityController() {
//		ServiceCaller.getInstance().setServiceCallBack(this);
	}

	/* Begin Observer part */
	private ArrayList<IObserver> observers = new ArrayList<IObserver>();

	/* End Observer part */

	/* Begin Changing Activity part */

	public void changeActivity(int code, BaseActivity base) {
		switch (code) {
		// Switch to Sign In View
		case ViewConstant.CHANGE_TO_SIGNIN: {
			Intent intent = new Intent(base, SignIn.class);
			base.startActivity(intent);
		}
			break;
		// Switch to Sign Up View
		case ViewConstant.CHANGE_TO_SIGNUP: {
			Intent intent = new Intent(base, SignUp.class);
			base.startActivity(intent);
		}
			break;
		// Switch to Chat Box View
		case ViewConstant.CHANGE_TO_CHATBOX: {
			Intent intent = new Intent(base, ChatBox.class);
			base.startActivity(intent);
		}
			break;
		}
	}

	/* End Changing Activity part */

	@Override
	public void registerViewObserver(IObserver view) {
		observers.add(view);

	}

	@Override
	public void notifyViewObserver(Object data) {
		for (int i = 0; i < observers.size(); i++) {
			observers.get(i).updateView(data);
		}
	}

	@Override
	public void removeViewObserver(IObserver view) {
		observers.remove(view);

	}

//	@Override
//	public void responseData(Object data) {
//		notifyViewObserver(data);
//	}
}
