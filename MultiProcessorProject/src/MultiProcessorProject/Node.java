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
	int nodeNum;

	public Node(String input, int i) throws IOException {
		nodeNum = i;

		processor = new Processor(input);
		l1Controller = new L1Controller();
		System.out.println("Node" + i + ": invoked");
	}

	public boolean areQueuesEmpty() {
		if (processor.queueProcessor.isEmpty() && l1Controller.queueL1CtoL1D.isEmpty()
				&& l1Controller.queueL1CtoBusRequest.isEmpty() && l1Controller.queueBustoL1C.isEmpty()
				&& l1Controller.queueL1CtoProcessor.isEmpty() && l1Controller.queueProcessortoL1C.isEmpty()
				&& processor.queueL1CtoProcessor.isEmpty() && l1Controller.l1Data.queueL1CtoL1D.isEmpty()) {
			return true;
		}
		return false;
	}

	public int getNodeNum() {
		return nodeNum;
	}

	public void setNodeNum(int nodeNum) {
		this.nodeNum = nodeNum;
	}
}
