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

package com.nec.strudel.management.jmx;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.Nullable;
import javax.management.Descriptor;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;

import com.nec.strudel.management.ManagementService;
import com.nec.strudel.management.RegistrationException;
import com.nec.strudel.management.resource.Getter;
import com.nec.strudel.management.resource.Operation;
import com.nec.strudel.management.resource.ResourceAttribute;
import com.nec.strudel.management.resource.ResourceInfo;
import com.nec.strudel.management.resource.Setter;

public class JmxManagementService implements ManagementService {
    private final MBeanServer mbs;

    public JmxManagementService(MBeanServer mbs) {
        this.mbs = mbs;
    }

    public JmxManagementService() {
        this.mbs = ManagementFactory.getPlatformMBeanServer();
    }

    @Override
    public String register(Object managedObject) {
        ResourceInfo info = ResourceInfo.of(managedObject);
        ObjectName name = toName(info);
        try {
            mbs.registerMBean(
                    createMBean(managedObject, info),
                    name);
        } catch (InstanceAlreadyExistsException ex) {
            throw new RegistrationException(
                    "failed to register (instance exists):"
                            + name.getCanonicalName(),
                    ex);
        } catch (MBeanRegistrationException ex) {
            throw new RegistrationException(
                    "failed to register: "
                            + name.getCanonicalName(),
                    ex);
        } catch (NotCompliantMBeanException ex) {
            throw new RegistrationException(
                    "failed to register (invalid MBean)",
                    ex);
        }
        return name.getCanonicalName();
    }

    @Override
    public String registerName(Object managedObject) {
        ResourceInfo info = ResourceInfo.of(managedObject);
        ObjectName name = toName(info);
        return name.getCanonicalName();
    }

    @Override
    public boolean isRegistered(String objectName) {
        try {
            ObjectName name = new ObjectName(objectName);
            return mbs.isRegistered(name);
        } catch (MalformedObjectNameException ex) {
            return false;
        }
    }

    @Override
    public boolean unregister(Object managedObject) {
        return unregister(nameOf(managedObject));
    }

    @Override
    public boolean unregister(String objectName) {
        try {
            ObjectName name = new ObjectName(objectName);
            return unregister(name);
        } catch (MalformedObjectNameException ex) {
            throw new RegistrationException(
                    "failed to unregister (malformed): "
                            + objectName,
                    ex);
        }
    }

    public boolean unregister(ObjectName name) {
        try {
            mbs.unregisterMBean(name);
            return true;
        } catch (MBeanRegistrationException ex) {
            throw new RegistrationException(
                    "failed to unregister: "
                            + name.getCanonicalName(),
                    ex);
        } catch (InstanceNotFoundException ex) {
            return false;
        }
    }

    /**
     * <ul>
     * <li>domain name: if it is specified in the managed object annotation use
     * it. Otherwise, use the package name of this object.
     * <li>type: use the specified one if it is in the ManagedObject annotation.
     * Otherwise, use the class name of this object.
     * <li>name: use the result of a method call if the ResourceName annotation
     * is specified. Otherwise, omit this property.
     * <li>id: use the result of a method call if the ResourceId annotation is
     * specified. Otherwise, omit this property.
     * </ul>
     * 
     * @param managedObject
     * @return
     */
    public ObjectName nameOf(Object managedObject) {
        ResourceInfo info = ResourceInfo.of(managedObject);
        return toName(info);
    }

    ObjectName toName(ResourceInfo info) {
        Hashtable<String, String> tab = new Hashtable<String, String>();
        tab.put("type", info.getType());
        if (info.getId() != null) {
            tab.put("id", info.getId());
        }
        if (info.getName() != null) {
            tab.put("name", info.getName());
        }
        try {
            return new ObjectName(info.getDomain(), tab);
        } catch (MalformedObjectNameException ex) {
            throw new RegistrationException(
                    "failed to create ObjectName", ex);
        }
    }

    public ModelMBean createMBean(Object managedObject, ResourceInfo info) {
        try {
            RequiredModelMBean mb = new RequiredModelMBean();
            mb.setModelMBeanInfo(createMBeanInfo(managedObject,
                    info));
            mb.setManagedResource(managedObject, "ObjectReference");
            return mb;
        } catch (RuntimeOperationsException ex) {
            throw new RegistrationException(
                    "failed to create MBean", ex);
        } catch (InstanceNotFoundException ex) {
            throw new RegistrationException(
                    "failed to create MBean", ex);
        } catch (MBeanException ex) {
            throw new RegistrationException(
                    "failed to create MBean", ex);
        } catch (InvalidTargetObjectTypeException ex) {
            throw new RegistrationException(
                    "failed to create MBean", ex);
        }
    }

    public ModelMBeanInfo createMBeanInfo(Object managedObject,
            ResourceInfo info) {
        ModelMBeanInfo mbi = new ModelMBeanInfoSupport(
                managedObject.getClass().getName(),
                info.getDescription(),
                attributes(info),
                /**
                 * TODO do we need constructors?
                 */
                new ModelMBeanConstructorInfo[0],
                operations(managedObject),
                new ModelMBeanNotificationInfo[0]);
        return mbi;
    }

    ModelMBeanAttributeInfo[] attributes(ResourceInfo info) {
        ResourceAttribute[] attrs = info.getAttributes();
        ModelMBeanAttributeInfo[] mbais = new ModelMBeanAttributeInfo[attrs.length];
        try {
            for (int i = 0; i < attrs.length; i++) {
                ResourceAttribute attr = attrs[i];
                mbais[i] = new ModelMBeanAttributeInfo(
                        attr.getName(),
                        attr.getDescription(),
                        attr.getGetter(),
                        attr.getSetter());
                Descriptor desc = mbais[i].getDescriptor();
                Method getter = attr.getGetter();
                if (getter != null) {
                    desc.setField("getMethod", getter.getName());
                }
                Method setter = attr.getSetter();
                if (setter != null) {
                    desc.setField("setMethod", setter.getName());
                }
                mbais[i].setDescriptor(desc);
            }
        } catch (IntrospectionException ex) {
            throw new RegistrationException(
                    "failed to get atrributes", ex);
        }
        return mbais;
    }

    ModelMBeanOperationInfo[] operations(Object managedObject) {
        List<ModelMBeanOperationInfo> infos = new ArrayList<ModelMBeanOperationInfo>();
        for (Method m : managedObject.getClass().getMethods()) {
            ModelMBeanOperationInfo mboi = operationOf(m);
            if (mboi != null) {
                infos.add(mboi);
            }
        }
        return infos.toArray(new ModelMBeanOperationInfo[infos.size()]);
    }

    @Nullable
    ModelMBeanOperationInfo operationOf(Method method) {
        Operation op = method.getAnnotation(Operation.class);
        if (op != null) {
            return new ModelMBeanOperationInfo("", method);
        }
        Getter getter = method.getAnnotation(Getter.class);
        if (getter != null) {
            return operationOf(method, "getter");
        }
        Setter setter = method.getAnnotation(Setter.class);
        if (setter != null) {
            return operationOf(method, "setter");
        }
        return null;
    }

    ModelMBeanOperationInfo operationOf(Method method, String role) {
        Descriptor dsc = new DescriptorSupport(
                "name=" + method.getName(),
                "descriptorType=operation",
                "role=" + role);
        return new ModelMBeanOperationInfo("", method, dsc);
    }
}
