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
package com.nec.strudel.bench.auction.interactions.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.SaleId;
import com.nec.strudel.bench.auction.entity.SaleItem;

public class AuctionAccessor {
	private final EntityManager em;
	public AuctionAccessor(EntityManager em) {
		this.em = em;
	}
	public void store(Object... entities) {
		em.getTransaction().begin();
		for (Object e : entities) {
			em.persist(e);
		}
		em.getTransaction().commit();
	}
	public List<AuctionItem> auctionsBySeller(int sellerId) {
		return em.createQuery(
		"SELECT a FROM AuctionItem a WHERE a.sellerId = :sid",
				AuctionItem.class)
				.setParameter("sid", sellerId)
				.getResultList();
	}
	public List<SaleItem> salesBySeller(int sellerId) {
		return em.createQuery(
		"SELECT a FROM SaleItem a WHERE a.sellerId = :sid",
				SaleItem.class)
				.setParameter("sid", sellerId)
				.getResultList();	
	}
	public List<Bid> bidsByItem(ItemId itemId) {
		String QUERY =
				"SELECT b FROM Bid b WHERE b.sellerId = :sid AND b.itemNo = :ino";
		return em.createQuery(QUERY, Bid.class)
				.setParameter("sid", itemId.getSellerId())
				.setParameter("ino", itemId.getItemNo())
				.getResultList();
	}
	public List<BuyNowAuction> bnaByBuyer(int buyerId) {
		String QUERY =
			"SELECT b FROM BuyNowAuction b WHERE b.buyerId = :uid";
		return em.createQuery(QUERY, BuyNowAuction.class)
				.setParameter("uid", buyerId)
				.getResultList();
	}
	public List<BuyNowSale> bnsByItem(ItemId itemId) {
		String QUERY =
				"SELECT b FROM BuyNowSale b WHERE b.sellerId = :sid AND b.itemNo = :ino";
		return em.createQuery(QUERY, BuyNowSale.class)
				.setParameter("sid", itemId.getSellerId())
				.setParameter("ino", itemId.getItemNo())
				.getResultList();
		
	}
	public List<BuyNowSale> bnsByBuyer(int buyerId) {
		String QUERY =
				"SELECT b FROM BuyNowSale b WHERE b.buyerId = :uid";
			return em.createQuery(QUERY, BuyNowSale.class)
					.setParameter("uid", buyerId)
					.getResultList();
		
	}
	public BuyNowAuction bna(ItemId itemId) {
		return em.find(BuyNowAuction.class, itemId);
	}
	public BuyNowSale bns(SaleId id) {
		return em.find(BuyNowSale.class, id);
	}
	public AuctionItem item(ItemId itemId) {
		return em.find(AuctionItem.class, itemId);
	}
	public SaleItem sale(ItemId itemId) {
		return em.find(SaleItem.class, itemId);
	}
}