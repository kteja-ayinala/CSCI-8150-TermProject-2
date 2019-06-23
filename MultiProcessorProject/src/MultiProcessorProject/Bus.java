/**
 * 
 */
package MultiProcessorProject;

import java.util.HashMap;

/**
 * @author {Krishna Teja Ayinala, Sindhura Bonthu}
 *
 */

// conncetion between nodes and the memory
public class Bus {
	private HashMap<Integer, String> state;

	public Block data = null;
	public Instruction dataIns = null;
	public Queue queueBusRequest = null;
	public Queue queueBusRequestStalled = null;
	public Queue queueBusResponse = null;
	public Queue queueBusData = null;
	public Queue queueAckFromPeers = null;
	public Queue queueBustoMemory = null;
	public int totalNodes = 3;
	public boolean isBusBusy = false;

	Bus() {
		queueBusRequest = new Queue();
		queueBusResponse = new Queue();
		queueBusRequestStalled = new Queue();
		queueBustoMemory = new Queue();
		queueAckFromPeers = new Queue(64, "StrQueue");
		state = new HashMap<>();

	}

	public String getState(int address) {
		return state.get(address);
	}

	public void setState(int address, String string) {
		state.put(address, string);

	}
}
