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

package com.nec.strudel.bench.micro.populate.base;

import java.util.List;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.params.DataParam;
import com.nec.strudel.bench.micro.populate.SubmitSet;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.ValidateReporter;

public abstract class AbstractPopulateSubmission<T>
        implements Populator<T, SubmitSet> {

    @Override
    public String getName() {
        return "Submission";
    }

    @Override
    public SubmitSet createParameter(PopulateParam param) {
        int size = param.getInt(DataParam.SUBMISSIONS_PER_USER);
        int length = param.getInt(DataParam.CONTENT_LENGTH);
        int minUid = param.getInt(DataParam.MIN_USER_ID);
        int userNum = param.getInt(DataParam.USER_NUM);
        RandomSelector<Integer> receiver = RandomSelector.create(minUid,
                minUid + userNum);
        RandomSelector<String> content = RandomSelector
                .createAlphaString(length);

        return SubmitSet.create(param.getId(), size,
                receiver, content, param.getRandom());
    }

    @Override
    public boolean validate(T db, SubmitSet param,
            ValidateReporter reporter) {
        int userId = param.getSender();
        List<Submission> subs = getSubmissionsBySender(db, userId);
        if (subs.size() != param.size()) {
            reporter.error("# of submission for sender ("
                    + userId + ")"
                    + " must be " + param.size()
                    + " but " + subs.size());
            return false;
        }
        for (int i = 0; i < param.size(); i++) {
            int receiver = param.getReceiver(i);
            String content = param.getContent(i);
            boolean idFound = false;
            boolean found = false;
            for (Submission s : getSubmissionsByReceiver(db, receiver)) {
                if (s.getSenderId() == userId) {
                    idFound = true;
                    if (s.getContent().equals(content)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                if (idFound) {
                    reporter.error("submission from "
                            + userId
                            + " to " + receiver
                            + " found in "
                            + " the index but"
                            + " content is different");
                } else {
                    reporter.error("submission from "
                            + userId
                            + " to " + receiver
                            + " not found in "
                            + " the index");
                }
                return false;
            }
        }
        return true;
    }

    protected abstract List<Submission> getSubmissionsBySender(T db,
            int senderId);

    protected abstract List<Submission> getSubmissionsByReceiver(T db,
            int receiverId);
}