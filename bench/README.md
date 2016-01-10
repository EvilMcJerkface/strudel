Transactional Benchmarks on Strudel
=====
Strudel provides the following benchmark implementations
as examples of micro-level and application-level session-oriented workloads.


Micro Benchmark
---------------
Micro Benchmark is a microbenchmark that emulates a simplified user
content management service. To represent different patterns of
user data access, it include various types of entities (e.g. emulating
personal items and shared items). A developer can compose
a session state transition model by mixing these entities and interactions
over them in order to emulate access pattern of a specific application. 


Auction Benchmark
-----------------
Auction Benchmark is an example of application-level
benchmarks. It is similar
to other benchmarks (such as AuctionMark in OLTP-Bench and RUBiS
benchmark) but features entity groups.

