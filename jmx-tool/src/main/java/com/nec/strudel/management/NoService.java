package com.nec.strudel.management;

public class NoService implements ManagementService {

	public NoService() {
	}

	@Override
	public String register(Object managedObject) {
		return "";
	}

	@Override
	public boolean unregister(Object managedObject) {
		return false;
	}

	@Override
	public boolean unregister(String objectName) {
		return false;
	}
	@Override
	public String registerName(Object managedObject) {
		return "";
	}
	@Override
	public boolean isRegistered(String objectName) {
		return false;
	}

}
