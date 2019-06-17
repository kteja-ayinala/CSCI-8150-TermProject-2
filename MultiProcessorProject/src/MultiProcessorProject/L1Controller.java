/**
 * 
 */
package MultiProcessorProject;

import java.util.HashMap;

/**
 * @author krishnatejaayinala, sindhura
 *
 */
public class L1Controller {

	int l1_Tag;
	int l1_Index;
	int l1_Offset;
	int l1_SetCount;
	int l1_BlockSize;
	int l1_CpuBits;
	Block way1[];
	Block way2[];
	Block way3[];
	Block way4[];
	public Queue queueProcessortoL1C = new Queue();
	public Queue queueL1CtoL1D = new Queue();
	public Queue queueL1CtoL2C = new Queue();
	public Queue queueL1CtoProcessor = new Queue();
	public Queue queueL2CtoL1C = new Queue();
	L1Data l1Data;
	private HashMap<Integer, String> state;

	// Integer is the address and String is the state

	L1Controller() {
		l1_Tag = 6;
		l1_Index = 6;
		l1_Offset = 5;
		l1_SetCount = 64;
		l1_BlockSize = 32;
		l1_CpuBits = 17;

		l1Data = new L1Data();
		state = new HashMap<>();
		// L1 Instruction Cache
		way1 = new Block[64];
		way2 = new Block[64];
		way3 = new Block[64];
		way4 = new Block[64];

		// ReadInstruction rdIns0 = new ReadInstruction();
		// rdIns0.setProcessorInstructionKind(0);
		// rdIns0.setAddress(formatAddress(3, l1_Tag, l1_Index, l1_Offset));
		// rdIns0.setByteEnables(4);
		// rdIns0.setInstructioNum(1);
		// rdIns0.setCommand("Read 3 4");
		//
		// ReadInstruction rdIns1 = new ReadInstruction();
		// rdIns1.setProcessorInstructionKind(0);
		// rdIns1.setAddress(formatAddress(4, l1_Tag, l1_Index, l1_Offset));
		// rdIns1.setByteEnables(4);
		// rdIns1.setInstructioNum(1);
		// rdIns1.setCommand("Read 4 4");
		//
		// ReadInstruction rdIns2 = new ReadInstruction();
		// rdIns2.setProcessorInstructionKind(0);
		// rdIns2.setAddress(formatAddress(10, l1_Tag, l1_Index, l1_Offset));
		// rdIns2.setByteEnables(4);
		// rdIns2.setInstructioNum(1);
		// rdIns2.setCommand("Read 10 4");
		//
		// ReadInstruction rdIns3 = new ReadInstruction();
		// rdIns3.setProcessorInstructionKind(0);
		// rdIns3.setAddress(formatAddress(25, l1_Tag, l1_Index, l1_Offset));
		// rdIns3.setByteEnables(4);
		// rdIns3.setInstructioNum(1);
		// rdIns3.setCommand("Read 25 4");
		//
		// ReadInstruction rdIns4 = new ReadInstruction();
		// rdIns4.setProcessorInstructionKind(0);
		// rdIns4.setAddress(formatAddress(50, l1_Tag, l1_Index, l1_Offset));
		// rdIns4.setByteEnables(4);
		// rdIns4.setInstructioNum(1);
		// rdIns4.setCommand("Read 50 4");
		//
		// ReadInstruction rdIns5 = new ReadInstruction();
		// rdIns5.setProcessorInstructionKind(0);
		// rdIns5.setAddress(formatAddress(100, l1_Tag, l1_Index, l1_Offset));
		// rdIns5.setByteEnables(4);
		// rdIns5.setInstructioNum(1);
		// rdIns5.setCommand("Read 100 4");
		//
		// ReadInstruction rdIns6 = new ReadInstruction();
		// rdIns6.setProcessorInstructionKind(0);
		// rdIns6.setAddress(formatAddress(200, l1_Tag, l1_Index, l1_Offset));
		// rdIns6.setByteEnables(4);
		// rdIns6.setInstructioNum(1);
		// rdIns6.setCommand("Read 200 4");
		//
		// ReadInstruction rdIns7 = new ReadInstruction();
		// rdIns7.setProcessorInstructionKind(0);
		// rdIns7.setAddress(formatAddress(400, l1_Tag, l1_Index, l1_Offset));
		// rdIns7.setByteEnables(4);
		// rdIns7.setInstructioNum(1);
		// rdIns7.setCommand("Read 400 4");
		//
		// ReadInstruction[] values = new ReadInstruction[8];
		// values[0] = rdIns0;
		// values[1] = rdIns1;
		// values[2] = rdIns2;
		// values[3] = rdIns3;
		// values[4] = rdIns4;
		// values[5] = rdIns5;
		// values[6] = rdIns6;
		// values[7] = rdIns7;

		// way1[0].setrIns(values);

		for (int i = 0; i < 64; i++) {
			way1[i] = new Block();
			way1[i].setValidBit(0);
			way2[i] = new Block();
			way2[i].setValidBit(0);
			way3[i] = new Block();
			way3[i].setValidBit(0);
			way4[i] = new Block();
			way4[i].setValidBit(0);
		}
		// way1[0] = new Block(values);
		System.out.println("Instructions added to L1 Instruction Cache");
	}

