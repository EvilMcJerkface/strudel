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
 * An entity that represents a content
 * shared by many users. There is no particular
 * user who owns this content. Anyone can
 * read and write.
 * @author tatemura
 *
 */
@Entity
@Indexes({
	@On(property = "setId", auto = true,
			name = "shared_index")

})
@Table(indexes={@Index(columnList="SETID")})
@IdClass(SharedId.class)
public class Shared {
	@GroupId @Id private int setId;
	@Id @GeneratedValue private int itemNo;
	private String content;

	public Shared() {
	}
	public Shared(int setId, int itemNo) {
		this.setId = setId;
		this.itemNo = itemNo;
	}
	public Shared(int setId) {
		this.setId = setId;
	}

	public SharedId getSharedId() {
		return new SharedId(setId, itemNo);
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
