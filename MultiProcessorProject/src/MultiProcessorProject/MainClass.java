/**
 * 
 */
package MultiProcessorProject;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author krishnatejaayinala, sindhura
 *
 */
public class MainClass extends CommonImpl {
	static Node node1;
	static Node node2;
	static Node node3;
	static int cycle = 0;
	static Instruction instruction;
	static Memory memory;
	static Bus bus;
	static Queue queueQuantum;
	// static HashMap<String, Integer> queueQuantum;
	// static int scheduledNode;
	// static String scheduledNodeKey ;

	public static void main(String[] args) throws IOException {

		node1 = new Node("/src/testcases/testcasenode1.txt", 1);
		node2 = new Node("/src/testcases/testcasenode2.txt", 2);
		node3 = new Node("/src/testcases/testcasenode3.txt", 3);

		memory = new Memory();
		bus = new Bus();
		// loops to give equal chance for each processor
		// queueQuantum = new HashMap<String, Integer>();
		// queueQuantum.put("node1", 1);
		// queueQuantum.put("node2", 2);
		// queueQuantum.put("node3", 3);
		queueQuantum = new Queue(64);
		queueQuantum.enqueueInt(1);
		queueQuantum.enqueueInt(2);
		queueQuantum.enqueueInt(3);
		int scheduledNode;
		// scheduledNodeKey = "node1";
		do {
			cycle++;
			System.out.println("---------cycle:" + cycle + "---------");
			if (!node1.areQueuesEmpty())
				processNodeRequest(node1, 1);
			if (!node2.areQueuesEmpty())
				processNodeRequest(node2, 2);
			if (!node3.areQueuesEmpty())
				processNodeRequest(node3, 3);
			// CPU scheduling alogorithm Round Robin (time slice set equally
			// among processors - mainly avoids starvation of processors)

			// if (!bus.queueBusRequest.isEmpty() ||
			// !bus.queueBusResponse.isEmpty() ) {}
			// scheduledNode = queueQuantum.get(scheduledNodeKey);

			if (!bus.queueBusRequest.isEmpty()) {
				instruction = (Instruction) bus.queueBusRequest.dequeue();
				System.out.println(
						"Bus Request: Node - " + instruction.getInstructionNode() + " ins:" + instruction.getCommand());
				busProcessing(instruction);
			}
			scheduledNode = queueQuantum.dequeueInt();
			queueQuantum.enqueueInt(scheduledNode);

			if (scheduledNode == 1) {
				if (!node1.l1Controller.queueL1CtoBusRequest.isEmpty()
						|| !node1.l1Controller.queueL1CtoBusResponse.isEmpty()
						|| !node1.l1Controller.queueBustoL1C.isEmpty()) {
					nodeScheduler(node1, 1);
					// queueQuantum.enqueueInt(scheduledNode);
				} else {
					// queueQuantum.enqueueInt(scheduledNode);
					scheduledNode = 2;
				}

			}
			if (scheduledNode == 2) {
				if (!node2.l1Controller.queueL1CtoBusRequest.isEmpty()
						|| !node2.l1Controller.queueL1CtoBusResponse.isEmpty()
						|| !node2.l1Controller.queueBustoL1C.isEmpty()) {
					nodeScheduler(node2, 2);
					// queueQuantum.enqueueInt(scheduledNode);
				} else {
					// queueQuantum.enqueueInt(scheduledNode);
					scheduledNode = 3;

				}
			}
			if (scheduledNode == 3) {
				if (!node3.l1Controller.queueL1CtoBusRequest.isEmpty()
						|| !node3.l1Controller.queueL1CtoBusResponse.isEmpty()
						|| !node3.l1Controller.queueBustoL1C.isEmpty()) {
					nodeScheduler(node3, 3);
					// queueQuantum.enqueueInt(scheduledNode);
				} else {
					// queueQuantum.enqueueInt(scheduledNode);
					scheduledNode = 1;
				}
			}

			// switch (scheduledNode) {
			// case 1:
			// if (!node1.l1Controller.queueL1CtoBusRequest.isEmpty()
			// || !node1.l1Controller.queueL1CtoBusResponse.isEmpty()
			// || !node1.l1Controller.queueBustoL1C.isEmpty())
			// nodeScheduler(node1, 1);
			// queueQuantum.enqueueInt(scheduledNode);
			// break;
			// case 2:
			// if (!node2.l1Controller.queueL1CtoBusRequest.isEmpty()
			// || !node2.l1Controller.queueL1CtoBusResponse.isEmpty()
			// || !node2.l1Controller.queueBustoL1C.isEmpty())
			// nodeScheduler(node2, 2);
			// queueQuantum.enqueueInt(scheduledNode);
			// break;
			// case 3:
			// if (!node3.l1Controller.queueL1CtoBusRequest.isEmpty()
			// || !node3.l1Controller.queueL1CtoBusResponse.isEmpty()
			// || !node3.l1Controller.queueBustoL1C.isEmpty())
			// nodeScheduler(node3, 3);
			// queueQuantum.enqueueInt(scheduledNode);
			// break;
			// default:
			// break;

			// }

		} while (!node1.areQueuesEmpty() || !node2.areQueuesEmpty() || !node3.areQueuesEmpty());

	}

