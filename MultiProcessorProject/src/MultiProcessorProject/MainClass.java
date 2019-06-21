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
			
			
		} while (!node1.areQueuesEmpty() || !node2.areQueuesEmpty() || !node3.areQueuesEmpty());
	}
}
