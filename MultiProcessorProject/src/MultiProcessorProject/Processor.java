/**
 * 
 */
package MultiProcessorProject;

/**
 * @author krishnatejaayinala, sindhura
 *
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Processor extends CommonImpl {
	public Queue queueProcessor = new Queue();
	public Queue queueProcessortoL1C = new Queue();
	public Queue queueL1CtoProcessor = new Queue();

	Processor(String input) throws IOException {
		String testcaseFile = curDir + input;
		// System.out.println(curDir);
		BufferedReader reader = new BufferedReader(new FileReader(testcaseFile));
		String test = null;
		int insCount = 0;
		while ((test = reader.readLine()) != null) {
			String command = test;
			int instructionNum = ++insCount;
			String kind = test.split(" ")[0];
			String address = test.split(" ")[1];
			Address fAddress = formatAddress(Integer.parseInt(address), 6, 6, 5);
			if (kind.equalsIgnoreCase("R")) {
				int instructionKind = 0;
				String byteEnables = test.split(" ")[2];
				ReadInstruction ins = new ReadInstruction();
				ins.setCommand(command);
				ins.setInstructioNum(instructionNum);
				ins.setProcessorInstructionKind(instructionKind);
				ins.setAddress(fAddress);
				ins.setByteEnables(Integer.parseInt(byteEnables));
				ins.getInstructionNode();
				queueProcessor.enqueue(ins);
				System.out.println("r enqued");
			} else if (kind.equalsIgnoreCase("W")) {
				int instructionKind = 1;
				char data = test.split(" ")[2].charAt(0);
				WriteInstruction ins = new WriteInstruction();
				ins.setCommand(command);
				ins.setInstructioNum(instructionNum);
				ins.setProcessorInstructionKind(instructionKind);
				ins.setAddress(fAddress);
				ins.setWriteData(data);
				queueProcessor.enqueue(ins);
				System.out.println("w enqued");
			} else {
				System.out.println("Inappropriate instruction format");
			}
		}
		reader.close();
		// System.out.println("Processor read");
	}
}
