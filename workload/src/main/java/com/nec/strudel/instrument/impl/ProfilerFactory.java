/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
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

package com.nec.strudel.instrument.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.nec.strudel.instrument.GetOperationListener;
import com.nec.strudel.instrument.OperationListener;

public final class ProfilerFactory {
    private ProfilerFactory() {
        // utility class not instantiated
    }

    public static TimeProfiler timeProfiler(
            String name, ProfilerServiceImpl ps, Object stat) {
        OperationListener mon = getListener(name, stat);
        TimeProfiler tp = new TimeProfiler(name,
                ps.getMeasurementState(),
                mon);
        return tp;
    }

    public static TimeProfiler timeProfiler(
            String name, ProfilerServiceImpl ps) {
        TimeProfiler tp = new TimeProfiler(name,
                ps.getMeasurementState());
        return tp;
    }

    static OperationListener getListener(String name, Object stat) {
        Class<?> cls = stat.getClass();
        try {
            for (Method m : cls.getMethods()) {
                GetOperationListener lsn = m
                        .getAnnotation(GetOperationListener.class);
                if (lsn != null && name.equals(lsn.value())) {
                    return (OperationListener) m.invoke(stat);
                }
            }
        } catch (SecurityException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        throw new RuntimeException("failed to get listener for " + name);
    }
}
