package com.nec.strudel.management;

public interface ManagementService {

	/**
	 * @param managedObject
	 * @return the name of the object
	 * given by registerName(managedObject)
	 */
	String register(Object managedObject);
	/**
	 * Does the same as unregister(registerName(managedObject))
	 * @param managedObject
	 * @return false if there is no such object
	 *  registered
	 */
	boolean unregister(Object managedObject);
	/**
	 * unregister the named object
	 * @param objectName
	 * @return false if there is no such
	 * object registered.
	 */
	boolean unregister(String objectName);
	String registerName(Object managedObject);
	boolean isRegistered(String objectName);
}
