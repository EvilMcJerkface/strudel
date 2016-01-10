Strudel: Framework for Benchnarking Transactions on SQL/NoSQL
================================
Strudel is a set of abstraction layers that wrap various
transactional store, SQL or NoSQL, and help developers
to compose various workloads that run those stores.

This work will be presented at EDBT 2016 as
"Strudel: Framework for Transaction Performance Analyses on SQL/NoSQL stores."

**This is a preview version.**
We have included components that are
used in our internal experiments (by excluding proprietary components).
While they are fully functional, the current repository lacks (1) documentation
(2) some code and scripts that integrate the released components to
run experiments on a specific data store in a specific environment.

Description
-----------
[To be written...]


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
