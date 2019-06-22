/**
 * 
 */
package MultiProcessorProject;

import java.io.IOException;

/**
 * @author krishnatejaayinala, sindhura
 *
 */
public class MainClass {

	public static void main(String[] args) throws IOException {

		Node node1 = new Node("/src/testcases/testcasenode1.txt", 1);
		Node node2 = new Node("/src/testcases/testcasenode2.txt", 2);
		Node node3 = new Node("/src/testcases/testcasenode3.txt", 3);

		Memory memory = new Memory();
		Bus bus = new Bus();

		int cycle = 0;
		Instruction instruction;
		do {
			cycle++;
			System.out.println("---------cycle:" + cycle + "---------");
			if (!node1.processor.queueProcessor.isEmpty()) {
				instruction = (Instruction) node1.processor.queueProcessor.dequeue();
				node1.l1Controller.queueProcessortoL1C.enqueue(instruction);
				System.out.println("P to L1C: " + instruction.getCommand());
			}
			if (!node2.processor.queueProcessor.isEmpty()) {
				instruction = (Instruction) node2.processor.queueProcessor.dequeue();
				node2.l1Controller.queueProcessortoL1C.enqueue(instruction);
				System.out.println("P to L1C: " + instruction.getCommand());
			}
			if (!node3.processor.queueProcessor.isEmpty()) {
				instruction = (Instruction) node3.processor.queueProcessor.dequeue();
				node3.l1Controller.queueProcessortoL1C.enqueue(instruction);
				System.out.println("P to L1C: " + instruction.getCommand());
			}

			if (!node1.l1Controller.queueProcessortoL1C.isEmpty()) {
				instruction = (Instruction) node1.l1Controller.queueProcessortoL1C.dequeue();
				boolean L1Hit = false;
				if (instruction.getProcessorInstructionKind() == 0) { // Read //
																		// Instruction
					L1Hit = node1.l1Controller.isL1Hit(instruction.getAddress());
					if (L1Hit) {
						System.out.println("L1C to L1D: L1 Hit:  " + instruction.getCommand());
						node1.l1Controller.l1Data.queueL1CtoL1D.enqueue(instruction);
						node1.l1Controller.setState(instruction.getAddress().getAddress(), "Rdwaitd");
					} else {
						int index = Integer.parseInt(instruction.getAddress().getIndex(), 2);
						int tag = Integer.parseInt(instruction.getAddress().getTag(), 2);

						if (node1.l1Controller.isValid(index, tag) == 1) {
							if (node1.l1Controller.isDirty(index, tag) == 1) {
								node1.l1Controller.queueL1CtoBusRequest.enqueue(instruction);
								node1.l1Controller.setState(instruction.getAddress().getAddress(), "Rd2waitd");
								System.out.println("L1C to BusRequest: " + instruction.getCommand());
								System.out.println(
										"L1C main state: missd, state assign: Rd2waitd " + instruction.getCommand());
							} else { // need not write back
								node1.l1Controller.queueL1CtoBusRequest.enqueue(instruction);
								node1.l1Controller.setState(instruction.getAddress().getAddress(), "RdBuswaitd");
								System.out.println("L1C to BusRequest: " + instruction.getCommand());
								System.out.println(
										"L1C main state: missc, state assign: RdBuswaitd " + instruction.getCommand());
							}
							//
						} else {
							// invalid, missi
							node1.l1Controller.queueL1CtoBusRequest.enqueue(instruction);
							node1.l1Controller.setState(instruction.getAddress().getAddress(), "RdBuswaitd");
							System.out.println("L1C to BusRequest: " + instruction.getCommand());
							System.out.println(
									"L1C main state: missi, state assign: RdBuswaitd " + instruction.getCommand());

						}
					}
				} else {
					L1Hit = node1.l1Controller.isL1Hit(instruction.getAddress());
					if (L1Hit) {
						System.out.println("L1C to L1D: L1 Hit:  " + instruction.getCommand());
						node1.l1Controller.l1Data.queueL1CtoL1D.enqueue(instruction);
						node1.l1Controller.setState(instruction.getAddress().getAddress(), "Wrwaitd");
					} else {
						int index = Integer.parseInt(instruction.getAddress().getIndex(), 2);
						int tag = Integer.parseInt(instruction.getAddress().getTag(), 2);
						if (node1.l1Controller.isValid(index, tag) == 1) {
							if (node1.l1Controller.isDirty(index, tag) == 1) {
								node1.l1Controller.queueL1CtoBusRequest.enqueue(instruction);
								node1.l1Controller.setState(instruction.getAddress().getAddress(), "Wr2waitd");
								System.out.println("L1C to BusRequest: " + instruction.getCommand());
								System.out.println(
										"L1C main state: missd, state assign: Wr2waitd " + instruction.getCommand());
							} else { // need not write back
								node1.l1Controller.queueL1CtoBusRequest.enqueue(instruction);
								node1.l1Controller.setState(instruction.getAddress().getAddress(), "WrBuswaitd");
								System.out.println("L1C to BusRequest: " + instruction.getCommand());
								System.out.println(
										"L1C main state: missc, state assign: WrBuswaitd " + instruction.getCommand());
							}
						}
					}
				}

			}

		} while (!node1.processor.queueProcessor.isEmpty() || !node2.processor.queueProcessor.isEmpty() || !node3.processor.queueProcessor.isEmpty());
//		while (!node1.areQueuesEmpty() || !node2.areQueuesEmpty() || !node3.areQueuesEmpty());
	}
}
