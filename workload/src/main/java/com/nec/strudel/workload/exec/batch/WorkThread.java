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

package com.nec.strudel.workload.exec.batch;

import com.nec.strudel.metrics.Report;

/**
 * A class that emulates one thread of a workload.
 * 
 * @author tatemura
 *
 */
public interface WorkThread extends Runnable {
    /**
     * checks if work is running.
     * 
     * @return false if work has not been started, run() is done, or stop()
     *         request is accepted.
     */
    boolean isRunning();

    /**
     * checks if work is done
     * 
     * @return true if run() has been executed and done.
     */
    boolean isDone();

    boolean isSuccessful();

    int getId();

    void stop();

    Report getReport();
}
