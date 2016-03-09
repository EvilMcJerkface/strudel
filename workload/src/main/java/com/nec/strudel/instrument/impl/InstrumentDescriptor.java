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

import javax.annotation.Nullable;

import com.nec.strudel.instrument.Instrument;
import com.nec.strudel.instrument.Profiler;

public class InstrumentDescriptor {
    public enum Type {
        TIME, COUNT
    }

    private final String name;
    private final Type type;

    public InstrumentDescriptor(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String toString() {
        return name + ":" + type;
    }

    public Profiler createInstrument(ProfilerServiceImpl ps, Object stat) {
        if (type == Type.TIME) {
            return ProfilerFactory.timeProfiler(name, ps, stat);
        } else {
            return new CountingProfiler(name,
                    ps.getMeasurementState());
        }
    }

    public Profiler createInstrument(ProfilerServiceImpl ps) {
        if (type == Type.TIME) {
            return ProfilerFactory.timeProfiler(name, ps);
        } else {
            return new CountingProfiler(name,
                    ps.getMeasurementState());
        }
    }

    public Profiler createDisabled() {
        if (type == Type.TIME) {
            return TimeProfiler.NO_TIME;
        } else {
            return CountingProfiler.NO_COUNT;
        }
    }

    @Nullable
    public static InstrumentDescriptor of(Field field) {
        Instrument instr =
                field.getAnnotation(Instrument.class);
        if (instr != null) {
            String name = instr.name().isEmpty() ? field.getName() : instr.name();
            return new InstrumentDescriptor(name, typeOf(field));
        }
        return null;
    }

    private static Type typeOf(Field field) {
        Class<?> cls = field.getType();
        if (cls.isAssignableFrom(TimeProfiler.class)) {
            return Type.TIME;
        }
        if (cls.isAssignableFrom(CountingProfiler.class)) {
            return Type.COUNT;
        }
        throw new IllegalArgumentException("missing type on " + field.getName());
    }
}