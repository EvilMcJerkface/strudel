package com.nec.strudel.bench.auction.populate.jpa;

import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.jpa.AuctionAccessor;
import com.nec.strudel.bench.auction.populate.AuctionItemParams;
import com.nec.strudel.bench.auction.populate.AuctionParamSet;
import com.nec.strudel.bench.auction.populate.SaleItemParams;
import com.nec.strudel.bench.auction.populate.SaleParamSet;
import com.nec.strudel.bench.auction.populate.base.AbstractPopulateUser.InParam;
import com.nec.strudel.bench.auction.populate.jpa.PopulateAuctionItem;
import com.nec.strudel.bench.auction.populate.jpa.PopulateSaleItem;
import com.nec.strudel.bench.auction.populate.jpa.PopulateUser;
import com.nec.strudel.bench.test.EntityAssert;
import com.nec.strudel.bench.test.JpaDatabase;
import com.nec.strudel.bench.test.populate.PopulateUtil;

/**
 * Testing population with JPA. By default it uses Derby.
 * To test with MySQL:
 * <ol>
 * <li> make MySQL available for user=root, password="" and create database "auction"
 * <li> do: mvn test -DargLine="-Dtest.mysql=localhost" or specify a remote hostname.
 * <li> for a different user or password use: test.mysql.user and test.mysql.password,
 * respectively
 * </ol>
 */
public class PopulateTest {
	static final PopulateAuctionItem POP_AUC = new PopulateAuctionItem();
	static final PopulateSaleItem POP_SEL = new PopulateSaleItem();
	static final AtomicInteger USER_IDS = new AtomicInteger(1);
	static final JpaDatabase JPA_DB = new JpaDatabase("auction");
	private EntityManager em;
	@BeforeClass
	public static void startup() {
		JPA_DB.startup();
	}
	@AfterClass
	public static void shutdown() {
		JPA_DB.close();
	}
	@Before
	public void begin() {
		em = JPA_DB.createEntityManager();
	}
	@After
	public void end() {
		if (em != null) {
			em.clear();
		}
	}
	AuctionAccessor accessor() {
		return new AuctionAccessor(em);
	}

	AuctionItemParams auctionParam() {
		int userId = USER_IDS.getAndIncrement();
		accessor().store(new User(userId, "test"));
		return new AuctionItemParams(userId, userId);
	}
	SaleItemParams saleParam() {
		int userId = USER_IDS.getAndIncrement();
		accessor().store(new User(userId, "test"));
		return new SaleItemParams(userId, userId);
	}
	@Test
    public void testPopulateAuction() {
        AuctionParamSet aps =
        		auctionParam().create();
        PopulateUtil.process(POP_AUC, em, aps);
    }
    /**
     * different number of items per user.
     */
    @Test
    public void testPopulateAuctionMoreItems() {
        AuctionParamSet aps = auctionParam()
        		.setItemsPerUser(10)
        		.create();

        PopulateUtil.process(POP_AUC, em, aps);
    }
    /**
     * different number of bids per item.
     */
    @Test
    public void testPopulateAuctionMoreBids() {
        AuctionParamSet aps = auctionParam()
        		.setNumOfBids(10)
        		.create();
        PopulateUtil.process(POP_AUC, em, aps);
    }
    @Test
    public void testPopulateAuctionNoBids() {
        AuctionParamSet aps = auctionParam()
				.setNumOfBids(0)
				.create();
        PopulateUtil.process(POP_AUC, em, aps);
    }
	@Test
    public void testPopulate() {
    	SaleParamSet sps = saleParam().create();
        PopulateUtil.process(POP_SEL, em, sps);
    }
    /**
     * different number of items per user.
     */
    @Test
    public void testPopulateWithTestParamMoreItems() {
    	SaleParamSet sps = 
    			saleParam().setItemsPerUser(10).create();
        PopulateUtil.process(POP_SEL, em, sps);
    }
    /**
     * different number of BuyNowSale per item.
     */
    @Test
    public void testPopulateWithTestParamMoreBNSs() {
    	SaleParamSet sps = saleParam()
    			.setNumOfBns(10)
    			.create();
        PopulateUtil.process(POP_SEL, em, sps);
    }
    /**
     * Control that no BNS is created for items.
     */
    @Test
    public void testPopulateWithTestParamNoBNS() {
    	SaleParamSet sps = saleParam()
    			.setBuynowRatio(0)
    			.create();
        PopulateUtil.process(POP_SEL, em, sps);
    }

    @Test
    public void testPopulateUser() {
        int userId = USER_IDS.getAndIncrement();
        int nameLen = 14;
    	PopulateUser pop = new PopulateUser();
    	User user = PopulateUtil.param(userId)
    			.param(InParam.UNAME_LENGTH, nameLen)
    			.createParam(pop);
    	pop.process(em, user);

    	EntityAssert.assertEntityEquals(user,
    			em.find(User.class, user.getUserId()));
    }


}
