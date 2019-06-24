/**
 * 
 */
package MultiProcessorProject;

import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	static HashMap<Instruction, String> acknowlegmentEntry;
	static List<String> ack;
	static boolean busInProcess = false;
	static Instruction busReqIns;
	// static HashMap<String, Integer> queueQuantum;
	// static int scheduledNode;
	// static String scheduledNodeKey ;

	public static void main(String[] args) throws IOException {
//		 System.setOut(new PrintStream(new FileOutputStream(curDir +
//		 "/src/MultiProcessorProject/trace.txt")));
		node1 = new Node("/src/testcases/testcasenode1.txt", 1);
		node2 = new Node("/src/testcases/testcasenode2.txt", 2);
		node3 = new Node("/src/testcases/testcasenode3.txt", 3);

		memory = new Memory();
		bus = new Bus();
		acknowlegmentEntry = new HashMap<Instruction, String>();
		ack = new ArrayList<String>();
		// loops to give equal chance for each processor

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

			if (!bus.queueBusResponse.isEmpty()) {
				instruction = (Instruction) bus.queueBusResponse.dequeue();

				busReqIns = instruction;
				if (instruction.getInstructionNode() == 1) {
					node1.l1Controller.queueBustoL1C.enqueue(instruction);
					node1.l1Controller.setMESI('E');
					System.out.println("BusResponse to  L1C -node " + instruction.getInstructionNode()
							+ " MESI state: E " + " ins:" + instruction.getCommand());
				} else if (instruction.getInstructionNode() == 2) {
					node2.l1Controller.queueBustoL1C.enqueue(instruction);
					node2.l1Controller.setMESI('E');
					System.out.println("BusResponse to  L1C -node " + instruction.getInstructionNode()
							+ " MESI state: E " + " ins:" + instruction.getCommand());
				} else if (instruction.getInstructionNode() == 3) {
					node3.l1Controller.queueBustoL1C.enqueue(instruction);
					node3.l1Controller.setMESI('E');
					System.out.println("BusResponse to  L1C -node " + instruction.getInstructionNode()
							+ " MESI state: E " + " ins:" + instruction.getCommand());
				}
			}

			Block memBlock;
			if (!memory.queueBustoMemory.isEmpty()) {
				instruction = (Instruction) memory.queueBustoMemory.dequeue();
				int address = instruction.getAddress().getAddress();
				Address fAddress = formatAddress(address, memory.memory_Tag, memory.memory_Index, memory.memory_Offset);
				int index = Integer.parseInt(fAddress.getIndex(), 2);
				if (instruction.getInstructionTransferType() == 0) {// Read
																	// Instruction
					memBlock = memory.memory[index];
					memBlock.setBlockAddress(index);
					ReadInstruction rIns = new ReadInstruction();
					rIns.setCommand(instruction.getCommand());
					rIns.setByteEnables(((ReadInstruction) instruction).getByteEnables());
					rIns.setTransferBlock(memBlock);
					rIns.setProcessorInstructionKind(instruction.getProcessorInstructionKind());
					rIns.setInstructionNode(instruction.getInstructionNode());
					rIns.setProcessorInstructionKind(instruction.getProcessorInstructionKind());
					rIns.setInstructionTransferType(0);
					rIns.setAddress(fAddress);
					// memory.queueMemorytoBus.enqueue(rIns);
					bus.queueBusResponse.enqueue(rIns);

					System.out.println("Bus Response: Data Block " + instruction.getCommand());
				} else {// Write instruction
					memBlock = instruction.getTransferBlock();
					if (memBlock != null) {
						char[] datawrite = memBlock.getData();
						memory.memory[index].setData(datawrite);
						System.out.println(datawrite);
						System.out.println("Memory data updated Node - " + instruction.getInstructionNode() + " ins:"
								+ instruction.getCommand());
					}
				}
			}

			if (!bus.queueAckFromPeers.isEmpty()) {
				if (bus.queueAckFromPeers.dequeueStr().equals("Invalid")) {
					instruction = (Instruction) bus.queueBustoMemory.dequeue();
					System.out.println("Acknowledgements received as both Invalid for Node - "
							+ instruction.getInstructionNode() + " ins:" + instruction.getCommand());
					System.out.println("Bus to Memory: Node - " + instruction.getInstructionNode() + " ins:"
							+ instruction.getCommand());
					memory.queueBustoMemory.enqueue(instruction);
				}
			}

			// if (!bus.queueBusRequestStalled.isEmpty()) {
			// instruction = (Instruction) bus.queueBusRequestStalled.dequeue();
			// int address = instruction.getAddress().getAddress();
			// if (bus.getState(address) == null ||
			// !bus.getState(address).equals("Stall")) {
			// System.out.println("L1C to Bus Request: Node - " +
			// instruction.getInstructionNode() + " ins:"
			// + instruction.getCommand());
			// busProcessing(instruction);
			// bus.setState(address, "Stall");
			// } else {
			// if (instruction.processorInstructionKind == 0) {
			// System.out.println("BusRead Request stalled: Node - " +
			// instruction.getInstructionNode()
			// + " ins:" + instruction.getCommand());
			// bus.queueBusRequestStalled.enqueue(instruction);
			// } else {
			// System.out.println("BusReadEx Request stalled: Node - " +
			// instruction.getInstructionNode()
			// + " ins:" + instruction.getCommand());
			// bus.queueBusRequestStalled.enqueue(instruction);
			// }
			// }
			// }

			if (!bus.queueBusRequest.isEmpty() && busInProcess == false) {
				instruction = (Instruction) bus.queueBusRequest.dequeue();
				busInProcess = true;
				busProcessing(instruction);
			}

			scheduledNode = queueQuantum.dequeueInt();
			queueQuantum.enqueueInt(scheduledNode);

			if (scheduledNode == 1) {
				System.out.println("Round robin scheduling, available for node1");
				if (!node1.l1Controller.queueL1CtoBusRequest.isEmpty()
						|| !node1.l1Controller.queueL1CtoBusResponse.isEmpty()
						|| !node1.l1Controller.queueBustoL1C.isEmpty()) {
					nodeScheduler(node1, 1);
				} else {
					System.out.println("No bus request, assigning other processor");
					scheduledNode = 2;
				}

			}
			if (scheduledNode == 2) {
				System.out.println("Round robin scheduling, available for node2");
				if (!node2.l1Controller.queueL1CtoBusRequest.isEmpty()
						|| !node2.l1Controller.queueL1CtoBusResponse.isEmpty()
						|| !node2.l1Controller.queueBustoL1C.isEmpty()) {
					nodeScheduler(node2, 2);
				} else {
					System.out.println("No bus request, assigning other processor");
					scheduledNode = 3;

				}
			}
			if (scheduledNode == 3) {
				System.out.println("Round robin scheduling, available for node3");
				if (!node3.l1Controller.queueL1CtoBusRequest.isEmpty()
						|| !node3.l1Controller.queueL1CtoBusResponse.isEmpty()
						|| !node3.l1Controller.queueBustoL1C.isEmpty()) {
					nodeScheduler(node3, 3);
				} else {
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

		} while (!node1.areQueuesEmpty() || !node2.areQueuesEmpty() || !node3.areQueuesEmpty()
				|| !bus.queueBusRequest.isEmpty() || !bus.queueBusResponse.isEmpty()
				|| !memory.queueBustoMemory.isEmpty());

	}

	private static void processNodeRequest(Node node, int nodeNum) {
		int initialCount = 0;

		if (!node.processor.queueProcessor.isEmpty()) {
			if (initialCount == 0) {
				instruction = (Instruction) node.processor.queueProcessor.dequeue();
				node.processor.queueProcessortoL1C.enqueue(instruction);
				System.out.println("P to L1C:  Node - " + nodeNum + " ins:" + instruction.getCommand());
				initialCount++;
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

		if (!node.l1Controller.l1Data.queueL1CtoL1D.isEmpty()) {
			instruction = (Instruction) node.l1Controller.l1Data.queueL1CtoL1D.dequeue();
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
					bus.clear(address);
					busInProcess = false;
				}
			} else {
				if (instruction.instructionTransferType == 1) {
					char wdata = instruction.getCommand().toString().split(" ")[2].charAt(0);
					int offset = Integer.parseInt(fAddress.getOffset(), 2);
					Block block = node.l1Controller.readBlock(fAddress);
					block.setBitData(offset, wdata);
					node.l1Controller.l1writeChar(((WriteInstruction) instruction).getWriteData(), fAddress);
					node.l1Controller.l1write(block, fAddress);
					System.out.println(block.data);
					instruction.setTransferBlock(block);
					instruction.setInstructionNode(nodeNum);
					System.out
							.println("L1D write Data update: Node - " + nodeNum + " ins: " + instruction.getCommand());
					if (node.l1Controller.getState(instruction.getAddress().getAddress()).equals("WriteBack")) {
						memory.queueBustoMemory.enqueue(instruction);

					}
					bus.clear(address);
					busInProcess = false;
				}
			}
		}

		if (!node.processor.queueL1CtoProcessor.isEmpty()) {
			instruction = (Instruction) node.processor.queueL1CtoProcessor.dequeue();
			char[] data = ((ReadInstruction) instruction).getByteEnableData();
			String finaldata = String.valueOf(data);
			System.out.println("!!****************************!!");
			System.out.println("Result: " + finaldata + " for " + instruction.getCommand());
			System.out.println("!!****************************!!");
		}

		if (!node.l1Controller.queueBustoL1C.isEmpty()) {
			instruction = (Instruction) node.l1Controller.queueBustoL1C.dequeue();

			int address = instruction.getAddress().getAddress();
			Address fAddress = formatAddress(address, node.l1Controller.l1_Tag, node.l1Controller.l1_Index,
					node.l1Controller.l1_Offset);

			if (instruction.getProcessorInstructionKind() == 0) {
				int byteena = ((ReadInstruction) instruction).getByteEnables();
				if (instruction.getInstructionTransferType() == 0) {
					WriteInstruction wIns = new WriteInstruction();
					wIns.setTransferBlock(instruction.getTransferBlock());
					wIns.setAddress(fAddress);
					wIns.setCommand(instruction.getCommand());
					wIns.setProcessorInstructionKind(instruction.getProcessorInstructionKind());
					wIns.setInstructionNode(instruction.getInstructioNum());
					wIns.setInstructionTransferType(1);
					// wIns.setWriteData((ReadInstruction)instruction.getTransferBlock());
					node.l1Controller.l1Data.queueL1CtoL1D.enqueue(wIns);
					System.out.println("L1C to L1D: Node - " + nodeNum + " ins: " + instruction.getCommand());
				}
				ReadInstruction rIns = new ReadInstruction();
				rIns.setByteEnables(byteena);
				rIns.setAddress(fAddress);
				rIns.setCommand(instruction.getCommand());
				rIns.setProcessorInstructionKind(instruction.getProcessorInstructionKind());
				rIns.setInstructionTransferType(0);
				if (byteena != 0) {
					char[] data = new char[byteena];
					Block block = instruction.getTransferBlock();
					for (int i = 0; i < byteena; i++) {
						data[i] = block.getData()[Integer.parseInt(fAddress.getOffset(), 2) + i];
					}
					rIns.setByteEnableData(data);
				} else {
					char returnData = instruction.getTransferBlock().getData()[Integer.parseInt(fAddress.getOffset(),
							2)];
					rIns.setSingleCharData(returnData);
				}
				// l1Controller.setState(instruction.getAddress().getAddress(),
				// "Rdwaitd");
				node.processor.queueL1CtoProcessor.enqueue(rIns);
				System.out.println("L1C to Processor: Node - " + nodeNum + " ins: " + instruction.getCommand());

				// }F
			} else {// Write
				if (node.l1Controller.isValid(Integer.parseInt(fAddress.getIndex(), 2),
						Integer.parseInt(fAddress.getTag(), 2)) == 1) {
					if (node.l1Controller.isDirty(Integer.parseInt(fAddress.getIndex(), 2),
							Integer.parseInt(fAddress.getTag(), 2)) == 1) {
						// Block block = node.l1Controller.readBlock(fAddress);
						node.l1Controller.setDirty(Integer.parseInt(fAddress.getIndex(), 2),
								Integer.parseInt(fAddress.getTag(), 2), 0);
						L1Controller.setvalid(Integer.parseInt(fAddress.getIndex(), 2),
								Integer.parseInt(fAddress.getTag(), 2), 0);

					}

				}
				WriteInstruction wIns = new WriteInstruction();
				wIns.setTransferBlock(instruction.getTransferBlock());
				wIns.setAddress(fAddress);
				wIns.setCommand(instruction.getCommand());
				wIns.setProcessorInstructionKind(instruction.getProcessorInstructionKind());
				wIns.setInstructionTransferType(1);
				wIns.setWriteData(instruction.getCommand().toString().split(" ")[2].charAt(0));
				node.l1Controller.l1Data.queueL1CtoL1D.enqueue(wIns);
				System.out.println("L1C to L1D:  state set to: Wrwait1d" + instruction.getCommand());
				node.l1Controller.setState(instruction.getAddress().getAddress(), "Wrwait1d");

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

						}
						//
					} else {
						// invalid, missi
						node.l1Controller.queueL1CtoBusRequest.enqueue(instruction);
						node.l1Controller.setState(instruction.getAddress().getAddress(), "RdBuswaitd");
						System.out
								.println("L1C main state: missi, state assign: RdBuswaitd " + instruction.getCommand());
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

						}
					} else {
						ReadInstruction rIns = new ReadInstruction();
						rIns.setAddress(instruction.getAddress());
						rIns.setCommand(instruction.getCommand());
						rIns.setInstructioNum(instruction.instructioNum);
						rIns.setSingleCharData(((WriteInstruction) instruction).getWriteData());
						rIns.setProcessorInstructionKind(instruction.getProcessorInstructionKind());
						rIns.setInstructionTransferType(0);
						rIns.setInstructionNode(instruction.getInstructionNode());
						node.l1Controller.queueL1CtoBusRequest.enqueue(instruction);
						node.l1Controller.setState(instruction.getAddress().getAddress(), "WrBuswaitd");
						System.out
								.println("L1C main state: missi, state assign: WrBuswaitd " + instruction.getCommand());

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
			System.out.println("L1C to Bus Request: Node - " + instruction.getInstructionNode() + " ins:"
					+ instruction.getCommand());
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
		acknowlegmentEntry = checkPresence(instruction, block);
	}

	private static HashMap<Instruction, String> checkPresence(Instruction instruction, int block) {
		if (instruction.getInstructionNode() == 1) {
			System.out.println(instruction.getBusRequest() + " activated on bus, node2,3 verify: Node - "
					+ instruction.getInstructionNode() + " ins:" + instruction.getCommand());

			if (busReqIns != null && busReqIns.getAddress().getAddress() == instruction.getAddress().getAddress()) {
				node1.l1Controller.queueBustoL1C.enqueue(busReqIns);
				node1.l1Controller.setMESI('S');
				node2.l1Controller.setMESI('S');
				System.out.println("BusResponse to  L1C - node: " + instruction.getInstructionNode() + ", "
						+ busReqIns.getInstructionNode() + " MESI state: S " + " ins:" + instruction.getCommand());
				busInProcess = false;
				busReqIns = null;
			}
			if (node2.l1Controller.isL1Hit(instruction.address)) {
				System.out.println("hit in  node2 L1");
				if (node2.l1Controller.getMESI() == 'M') {
					node2.l1Controller.setMESI('I');
					node2.l1Controller.setState(instruction.getAddress().getAddress(), "WriteBack");
					System.out.println("Write back the cache line");
					instruction.setTransferBlock(node1.l1Controller.readBlock(instruction.getAddress()));
					bus.queueBusResponse.enqueue(instruction);
					node1.l1Controller.setState(instruction.getAddress().getAddress(), "WriteBack");
				}
//				if(node2.l1Controller.getMESI() == 'S'){
//					if (busReqIns != null && busReqIns.getAddress().getAddress() == instruction.getAddress().getAddress()) {
//						node1.l1Controller.queueBustoL1C.enqueue(busReqIns);
//						node1.l1Controller.setMESI('S');
//						node2.l1Controller.setMESI('S');
//						System.out.println("BusResponse to  L1C - node: " + instruction.getInstructionNode() + ", "
//								+ busReqIns.getInstructionNode() + " MESI state: M " + " ins:" + instruction.getCommand());
//						busInProcess = false;
////						busReqIns = null;
//					}
//				}
			} else if (node3.l1Controller.isL1Hit(instruction.address)) {
				System.out.println("hit in  node3 L1");
			} else {
				bus.queueBustoMemory.enqueue(instruction);
				bus.queueAckFromPeers.enqueueStr("Invalid");
				ack.add("Invalid");
				acknowlegmentEntry.put(instruction, "Invalid");
				// send invalid
			}

		} else if (instruction.getInstructionNode() == 2) {
			System.out.println(instruction.getBusRequest() + " activated on bus, node1,3 verify: Node - "
					+ instruction.getInstructionNode() + " ins:" + instruction.getCommand());
			if (node2.l1Controller.isL1Hit(instruction.address)) {
				System.out.println("Hit in own cache- node: " + instruction.getInstructionNode() + " MESI state: M "
						+ " ins:" + instruction.getCommand());
				node2.l1Controller.setMESI('M');
				node1.l1Controller.setMESI('I');
				node1.l1Controller.l1delete(instruction.getAddress());
				System.out.println("Invalid sent  MESI state: I " + " ins:" + instruction.getCommand());
				node2.l1Controller.queueBustoL1C.enqueue(instruction);
				busInProcess = false;
			} else if (node1.l1Controller.isL1Hit(instruction.address)) {
				System.out.println("hit in  node1 L1");
			} else if (node3.l1Controller.isL1Hit(instruction.address)) {
				System.out.println("hit in  node3 L1");
			} else {
				bus.queueBustoMemory.enqueue(instruction);
				bus.queueAckFromPeers.enqueueStr("Invalid");
				ack.add("Invalid");
				acknowlegmentEntry.put(instruction, "Invalid");
				// send invalid
			}
		} else if (instruction.getInstructionNode() == 3) {
			System.out.println(instruction.getBusRequest() + " activated on bus, node1,2 verify: Node - "
					+ instruction.getInstructionNode() + " ins:" + instruction.getCommand());
			if (node3.l1Controller.isL1Hit(instruction.address)) {
				System.out.println("hit in  node3 L1");
			} else if (node1.l1Controller.isL1Hit(instruction.address)) {
				System.out.println("hit in  node1 L1");
			} else if (node2.l1Controller.isL1Hit(instruction.address)) {
				System.out.println("hit in  node2 L1");
			} else {
				bus.queueBustoMemory.enqueue(instruction);
				bus.queueAckFromPeers.enqueueStr("Invalid");
				ack.add("Invalid");
				acknowlegmentEntry.put(instruction, "Invalid");
				// send invalid
			}
		}
		return acknowlegmentEntry;
	}

}