	private static void processNodeRequest(Node node, int nodeNum) {
		int initialCount = 0;

		if (!node.processor.queueL1CtoProcessor.isEmpty()) {
			char[] data = ((ReadInstruction) instruction).getByteEnableData();
			String finaldata = String.valueOf(data);
			System.out.println("!!****************************!!");
			System.out.println("Result: " + finaldata + " for " + instruction.getCommand());
			System.out.println("!!****************************!!");
		}

		if (!node.processor.queueProcessor.isEmpty()) {
			if (initialCount == 0) {
				instruction = (Instruction) node.processor.queueProcessor.dequeue();
				node.processor.queueProcessortoL1C.enqueue(instruction);
				System.out.println("P to L1C:  Node - " + nodeNum + " ins:" + instruction.getCommand());
				initialCount++;
			}
		}

		// if (!node.l1Controller.queueL1CtoBusRequest.isEmpty()) {
		// instruction = (Instruction)
		// node.l1Controller.queueL1CtoBusRequest.dequeue();
		// bus.queueBusRequest.enqueue(instruction);
		// System.out.println("Bus request Queue updated: Node - " + nodeNum + "
		// ins:" + instruction.getCommand());
		// }
		if (!node.l1Controller.queueL1CtoL1D.isEmpty()) {
			instruction = (Instruction) node.processor.queueProcessor.dequeue();
			int address = instruction.getAddress().getAddress();

			Address fAddress = formatAddress(address, node.l1Controller.l1_Tag, node.l1Controller.l1_Index,
					node.l1Controller.l1_Offset);
			if (instruction.getProcessorInstructionKind() == 0) {// Read
				if (instruction.getInstructionTransferType() == 0) {
					int byteena = ((ReadInstruction) instruction).getByteEnables();
					Block block = node.l1Controller.readBlock(fAddress);
					char data = block.getData()[Integer.parseInt(fAddress.getOffset(), 2)];
					ReadInstruction rIns = new ReadInstruction();
					rIns.setAddress(fAddress);
					rIns.setTransferBlock(block);
					rIns.setByteEnables(byteena);
					rIns.setCommand(instruction.getCommand());
					rIns.setSingleCharData(data);
					node.l1Controller.l1Data.queueL1DtoL1C.enqueue(rIns);
				} else {
					Block block = instruction.getTransferBlock();
					node.l1Controller.l1write(block, fAddress);
					System.out.println(block.data);
					System.out.println("L1D block data updated from main memory: Node - " + nodeNum + " ins: "
							+ instruction.getCommand());
					// node.l1Controller.setState(instruction.getAddress().getAddress(),
					// "Data");
					node.l1Controller.clear(address);
				}
			} else {
				if (instruction.instructionTransferType == 1) {
					Block block = instruction.getTransferBlock();
					block.setBitData(Integer.parseInt(fAddress.getOffset(), 2),
							((WriteInstruction) instruction).getWriteData());
					node.l1Controller.l1writeChar(((WriteInstruction) instruction).getWriteData(), fAddress);
					node.l1Controller.l1write(block, fAddress);
					System.out.println("L1D write Data update: " + instruction.getCommand());
					System.out.println(block.data);
					System.out
							.println("L1D write Data update: Node - " + nodeNum + " ins: " + instruction.getCommand());
					// node.l1Controller.setState(instruction.getAddress().getAddress(),
					// "Data");
					node.l1Controller.clear(address);

				}
			}
		}

		if (!node.l1Controller.l1Data.queueL1DtoL1C.isEmpty()) {
			instruction = (Instruction) node.l1Controller.l1Data.queueL1DtoL1C.dequeue();
			int address = instruction.getAddress().getAddress();
			Address fAddress = formatAddress(address, node.l1Controller.l1_Tag, node.l1Controller.l1_Index,
					node.l1Controller.l1_Offset);
			int byteena = ((ReadInstruction) instruction).getByteEnables();

			ReadInstruction rIns = new ReadInstruction();
			rIns.setByteEnables(byteena);
			rIns.setAddress(fAddress);
			rIns.setCommand(instruction.getCommand());
			rIns.setProcessorInstructionKind(instruction.getProcessorInstructionKind());
			rIns.setInstructionTransferType(0);
			if (node.l1Controller.getState(address).equals("Rdwaitd")) {
				char[] data = new char[byteena];
				if (byteena != 0) {

					Block block = node.l1Controller.readBlock(fAddress);
					char[] blockdata = block.getData();
					for (int i = 0; i < byteena; i++) {
						data[i] = blockdata[Integer.parseInt(fAddress.getOffset(), 2) + i];
						// data[i] =
						// node.l1Controller.readBlock(fAddress).getData()[Integer.parseInt(fAddress.getOffset(),
						// 2)
						// + i];
					}
					rIns.setByteEnableData(data);
				} else {
					char returnData = instruction.getTransferBlock().getData()[Integer.parseInt(fAddress.getOffset(),
							2)];
					rIns.setSingleCharData(returnData);
				}
				node.processor.queueL1CtoProcessor.enqueue(rIns);
				System.out.println("L1C to Processor: Data sent after hit,  Node - " + nodeNum + " ins: "
						+ instruction.getCommand());
				node.l1Controller.clear(address);
			}
		}

		if (!node.l1Controller.queueProcessortoL1C.isEmpty()) {
			instruction = (Instruction) node.l1Controller.queueProcessortoL1C.dequeue();
			boolean L1Hit = false;
			if (instruction.getProcessorInstructionKind() == 0) { // Read //
																	// Instruction
				L1Hit = node.l1Controller.isL1Hit(instruction.getAddress());
				if (L1Hit) {
					System.out.println("L1C to L1D: L1 Hit:  Node - " + nodeNum + " ins:" + instruction.getCommand());
					node.l1Controller.l1Data.queueL1CtoL1D.enqueue(instruction);
					node.l1Controller.setState(instruction.getAddress().getAddress(), "Rdwaitd");
				} else {
					int index = Integer.parseInt(instruction.getAddress().getIndex(), 2);
					int tag = Integer.parseInt(instruction.getAddress().getTag(), 2);

					if (node.l1Controller.isValid(index, tag) == 1) {
						if (node.l1Controller.isDirty(index, tag) == 1) {
							node.l1Controller.queueL1CtoBusRequest.enqueue(instruction);
							node.l1Controller.setState(instruction.getAddress().getAddress(), "Rd2waitd");
							System.out.println(
									"L1C to BusRequest:  Node - " + nodeNum + " ins:" + instruction.getCommand());
							System.out.println(
									"L1C main state: missd, state assign: Rd2waitd " + instruction.getCommand());
						} else { // need not write back
							node.l1Controller.queueL1CtoBusRequest.enqueue(instruction);
							node.l1Controller.setState(instruction.getAddress().getAddress(), "RdBuswaitd");
							System.out.println(
									"L1C to BusRequest:  Node - " + nodeNum + " ins:" + instruction.getCommand());
							System.out.println(
									"L1C main state: missc, state assign: RdBuswaitd " + instruction.getCommand());
						}
						//
					} else {
						// invalid, missi
						node.l1Controller.queueL1CtoBusRequest.enqueue(instruction);
						node.l1Controller.setState(instruction.getAddress().getAddress(), "RdBuswaitd");
						System.out
								.println("L1C main state: missi, state assign: RdBuswaitd " + instruction.getCommand());

						System.out
								.println("L1C to BusRequest:   Node - " + nodeNum + " ins:" + instruction.getCommand());

					}
				}
			} else {
				L1Hit = node.l1Controller.isL1Hit(instruction.getAddress());
				if (L1Hit) {
					System.out.println("L1C to L1D: L1 Hit:    Node - " + nodeNum + " ins:" + instruction.getCommand());
					node.l1Controller.l1Data.queueL1CtoL1D.enqueue(instruction);
					node.l1Controller.setState(instruction.getAddress().getAddress(), "Wrwaitd");
				} else {
					int index = Integer.parseInt(instruction.getAddress().getIndex(), 2);
					int tag = Integer.parseInt(instruction.getAddress().getTag(), 2);
					if (node.l1Controller.isValid(index, tag) == 1) {
						if (node.l1Controller.isDirty(index, tag) == 1) {
							node.l1Controller.queueL1CtoBusRequest.enqueue(instruction);
							node.l1Controller.setState(instruction.getAddress().getAddress(), "Wr2waitd");
							System.out.println(
									"L1C to BusRequest:   Node - " + nodeNum + " ins:" + instruction.getCommand());
							System.out.println(
									"L1C main state: missd, state assign: Wr2waitd " + instruction.getCommand());
						} else { // need not write back
							node.l1Controller.queueL1CtoBusRequest.enqueue(instruction);
							node.l1Controller.setState(instruction.getAddress().getAddress(), "WrBuswaitd");
							System.out.println(
									"L1C main state: missc, state assign: WrBuswaitd " + instruction.getCommand());
							System.out.println(
									"L1C to BusRequest:   Node - " + nodeNum + " ins:" + instruction.getCommand());

						}
					}
				}
			}
		}

		if (!node.processor.queueProcessortoL1C.isEmpty()) {
			instruction = (Instruction) node.processor.queueProcessortoL1C.dequeue();
			node.l1Controller.queueProcessortoL1C.enqueue(instruction);
		}

	}

