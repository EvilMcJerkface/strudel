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

package com.nec.strudel.bench.micro.populate.entity;

import java.util.List;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.populate.SubmitSet;
import com.nec.strudel.bench.micro.populate.base.AbstractPopulateSubmission;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.workload.api.Populator;

public class PopulateSubmission extends AbstractPopulateSubmission<EntityDB>
        implements Populator<EntityDB, SubmitSet> {

    @Override
    public void process(EntityDB db, SubmitSet param) {
        int userId = param.getSender();
        for (int i = 0; i < param.size(); i++) {
            int receiverId = param.getReceiver(i);
            Submission sub = new Submission(receiverId, userId);
            sub.setContent(param.getContent(i));
            db.create(sub);
        }
    }

    @Override
    protected List<Submission> getSubmissionsBySender(EntityDB db,
            int senderId) {
        return db.getEntitiesByIndex(Submission.class,
                "senderId", senderId);
    }

    @Override
    protected List<Submission> getSubmissionsByReceiver(EntityDB db,
            int receiverId) {
        return db.getEntitiesByIndex(
                Submission.class,
                "receiverId", receiverId);
    }

}