	public boolean isL1Hit(Address address) {
		int index = Integer.parseInt(address.getIndex(), 2);
		int tag = Integer.parseInt(address.getTag(), 2);
		boolean isInway1 = ((l1Data.way1[index].getTag() == tag) && l1Data.way1[index].getValidBit() == 1);
		boolean isInway2 = ((l1Data.way2[index].getTag() == tag) && l1Data.way2[index].getValidBit() == 1);
		boolean isInway3 = ((l1Data.way3[index].getTag() == tag) && l1Data.way3[index].getValidBit() == 1);
		boolean isInway4 = ((l1Data.way4[index].getTag() == tag) && l1Data.way4[index].getValidBit() == 1);
		if (isInway1 || isInway2 || isInway3 || isInway4) {
			return true;
		}
		return false;
	}

	public int isValid(int index, int tag) {
		int valid = 1;
		if ((l1Data.way1[index].getValidBit() == 0) || (l1Data.way2[index].getValidBit() == 0)
				|| !(l1Data.way3[index].validBit == 0) || !(l1Data.way4[index].validBit == 0)) {
			return 0;
		} else {
			if (l1Data.way1[index].getTag() == tag) {
				valid = l1Data.way1[index].getValidBit();
			}
			if (l1Data.way2[index].tag == tag) {
				valid = l1Data.way2[index].getValidBit();
			}
			if (l1Data.way3[index].tag == tag) {
				valid = l1Data.way3[index].getValidBit();
			}
			if (l1Data.way4[index].tag == tag) {
				valid = l1Data.way4[index].getValidBit();
			}
			return valid;
		}
	}

	public int isDirty(int index, int tag) {
		int dirty = 0;
		if (l1Data.way1[index].getTag() == tag) {
			dirty = l1Data.way1[index].getDirtyBit();
			return dirty;
		}
		if (l1Data.way2[index].getTag() == tag) {
			dirty = l1Data.way2[index].getDirtyBit();
			return dirty;
		}
		if (l1Data.way3[index].getTag() == tag) {
			dirty = l1Data.way3[index].getDirtyBit();
			return dirty;
		}
		if (l1Data.way4[index].getTag() == tag) {
			dirty = l1Data.way4[index].getDirtyBit();
			return dirty;
		}
		return dirty;
	}

	public void l1writeChar(char singleCharData, Address fAddress) {

		int tag = Integer.parseInt(fAddress.getTag());
		int index = Integer.parseInt(fAddress.getIndex(), 2);
		int offset = Integer.parseInt(fAddress.getOffset(), 2);
		if (l1Data.way1[index].getTag() == tag) {
			l1Data.way1[index].setBitData(offset, singleCharData);
			l1Data.way1[index].setDirtyBit(1);
		} else if (l1Data.way2[index].getTag() == tag) {
			l1Data.way2[index].setBitData(offset, singleCharData);
			l1Data.way2[index].setDirtyBit(1);
		} else if (l1Data.way3[index].getTag() == tag) {
			l1Data.way3[index].setBitData(offset, singleCharData);
			l1Data.way3[index].setDirtyBit(1);
		} else if (l1Data.way4[index].getTag() == tag) {
			l1Data.way4[index].setBitData(offset, singleCharData);
			l1Data.way4[index].setDirtyBit(1);
		}
	}

	Block block = null;

	public Block readBlock(Address fAddress) {
		int tag = Integer.parseInt(fAddress.getTag(), 2);
		int index = Integer.parseInt(fAddress.getIndex(), 2);

		if (l1Data.way1[index].getTag() == tag) {
			block = l1Data.way1[index];
		} else if (l1Data.way2[index].getTag() == tag) {
			block = l1Data.way2[index];
		} else if (l1Data.way3[index].getTag() == tag) {
			block = l1Data.way3[index];
		} else if (l1Data.way4[index].getTag() == tag) {
			block = l1Data.way4[index];
		}
		return block;
	}

