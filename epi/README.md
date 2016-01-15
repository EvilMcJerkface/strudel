Strudel EPI (Entity Persistence Interface)
=========
EPI is one of the abstraction layers of Strudel that are useful to
develop workloads that can run on various data stores.

It employs a subset of JPA (Java Persistence API) to fill the gap
between SQL and NoSQL stores. JPA is a standard Java API for Object-Relational
Mapping, providing a basis for us to abstract out the details of underlying
data stores. To most NoSQL stores, though, JPA is not directly applicable
since the concept of OR-Mapping relies on expressive power and declarativeness
of SQL and the relational model. For the purpose of a common
performance analysis platform, we have designed a simplified version of APIs.

### Entity Group Annotations

In order to provide a common API to incorporate relaxed
transaction support on NoSQL stores, we adopt the concept of entity groups.

In addition to annotations provided by JPA, we introduce
the following annotations to represent entity groups (which
are analogous to JPA's @Entity, @Id, @IdClass):

- @Group
- @GroupId
- @GroupIdClass

The following code uses the JPA standards to define Bid Java class as an entity @Entity with
a compound key (sellerId, itemNo, bidNo) (as annotated with @Id),
which is packaged as one object of a class BidId (@IdClass). We further annotate
it with EPI's annotations to define an entity group.

	@Group(parent = AuctionItem.class)
	@Entity
	@Indexes({
	  @On(property = "auctionItemId"),
	  @On(property = "userId")
	})
	@GroupIdClass(ItemId.class)
	@IdClass(BidId.class)
	public class Bid {
	    @GroupId @Id private int sellerId;
	    @GroupId @Id private int itemNo;
	    @Id @GeneratedValue
	    private int bidNo;
	    private double bidAmount;
	    private long bidDate;
	    private int userId;

With extended annotations,
a benchmark developer can associate two entity
classes together (as a parent-child relationship)
in one group by specifying @Group annotation at a child class
to indicate its parent. In this example, Bid is associated with AuctionItem
(so that we can consistently access all the bids on one particular auction
item and update the item when the maximum bid price changes).
A group id @GroupId is a member of a compound key (a group key)
that specifies a group instance.
A set of group ids on an entity class must be a subset of the set of
ids (i.e., a (compound) primary key) that are annotated with @Id. 

### EntityDB: Data Access API
We provide a simplified data access API (instead of
JPA's EntityManager).

- CRUD operations: read-write operations on a single entity
(similar to JPA's find/persist/merge/remove operations).
- Secondary key access: read operation on multiple entities by
specifying a secondary key.
- Group transactions: multi-statement transactions on a single
entity group.

The following methods are part of EntityDB interface for
the CRUD operations and secondary key access:

	<T> T get(Class<T> entityClass, Object key);
	void create(Object entity);
	void update(Object entity);
	void delete(Object entity);
	<T> List<T> getEntitiesByIndex(
	    Class<T> entityClass, String property,
	    Object key);

The following code is an example of a transaction that accesses
multiple entities in one group:

	BidResult r = edb.run(Bid.class, itemId,
	  new EntityTask<BidResult>() {
	    public BidResult run(EntityTransaction tx) {
	      AuctionItem item =
	          tx.get(AuctionItem.class, itemId);
	      if (item == null) {
	        return BidResult.NONE;
	      }
	      if (bid.amount() <= item.getMaxBid()) {
	        return BidResult.LOST;
	      }
	      tx.create(bid);
	      item.setMaxBid(bid.amount());
	      tx.update(item);
	      return BidResult.SUCCESS;
	    }
	});

### Secondary Index
In order to specify a secondary key of an entity, we provide
the following annotation.

- @Indexes

	@Indexes({
	  @On(property = "auctionItemId"),
	  @On(property = "userId")
	})

The actual way to provide accessibility with a secondary key
is specific to implementations.
