Strudel: Framework for Benchnarking Transactions on SQL/NoSQL
================================
Strudel is a set of abstraction layers that wrap various
transactional store, SQL or NoSQL, and help developers
to compose various workloads that run those stores.

This work will be presented at EDBT 2016 as
"Strudel: A Framework for Transaction Performance Analyses on SQL/NoSQL Systems."

**This is a preview version.**
We have included components that are
used in our internal experiments (by excluding proprietary components).
While they are fully functional, the current repository lacks (1) documentation
(2) some code and scripts that integrate the released components to
run experiments on a specific data store in a specific environment.

Description
-----------
Whereas a rich set of benchmarks and performance analysis platforms have
been developed for SQL-based systems (RDBMSs), it is challenging
for application developers to evaluate both SQL and NoSQL systems
for their specific needs. The Strudel framework helps such developers (as well as providers
of NoSQL stores) to build, customize, and share benchmarks that can
run on various SQL/NoSQL systems.


### EntityDB (Entity Persistence Interface)
https://github.com/tatemura/strudel/tree/master/epi

Entity DB is a simplified data access API designed to fill the gap between
SQL and NoSQL systems: it covers transactional
data access features that are common in various NoSQL systems as
well as relational databases.
It employs a subset of JPA annotations and defines new annotations
based on the concept of entity groups in order to cover  relaxed types of transactions
in a declarative manner.
It also provides a simplified access interface similar to JPA's EntityManager.

JPA is a standard Java API for Object-Relational Mapping, providing
a way to map Java objects (entities) and relational tables and a way
to access data in a relational database through such Java objects.
It consists of a set of annotations (e.g., @Entity, @Id) placed on Java entity
classes and various access methods (including a SQL-like query language).
JPA provides a basis for us to abstract out the details of underlying
data stores so that application developers can focus
on data handling in an object-oriented manner. However,
to most NoSQL systems, JPA is not applicable directly since the
concept of object-relational mapping relies on
expressive power and declarativeness of SQL and the relational model.

### Session Workload Framework
https://github.com/tatemura/strudel/tree/master/session

The Session Workload is a framework that helps developers to
implement benchmark application on different data access APIs (Entity DB, JPA, and
native NoSQL APIs) by reusing the code as much as possible.
It decouples data access logic from others:
Once a benchmark is developed on this framework, a developer can extend it
to use a custom data access API to exploit advanced features of SQL/NoSQL systems.


Requirements
------------
- Java >= 1.7

The following artifacts need manual installation
- Congenio: https://github.com/tatemura/congenio (used by the workload engine
for configuration management)
- Omid: https://github.com/yahoo/omid (OPTIONAL: to use Omid transaction server)

License
-------
Code licensed under the Apache License Version 2.0.
