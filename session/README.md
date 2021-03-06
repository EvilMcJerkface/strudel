Session Workload Framework
=======
Session Workload Framework is one of the abstraction layers
of Strudel, a layer at an application level for session-oriented
workloads so that developers can create benchmarks that can run various
data access APIs besides EPI.


### Interaction Interface
Emulated user interaction for each user
is called a session, which consists of a sequence of actions
(called  interactions). An interaction is a unit of the application's
work, which is a predefined data accessing procedure without user
intervention (one interaction may execute multiple transactions to perform
a unit of work).

Each interaction of a benchmark must implement the following interface:

	public interface Interaction<T> {
	  void prepare(ParamBuilder paramBuilder);
	  Result execute(Param param, T db,
	                       ResultBuilder res);
	  void complete(StateModifier modifier);
	}

The "prepare" operation is to generate
a parameter that is used in the next "execute" operation.
Typically, this operation emulates a human-side thinking
process.
For example, an auction benchmark emulates how a bid
price is decided given the current session state (e.g.,
information on the auction item retrieved in the past
interactions).

The "execute" operation implements the actual
action that accesses the data. Given the parameter (param)
generated by the "prepare" operation
and the data access API (db), the method performs
transactions with the data store.

The "complete" operation defines how the session
state is modified based on the result of "execute" operation.
For example, to emulate a human's browsing activities on
a web application, the result of a browsing interaction
includes a list of retrieved items. The "complete" operation
may choose one of such items as "current item of interest" (i.e. part
of the "state"). The modified state is used in the following
interaction, which may take an action on the chosen item
(e.g., placing a bid).


### Session State Transition

A benchmark workload based on the Session Workload framework can
be easily customized for a specific experiment. A state transition model
that emulates a user behavior is given at run-time as an XML data.
The following XML is an example of a session definition, which
is part of a workload definition. 

	<session>
	  <packageName .../>
	  <Transitions>
	    <transition name="START">
	      <next name="HOME"/>
	    </transition>
	    <transition name="HOME">
	      <next name="SELL_AUCTION_ITEM" prob="0.2"/>
	      <next name="SELL_SALE_ITEM" prob="0.1"/>
	      <next name="VIEW_AUCTION_ITEMS_BY_SELLER" prob="0.1"/>
	      <next name="VIEW_SALE_ITEMS_BY_SELLER" prob="0.1" />
	      <next name="VIEW_AUCTION_ITEMS_BY_BUYER" prob="0.1"/>
	      <next name="VIEW_SALE_ITEMS_BY_BUYER" prob="0.1"/>
	      <next name="VIEW_BIDS_BY_BIDDER" prob="0.1"/>
	      <next name="VIEW_WINNING_BIDS_BY_BIDDER" prob="0.1"/>
	      <next name="END" prob="0.1"/>
	    </transition>
	    <transition name="SELL_AUCTION_ITEM">
	      <next name="HOME"/>
	    </transition>
