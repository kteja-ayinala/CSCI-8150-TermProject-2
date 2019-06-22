/**
 * 
 */
package MultiProcessorProject;

import java.io.IOException;

/**
 * @author krishnatejaayinala, sindhura
 *
 */
public class Node {

	Processor processor;
	L1Controller l1Controller;

	public Node(String input, int i) throws IOException {
		processor = new Processor(input);
		l1Controller = new L1Controller();
		System.out.println("Node" + i + ": invoked");
	}

	public boolean areQueuesEmpty() {
		if (processor.queueProcessor.isEmpty() && l1Controller.queueL1CtoL1D.isEmpty()
				&& l1Controller.queueL1CtoBusRequest.isEmpty() && l1Controller.queueBustoL1C.isEmpty()
				&& l1Controller.queueL1CtoProcessor.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
