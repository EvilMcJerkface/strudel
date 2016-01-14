TKVS: Generic Transactional KVS for EPI Implementations
=====
Whereas EPI is simplified for minimum support for entity data acess,
it still need engineering efforts to develop an implementation for
a particular NoSQL store. We provide a yet another API fortransactional
key-value data access so that a provider of a NoSQL store can quickly
implement this further simplified API instead of directly implementing
EPI.

We have utilized this API to implement stores using HBase, MongoDB, TokuMX,
and Omid.

In Transactional KVS, a data record is just a pair
of byte-array key and value, and records are grouped by
a group key (another byte array). Data access is done
by a group transaction (started with a group key) and
simple put/get operations.

The framework provides an Entity DB implementation for
Transactional KVS, which automates:
- mapping from entities to
byte array key-value objects,
- index management, and
- auto key generation.