	private static void nodeScheduler(Node node, int nodeNum) {
		instruction = (Instruction) node.l1Controller.queueL1CtoBusRequest.dequeue();
		int address = instruction.getAddress().getAddress();
		if (node.l1Controller.getState(address).equals("RdBuswaitd")
				|| node.l1Controller.getState(address).equals("WrBuswaitd")) {
			instruction.setInstructionNode(nodeNum);
			bus.queueBusRequest.enqueue(instruction);
		}

	}

	private static void busProcessing(Instruction instruction) {
		// instruction = (Instruction) bus.queueBusRequest.dequeue();
		Address address = instruction.getAddress();
		int addr = instruction.getAddress().getAddress();
		int block = Integer.parseInt(address.getIndex());
		if (instruction.getProcessorInstructionKind() == 0) {
			instruction.setBusRequest("BusRead " + addr);
		} else {
			instruction.setBusRequest("BusReadEx " + addr);
		}
		checkPresence(instruction, block);
	}

	private static void checkPresence(Instruction instruction, int block) {
		if (instruction.getInstructionNode() == 1) {
			System.out.println(instruction.getBusRequest() + "activated on bus, node2,3 notified: Node - "
					+ instruction.getInstructionNode() + " ins:" + instruction.getCommand());
			if (node2.l1Controller.isL1Hit(instruction.address)) {

			} else if (node3.l1Controller.isL1Hit(instruction.address)) {

			} else {
				// send invalid
			}
		} else if (instruction.getInstructionNode() == 2) {
			System.out.println(instruction.getBusRequest() + " activated on bus, node1,3 notified: Node - "
					+ instruction.getInstructionNode() + " ins:" + instruction.getCommand());
			if (node1.l1Controller.isL1Hit(instruction.address)) {

			} else if (node3.l1Controller.isL1Hit(instruction.address)) {

			} else {
				// send invalid
			}
		} else if (instruction.getInstructionNode() == 3) {
			System.out.println(instruction.getBusRequest() + " activated on bus, node1,2 notified: Node - "
					+ instruction.getInstructionNode() + " ins:" + instruction.getCommand());
			if (node1.l1Controller.isL1Hit(instruction.address)) {

			} else if (node2.l1Controller.isL1Hit(instruction.address)) {

			} else {
				// send invalid
			}
		}
	}

}
