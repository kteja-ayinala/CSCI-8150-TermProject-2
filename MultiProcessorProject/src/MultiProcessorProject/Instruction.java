/**
 * 
 */
package MultiProcessorProject;

/**
 * @author krishnatejaayinala, sindhura
 *
 */
public class Instruction {

	String command;
	int instructioNum;
	int processorInstructionKind;
	Address address;
	// String bAddress;
	Block transferBlock;
	int instructionTransferType;

	public int getInstructionTransferType() {
		return instructionTransferType;
	}

	public void setInstructionTransferType(int instructionTransferType) {
		this.instructionTransferType = instructionTransferType;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getInstructioNum() {
		return instructioNum;
	}

	public void setInstructioNum(int instructioNum) {
		this.instructioNum = instructioNum;
	}

	public int getProcessorInstructionKind() {
		return processorInstructionKind;
	}

	public void setProcessorInstructionKind(int instructionKind) {
		this.processorInstructionKind = instructionKind;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	// public String getbAddress() {
	// return bAddress;
	// }
	//
	// public void setbAddress(String bAddress) {
	// this.bAddress = bAddress;
	// }

	public Block getTransferBlock() {
		return transferBlock;
	}

	public void setTransferBlock(Block transferBlock) {
		this.transferBlock = transferBlock;
	}
}
