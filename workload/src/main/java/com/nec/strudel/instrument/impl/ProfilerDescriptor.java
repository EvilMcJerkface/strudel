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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nec.strudel.instrument.Profiler;

public class ProfilerDescriptor {
    public static ProfilerDescriptor of(Class<?> profilerClass) {
        return new ProfilerDescriptor(profilerClass);
    }

    private final Class<?> profilerClass;
    private final List<InstrumentDescriptor> instruments = new ArrayList<InstrumentDescriptor>();
    private final Map<String, Method> instrumentSetters = new HashMap<String, Method>();
    private final Map<String, Method> instrumentGetters = new HashMap<String, Method>();

    public ProfilerDescriptor(Class<?> profilerClass) {
        this.profilerClass = profilerClass;
        createInstruments(profilerClass, instruments, instrumentSetters,
                instrumentGetters);
    }

    public Collection<InstrumentDescriptor> getInstruments() {
        return instruments;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(ProfilerServiceImpl ps, Object stat) {
        try {
            Object prof = this.profilerClass.newInstance();
            setInstruments(prof, ps, stat);
            return (T) prof;
        } catch (InstantiationException ex) {
            throw new RuntimeException("failed to create profiler", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("failed to create profiler", ex);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T create(ProfilerServiceImpl ps) {
        try {
            Object prof = this.profilerClass.newInstance();
            setInstruments(prof, ps);
            return (T) prof;
        } catch (InstantiationException ex) {
            throw new RuntimeException("failed to create profiler", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("failed to create profiler", ex);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T createDisabled() {
        try {
            Object prof = this.profilerClass.newInstance();
            setDisabled(prof);
            return (T) prof;
        } catch (InstantiationException ex) {
            throw new RuntimeException("failed to create profiler", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("failed to create profiler", ex);
        }
    }

    public Profiler extractInstrument(Object profiler) {
        List<Profiler> profs = new ArrayList<Profiler>();
        for (InstrumentDescriptor instr : getInstruments()) {
            Profiler prof = getInstrument(instr.getName(), profiler);
            profs.add(prof);
        }
        return profs.size() == 1 ? profs.get(0) : new CompoundProfiler(profs);
    }

    public void setInstruments(Object profiler, ProfilerServiceImpl ps,
            Object stat) {
        for (InstrumentDescriptor instr : getInstruments()) {
            Profiler in = instr.createInstrument(ps, stat);
            setInstrument(instr.getName(), profiler, in);
        }
    }

    public void setInstruments(Object profiler, ProfilerServiceImpl ps) {
        for (InstrumentDescriptor instr : getInstruments()) {
            Profiler in = instr.createInstrument(ps);
            setInstrument(instr.getName(), profiler, in);
        }
    }

    public void setDisabled(Object profiler) {
        for (InstrumentDescriptor instr : getInstruments()) {
            Profiler in = instr.createDisabled();
            setInstrument(instr.getName(), profiler, in);
        }
    }

    private static void createInstruments(Class<?> profilerClass,
            List<InstrumentDescriptor> instruments,
            Map<String, Method> instrumentSetters,
            Map<String, Method> instrumentGetters) {
        Class<?> sup = profilerClass.getSuperclass();
        if (sup != null) {
            createInstruments(sup, instruments, instrumentSetters, instrumentGetters);
        }
        for (Field f : profilerClass.getDeclaredFields()) {
            InstrumentDescriptor instr = InstrumentDescriptor.of(f);
            if (instr != null) {
                instruments.add(instr);
                Method setter = findSetter(profilerClass, f.getName(), f.getType());
                if (setter != null) {
                    instrumentSetters.put(instr.getName(), setter);
                }
                Method getter = findGetter(profilerClass, f.getName(), f.getType());
                if (getter != null) {
                    instrumentGetters.put(instr.getName(), getter);
                }
            }
        }

    }

    private static Method findSetter(Class<?> prof, String name,
            Class<?> type) {
        String methodName = "set" + name.substring(0, 1).toUpperCase()
                + name.substring(1);
        try {
            return prof.getMethod(methodName, type);
        } catch (NoSuchMethodException ex) {
            return null;
        } catch (SecurityException ex) {
            return null;
        }
    }

    private static Method findGetter(Class<?> prof, String name,
            Class<?> type) {
        String methodName = "get" + name.substring(0, 1).toUpperCase()
                + name.substring(1);
        try {
            Method method = prof.getMethod(methodName);
            if (method.getReturnType().equals(type)) {
                return method;
            }
        } catch (NoSuchMethodException ex) {
            // ignore and return null
        } catch (SecurityException ex) {
            // ignore and return null
        }
        return null;
    }

    private void setInstrument(String name, Object prof, Object instr) {
        Method setter = instrumentSetters.get(name);
        try {
            setter.invoke(prof, instr);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("failed to set instrument:" + name, ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("failed to set instrument:" + name, ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("failed to set instrument:" + name, ex);
        }

    }

    private Profiler getInstrument(String name, Object prof) {
        Method getter = instrumentGetters.get(name);
        try {
            return (Profiler) getter.invoke(prof);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("failed to get instrument:" + name, ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("failed to get instrument:" + name, ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("failed to get instrument:" + name, ex);
        }
    }
}