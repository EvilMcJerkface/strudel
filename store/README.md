Strudel Data Stores
=======
Implementations of transactional data stores for
entity workloads

Currently we have the following data stores.

### TKVS-HBase

Transaction is implemented with HBase's check-and-put
operation, which operates value comparison and update in an atomic
manner.

Since check-and-put is applicable only to a single row, all
the entities that belongs to the same group must be packed
into one row. TO do this, we implement each key-value record
as a column-name value pair.

### TKVS-MongoDB

MongoDB's update operation is atomic for a single document
and consists of query part and update part. SImilar to the HBase implementation,
we pack entities of the same group into one document.

### TKVS-TokuMX

TokuMX is an enhanced version of MongoDB, supporting mluti-statement
transactions over multiple documents.

One limitation is that the current version does not support multi-state
transactions over sharded document collections.

We use a cluster of independent TokuMX servers and partition data based
on the group key so that a transaction of one group is always executable
at a single TokuMX server.

### TKVS-Omid

Omid is a transaction server on top of HBase in order to realize ACID
transactions over multiple rows.

