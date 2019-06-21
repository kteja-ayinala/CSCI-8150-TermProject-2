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

	// private String pInput = null;;

	public Node(String input) throws IOException {
		// pInput = input;
		Processor processor = new Processor(input);
		L1Controller l1Controller = new L1Controller();
		L1Data l1Data = new L1Data();
	}

}
