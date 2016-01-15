Strudel Workload Execution Engine
=================================
This artifact contains the main body of
Strudel's workload execution engine.

Strudel's execution platform consists of a workload manager
and a cluster of workers.
The workload manager interprets and executes a job definition
file and a worker server runs actual workloads as requested
from the workload manager.

The workload manager has the following features:
- invoking external scripts
for SQL/NoSQL server configuration and start-up
- management of data generation and population
- management of workloads and workflows
- continuous performance monitoring (through JMX)
- batch-oriented performance data aggregation
- performance reporting as JSON files

The framework does not include individual scripts to configure
and start/stop servers since they depend on the infrastructure
where an experiment is conducted.
(TODO: we will include an example (a set of scripts) based on
experiment settings in our proprietary environment -- after separating
its proprietary part...)
