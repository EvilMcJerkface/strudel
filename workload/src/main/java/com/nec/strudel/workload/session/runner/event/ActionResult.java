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

package com.nec.strudel.workload.session.runner.event;

import javax.annotation.Nullable;

import com.nec.strudel.session.Result;
import com.nec.strudel.workload.session.SessionContainer;

public class ActionResult<T> {
    private final SessionContainer<T> sc;
    private final SessionContext<T> ctxt;
    private final Result res;

    public ActionResult(SessionContainer<T> sc, SessionContext<T> ctxt,
            Result res) {
        this.sc = sc;
        this.ctxt = ctxt;
        this.res = res;
    }

    @Nullable
    public SessionEvent<T> nextAction() {
        if (sc.isActive()) {
            return new SessionEvent<T>(sc, ctxt);
        }
        return null;
    }

    public SessionEvent<T> newSession(SessionContainer<T> sc) {
        ctxt.profiler().newSession();
        return new SessionEvent<T>(sc, ctxt);
    }

    public Result getResult() {
        return res;
    }

}
