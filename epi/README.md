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

- @Group
- @GroupId
- @GroupIdClass

### EntityDB: Data Access API

- CRUD operations
- Secondary key access
- Group transactions

### Secondary Index

- @Indexes
