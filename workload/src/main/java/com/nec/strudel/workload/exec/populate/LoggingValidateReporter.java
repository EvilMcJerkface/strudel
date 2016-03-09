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

package com.nec.strudel.workload.exec.populate;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.nec.strudel.workload.api.ValidateReporter;

public class LoggingValidateReporter implements ValidateReporter {
    private final Logger logger;
    private boolean delayedCheck;
    private final List<String> warns;
    private final int reportMax;

    public LoggingValidateReporter(int reportMax, Logger logger) {
        this.logger = logger;
        this.reportMax = reportMax;
        warns = new ArrayList<String>(reportMax);
    }

    public void setDelayedCheck(boolean doubleCheck) {
        this.delayedCheck = doubleCheck;
    }

    @Override
    public void error(String message) {
        if (delayedCheck) {
            warn("delayed double check: " + message);
        } else {
            warn(message);
        }
    }

    private void warn(String message) {
        if (warns.size() < reportMax) {
            warns.add(message);
        }
        logger.warn(message);
    }

    public boolean hasWarn() {
        return !warns.isEmpty();
    }

    public List<String> getWarns() {
        return warns;
    }
}