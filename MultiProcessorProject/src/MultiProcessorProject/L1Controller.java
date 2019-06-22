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
	public Queue queueL1CtoBusRequest = new Queue();
	public Queue queueL1CtoBusResponse = new Queue();
	public Queue queueL1CtoProcessor = new Queue();
	public Queue queueBustoL1C = new Queue();
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
