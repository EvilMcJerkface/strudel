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
 * An entity that represents a content that
 * is published by one user and seen by other
 * users. Only the owner has write access to a post.
 * @author tatemura
 *
 */
@Entity
@Indexes({
	@On(property = "userId", auto = true,
			name = "post_index")
})
@Table(indexes={@Index(columnList="USERID")})
@IdClass(ItemId.class)
public class Post {
	@GroupId @Id private int userId;
	@Id @GeneratedValue private int itemNo;
	private String content;

	public Post() {
	}
	public Post(int userId, int itemNo) {
		this.userId = userId;
		this.itemNo = itemNo;
	}
	public Post(int userId) {
		this.userId = userId;
	}

	public ItemId getItemId() {
		return new ItemId(userId, itemNo);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getItemNo() {
		return itemNo;
	}

	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
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
