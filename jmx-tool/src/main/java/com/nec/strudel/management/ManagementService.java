/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
