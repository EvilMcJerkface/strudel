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
In addition to annotations provided by JPA, we introduce
the following annotations to represent entity groups (which
are analogous to JPA's @Entity, @Id, @IdClass):

- @Group
- @GroupId
- @GroupIdClass

### EntityDB: Data Access API
We provide a simplified data access API (instead of
JPA's EntityManager).

- CRUD operations: read-write operations on a single entity
(similar to JPA's find/persist/merge/remove operations).
- Secondary key access: read operation on multiple entities by
specifying a secondary key.
- Group transactions: multi-statement transactions on a single
entity group.

### Secondary Index
In order to specify a secondary key of an entity, we provide
the following annotation.

- @Indexes

The actual way to provide accessibility with a secondary key
is specific to implementations.
