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

public class SharedId {
    private int setId;
    private int itemNo;

    public SharedId() {
    }

    public SharedId(int setId, int itemNo) {
        this.setId = setId;
        this.itemNo = itemNo;
    }

    public int getSetId() {
        return setId;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }

    public int getItemNo() {
        return itemNo;
    }

    public void setItemNo(int itemNo) {
        this.itemNo = itemNo;
    }

    private static final int HASH_BASE = 31;

    @Override
    public int hashCode() {
        return setId * HASH_BASE + itemNo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SharedId) {
            SharedId sid = (SharedId) obj;
            return this.setId == sid.setId && this.itemNo == sid.itemNo;
        }
        return false;
    }
}
