package MultiProcessorProject;

/**
 * @author ${Krishna Teja Ayinala, Sindhura Bonthu}
 *
 */
public class ReadInstruction extends Instruction {

	private int byteEnables;
	private char[] readByteEnableddata;
	private char returnSingleCharData;

	public char getSingleCharData() {
		return returnSingleCharData;
	}

	public void setSingleCharData(char returnData) {
		this.returnSingleCharData = returnData;
	}

	public int getByteEnables() {
		return byteEnables;
	}

	public void setByteEnables(int byteEnables) {
		this.byteEnables = byteEnables;
	}

	// private int insAddr;

	// public int getInsAddr() {
	// return insAddr;
	// }
	//
	// public void setInsAddr(int insAddr) {
	// this.insAddr = insAddr;
	// }

	public char[] getByteEnableData() {
		return readByteEnableddata;
	}

	public void setByteEnableData(char[] data) {
		this.readByteEnableddata = data;
	}

}