	public void l1write(Block transferBlock, Address fAddress) {
		int index = Integer.parseInt(fAddress.getIndex(), 2);
		int tag = Integer.parseInt(fAddress.getTag(), 2);
		if (l1Data.way1[index].getValidBit() == 0) {
			l1Data.way1[index] = transferBlock;
			l1Data.way1[index].lru = 0;
			// return true;
		} else if (l1Data.way2[index].getValidBit() == 0) {
			l1Data.way2[index] = transferBlock;
			l1Data.way2[index].lru = 1;
			// return true;
		} else if (l1Data.way3[index].getValidBit() == 0) {
			l1Data.way3[index] = transferBlock;
			l1Data.way3[index].lru = 2;
			// return true;
		} else if (l1Data.way4[index].getValidBit() == 0) {
			l1Data.way4[index] = transferBlock;
			l1Data.way4[index].lru = 3;
			// return true;
		} else {
			if (l1Data.way1[index].getTag() == tag) {
				handleExistingblock(index, l1Data.way1[index].getLru(), fAddress);
				l1Data.way1[index].setLru(3);

			} else if (l1Data.way2[index].getLru() == tag) {
				handleExistingblock(index, l1Data.way2[index].getLru(), fAddress);
				l1Data.way2[index].setLru(3);

			} else if (l1Data.way3[index].getLru() == tag) {
				handleExistingblock(index, l1Data.way3[index].getLru(), fAddress);
				l1Data.way3[index].setLru(3);

			} else if (l1Data.way4[index].getLru() == tag) {
				handleExistingblock(index, l1Data.way4[index].getLru(), fAddress);
				l1Data.way4[index].setLru(3);

			} else {
				reduceCount(index);
				handleNewBlock(index, transferBlock, fAddress);
			}
		}

	}

	private void reduceCount(int index) {
		l1Data.way1[index].setLru(l1Data.way1[index].getLru() - 1);
		l1Data.way2[index].setLru(l1Data.way2[index].getLru() - 1);
		l1Data.way3[index].setLru(l1Data.way3[index].getLru() - 1);
		l1Data.way4[index].setLru(l1Data.way4[index].getLru() - 1);
	}

	private void handleNewBlock(int index, Block transferBlock, Address fAddress) {
		Block vBlock = null;
		if (l1Data.way1[index].getLru() == -1) {
			vBlock = l1Data.way1[index];
			l1Data.way1[index] = transferBlock;
			l1Data.way1[index].setLru(3);
		}
		if (l1Data.way2[index].getLru() == -1) {
			vBlock = l1Data.way2[index];
			l1Data.way2[index] = transferBlock;
			l1Data.way2[index].setLru(3);
		}
		if (l1Data.way3[index].getLru() == -1) {
			vBlock = l1Data.way3[index];
			l1Data.way3[index] = transferBlock;
			l1Data.way3[index].setLru(3);
		}
		if (l1Data.way4[index].getLru() == -1) {
			vBlock = l1Data.way4[index];
			l1Data.way4[index] = transferBlock;
			l1Data.way4[index].setLru(3);
		}

	}

	private void handleExistingblock(int index, int lru, Address fAddress) {

		if (l1Data.way1[index].getLru() > lru) {
			l1Data.way1[index].setLru(l1Data.way1[index].getLru() - 1);
		}
		if (l1Data.way2[index].getLru() > lru) {
			l1Data.way2[index].setLru(l1Data.way2[index].getLru() - 1);
		}
		if (l1Data.way3[index].getLru() > lru) {
			l1Data.way3[index].setLru(l1Data.way3[index].getLru() - 1);
		}
		if (l1Data.way4[index].getLru() > lru) {
			l1Data.way4[index].setLru(l1Data.way4[index].getLru() - 1);
		}
	}

	public void setDirty(int parseInt, int parseInt2, int i) {
		// TODO Auto-generated method stub

	}

	public static void setvalid(int parseInt, int parseInt2, int i) {
		// TODO Auto-generated method stub

	}

	public String getState(int address) {
		return state.get(address);
	}

	public void setState(int address, String string) {
		state.put(address, string);

	}

	public void clear(int address) {
		state.remove(address);
	}

}
