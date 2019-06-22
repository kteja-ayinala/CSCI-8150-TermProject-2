/**
 * 
 */
package MultiProcessorProject;

import java.io.IOException;

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

	public static void main(String[] args) throws IOException {

		node1 = new Node("/src/testcases/testcasenode1.txt", 1);
		node2 = new Node("/src/testcases/testcasenode2.txt", 2);
		node3 = new Node("/src/testcases/testcasenode3.txt", 3);

		memory = new Memory();
		bus = new Bus();
		// loops to give equal chance for each processor
		queueQuantum = new Queue(3);
		queueQuantum.enqueueInt(1);
		queueQuantum.enqueueInt(2);
		queueQuantum.enqueueInt(3);

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

		} while (!node1.processor.queueProcessor.isEmpty() || !node2.processor.queueProcessor.isEmpty()
				|| !node3.processor.queueProcessor.isEmpty());
		// while (!node1.areQueuesEmpty() || !node2.areQueuesEmpty() ||
		// !node3.areQueuesEmpty());

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
					node.l1Controller.setState(instruction.getAddress().getAddress(), "Data");
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
					node.l1Controller.setState(instruction.getAddress().getAddress(), "Data");

				}
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
}
