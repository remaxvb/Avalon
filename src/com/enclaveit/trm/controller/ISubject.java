package com.enclaveit.trm.controller;

import com.enclaveit.trm.view.IObserver;

public interface ISubject {
	public void registerViewObserver(IObserver view);
	public void notifyViewObserver(Object data);
	public void removeViewObserver(IObserver view);
}
