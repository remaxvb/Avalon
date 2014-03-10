package com.enclaveit.trm.controller;

import android.app.Activity;

import com.enclaveit.trm.controller.subcontroller.ActivityController;
import com.enclaveit.trm.controller.subcontroller.ServiceController;
import com.enclaveit.trm.view.BaseActivity;

public class MainController {
	/* Begin calling sub-controller */
	private ActivityController activityController;
	public ServiceController getServiceController() {
		return serviceController;
	}

	private ServiceController serviceController;

	/* End calling sub-controller */

	public ActivityController getActivityController() {
		return activityController;
	}

	/* Begin Singleton part */
	private static MainController instance = null;

	private MainController() {
		activityController = new ActivityController();
		serviceController = new ServiceController();
	}

	
	
	public synchronized static MainController getInstance() {
		if (instance == null) {
			instance = new MainController();
		}
		return instance;
	}

	/* End Singleton part */

	/* Begin Observer part */

	/* End Observer part */

	/* Begin Changing Activity part */
	public void changeActivity(int code, BaseActivity base) {
		activityController.changeActivity(code, base);
	}

		/* End Changing Activity part */

	/* Begin Requesting part */
	public void signIn(Activity base, String userName, String userPass) {
		serviceController.signIn(base, userName, userPass);
	}
	/* End Requesting part */
}
