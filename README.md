# tapestry

An event-oriented high performance distributed hash table.

*Copyright (C) 2006 Jorge M. Faleiro Jr.*

Tapestry is the foundation for distributed computing in QuantLET, supporting both data and CPU intensive use cases.

The DHT of choice if you are stuck in Java and still partying like it is 2006.

## Data Fabric

Tapestry’s data fabric supports fully replicated or partitioned mode on its native implementation

## Compute Grid

Tapestry compute grid solution is built around conventional map-reduce abstractions

### So, You Like Your Vendor?

No problem. In addition to the native back-end implementation, Tapestry works as an ideal abstraction layer for commercial solutions out there, including products like Coherence or GemFire.


## Installation

```
git clone https://github.com/jfaleiro/tapestry.git
cd tapestry
gradle check
```

## Use

Go to a node on your network and set some values.

```java
final DMap<String, Integer> m = new DMapLocal<>("example");
m.addConstraintListener(new ConstraintListener<EntryEvent<String, Integer>>() {
				@Override
				public void onConstraintEvent(EntryEvent<String, Integer> event) {
					constrain(event.getValue() >= 0, "must be positive");
				});
m.put("a", 1);
m.put("b", -2);
m.put("c", 3);

```

Go to a second node and watch what is going on

```java
final DMap<String, Integer> m = new DMapLocal<>("example");
m.addBoundListener(new BoundListener<EntryEvent<String, Integer>>() {
				@Override
				public void onBoundEvent(EntryEvent<String, Integer> event) {
					print("v = "+ event);
				}
			});
v = ["a"=1]
v = ["c"=3]
```

## For Local Installation


```
gradle install
```

## For More

Check unit and mutational tests for examples of use

```
gradle test pitest
```
