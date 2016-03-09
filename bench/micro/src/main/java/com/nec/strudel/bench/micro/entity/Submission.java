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

package com.nec.strudel.bench.micro.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

import com.nec.strudel.entity.EntityUtil;
import com.nec.strudel.entity.GroupId;
import com.nec.strudel.entity.On;
import com.nec.strudel.entity.Indexes;

/**
 * An entity that represents a content that is read by its owner and written by
 * others.
 * 
 * @author tatemura
 *
 */
@Entity
@Indexes({
        @On(property = "pairId", auto = true),
        @On(property = "receiverId", name = "submission_index"),
        @On(property = "senderId", name = "submission_sender_index")
})
@Table(indexes = {
        @Index(columnList = "RECEIVERID"),
        @Index(columnList = "SENDERID") })
@IdClass(SubmissionId.class)
public class Submission {
    @GroupId
    @Id
    private int receiverId;
    @Id
    private int senderId;
    @Id
    @GeneratedValue
    private int submitNo;
    private String content;

    public Submission() {
    }

    public Submission(int receiverId, int senderId, int submitNo) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.submitNo = submitNo;
    }

    public Submission(int receiverId, int senderId) {
        this.receiverId = receiverId;
        this.senderId = senderId;
    }

    public Submission(PairId pair, int submitNo) {
        this.receiverId = pair.getReceiverId();
        this.senderId = pair.getSenderId();
        this.submitNo = submitNo;
    }

    public SubmissionId getSubmissionId() {
        return new SubmissionId(receiverId, senderId, submitNo);
    }

    public PairId getPairId() {
        return new PairId(receiverId, senderId);
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getSubmitNo() {
        return submitNo;
    }

    public void setSubmitNo(int submitNo) {
        this.submitNo = submitNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int hashCode() {
        return EntityUtil.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EntityUtil.equals(this, obj);
    }

    @Override
    public String toString() {
        return EntityUtil.toString(this);
    }

}
