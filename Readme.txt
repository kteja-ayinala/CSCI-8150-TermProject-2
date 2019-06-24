CSCI-8150-TermProject-2

Multi Processor functionality using existing memory project

We have used round robin scheduling, to implement instructions from multiple processors. Each processor gets it turn based on the cycles. (A queue is maintained which dequeues and enqueues the three nodes)

In the current implementation, the instruction from Node2 is executed first. Then the bus requets are stalled till it get's required acknowledgements, and data from other processors or memory. 

The second instruction is from the Node1 to the same address as Node1 instruction address. Then the data is set to State "S". and returns to the node1.

The write instructions from Node2 and Node1 are followed after read instructions. The write instruction from Node2 is hit in it's own memory and set to "M" state. and sends invalid "I" to other nodes.

Then the write instruction from Node1 is processed, which is hit in it's own memory which is invalidated. But finds it in the Node2 cache in "M" state. So, data from Node2 cache is flused to the memory and sent to Node1 with "M" permission.

Then the write instruction from Node1 is processed successfully.


Testcase:
We have a separate test case package in the src folder. The test cases are set as parameters to the Node constructors in Mainclass.java. Output is saved as trace.txt in src folder.


