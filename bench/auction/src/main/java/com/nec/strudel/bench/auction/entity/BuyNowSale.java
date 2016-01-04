package com.nec.strudel.bench.auction.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

import com.nec.strudel.entity.EntityUtil;
import com.nec.strudel.entity.Group;
import com.nec.strudel.entity.GroupId;
import com.nec.strudel.entity.GroupIdClass;
import com.nec.strudel.entity.On;
import com.nec.strudel.entity.Indexes;

@Group(parent = SaleItem.class)
@Entity
@Indexes({
	@On(property = "itemId", auto = true,
			name = "sale_buynow_idx"),
	@On(property = "buyerId",
			name = "buyer_sale_idx")
})
@Table(indexes={
		@Index(columnList="SELLERID,ITEMNO"),
		@Index(columnList="BUYERID")})
@GroupIdClass(ItemId.class)
@IdClass(SaleId.class)
public class BuyNowSale {

	@GroupId @Id private int sellerId;
	@GroupId @Id private int itemNo;
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int bnsNo;
	private int qnty;
	private long bnsDate;
	private int buyerId;


	public BuyNowSale(ItemId itemId, int bnsNo) {
		this.sellerId = itemId.getSellerId();
		this.itemNo = itemId.getItemNo();
		this.bnsNo = bnsNo;
	}
	public BuyNowSale(ItemId itemId) {
		this.sellerId = itemId.getSellerId();
		this.itemNo = itemId.getItemNo();
	}

	public BuyNowSale(SaleId id) {
		ItemId itemId = id.getItemId();
		this.sellerId = itemId.getSellerId();
		this.itemNo = itemId.getItemNo();
		this.bnsNo = id.getBnsNo();
	}
	public BuyNowSale() {
	}

	public SaleId getId() {
		return new SaleId(getItemId(), bnsNo);
	}


	public int getSellerId() {
		return sellerId;
	}

	public void setSellerId(int sellerId) {
		this.sellerId = sellerId;
	}

	public int getItemNo() {
		return itemNo;
	}

	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}

	public int getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(int buyerId) {
		this.buyerId = buyerId;
	}

	public ItemId getItemId() {
		return new ItemId(sellerId, itemNo);
	}


	public int getBnsNo() {
		return bnsNo;
	}
	public void setBnsNo(int bnsNo) {
		this.bnsNo = bnsNo;
	}

	public int getQnty() {
		return qnty;
	}

	public void setQnty(int qnty) {
		this.qnty = qnty;
	}

	public long getBnsDate() {
		return bnsDate;
	}

	public void setBnsDate(long bnsDate) {
		this.bnsDate = bnsDate;
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
