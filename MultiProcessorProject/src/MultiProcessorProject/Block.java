/**
 * 
 */
package MultiProcessorProject;

/**
 * @author krishnatejaayinala, sindhura
 *
 */
public class Block {
	int validBit;
	int dirtyBit;
	int tag;
	int offset;
	int blockAddress;
	char data[];
	int lru;

	public Block() {
		validBit = 1;
		dirtyBit = 0;
		tag = 0;
		offset = 0;
		data = new char[32];
	}

	public Block(char mdata[]) {
		validBit = 1;
		dirtyBit = 0;
		tag = 0;
		offset = 0;
		data = new char[32];
		data = mdata.clone();
	}

//	public Block(ReadInstruction mdata[]) {
//		validBit = 1;
//		dirtyBit = 0;
//		tag = 0;
//		offset = 0;
//		rIns = new ReadInstruction[8];
//		rIns = mdata;
//	}

	public Block(char mdata[], int index, int cycle) throws InterruptedException {
		validBit = 1;
		tag = 0;
		offset = 0;
		data = new char[32];
		int counter = 0;
		boolean cycleIncflag = false;
		for (int i = 0; i < 8; i++) { // Read takes 8 cycle since block is 32 B,
										// bus width 32 bits i.e 4B
			int location = index * 32 + counter;
			for (int j = 0; j < 4; j++) {
				data[counter] = mdata[counter]; // each cycle fetches 4 bytes
				counter++;
			}
			if (cycleIncflag) {
				System.out.println("---------cycle:" + cycle + "---------");
				cycle++;
			} else {
				cycleIncflag = true;
				cycle++;
			}
			System.out.println("Reading data from memory " + location);
		}
	}

	public int getValidBit() {
		return validBit;
	}

	public void setValidBit(int validBit) {
		this.validBit = validBit;
	}

	public int getDirtyBit() {
		return dirtyBit;
	}

	public void setDirtyBit(int dirtyBit) {
		this.dirtyBit = dirtyBit;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public char[] getData() {
		return data;
	}

	public void setData(char[] data) {
		this.data = data;
	}

	public void setBitData(int offset, char data) {
		this.data[offset] = data;
		this.setDirtyBit(1);
	}

//	public ReadInstruction[] getrIns() {
//		return rIns;
//	}
//
//	public void setrIns(ReadInstruction[] rIns) {
//		this.rIns = rIns;
//	}
//
//	public WriteInstruction[] getwIns() {
//		return wIns;
//	}
//
//	public void setwIns(WriteInstruction[] wIns) {
//		this.wIns = wIns;
//	}

	public int getBlockAddress() {
		return blockAddress;
	}

	public void setBlockAddress(int blockAddress) {
		this.blockAddress = blockAddress;
	}
	
	public int getLru() {
		return lru;
	}

	public void setLru(int lru) {
		this.lru = lru;
	}

	
}
