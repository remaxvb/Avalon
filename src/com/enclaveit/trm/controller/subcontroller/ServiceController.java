package com.enclaveit.trm.controller.subcontroller;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.enclaveit.trm.ultils.ServiceCaller;
import com.enclaveit.trm.ultils.ServiceCaller.ServiceCallback;

public class ServiceController implements ServiceCallback {

	ServiceControllerCallback serviceControllerCallback;

	public ServiceController() {
		// do nothing
	}

	public void setServiceControllerCallback(ServiceControllerCallback scc) {
		this.serviceControllerCallback = scc;

		ServiceCaller.getInstance().setServiceCallback(this);
	}

	@Override
	public void onLoginCallback(Object data) {
		JSONObject result = (JSONObject) data;
		if(result == null) {
			serviceControllerCallback.onLoginFail("Cannot connect to server, try again!");
			return;
		}
		// Check login result here
		try {
			boolean success = result.getBoolean("success");
			if (success) {
				serviceControllerCallback.onLoginSuccess();
				return;
			}
			
			//Fail
			serviceControllerCallback.onLoginFail(result.getString("message"));
		} catch (JSONException e) {
			serviceControllerCallback.onLoginFail(data.toString());
		}
		
	}

	public void signIn(Activity base, String userName, String userPass) {
		ServiceCaller.getInstance().callSignIn(base, true, userName, userPass);
	}

	public interface ServiceControllerCallback {
		public void onLoginSuccess();

		public void onLoginFail(String message);
	}

}
