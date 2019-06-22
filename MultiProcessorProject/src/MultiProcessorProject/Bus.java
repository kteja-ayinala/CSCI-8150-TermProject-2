/**
 * 
 */
package MultiProcessorProject;

/**
 * @author {Krishna Teja Ayinala, Sindhura Bonthu}
 *
 */

// conncetion between nodes and the memory
public class Bus {
	public Block data = null;
	public Instruction dataIns = null;
	public Queue queueBusRequest = null;
	public Queue queueBusResponse = null;
	public Queue queueBusData = null;
	public int totalNodes = 3;
	public boolean isBusBusy = false;

	Bus() {
		queueBusRequest = new Queue();
		queueBusResponse = new Queue();

	}
}
