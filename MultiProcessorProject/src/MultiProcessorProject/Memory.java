package MultiProcessorProject;

/**
 * @author ${Krishna Teja Ayinala, Sindhura Bonthu}
 *
 */
public class Memory extends CommonImpl {
	int memory_Tag;
	int memory_Index;
	int memory_Offset;
	int memory_CpuBits;
	int memory_BlockCount;
	int memory_BlockSize;
	Block[] memory;
	public Queue queueMemorytoL2C = new Queue();
	public Queue queueL2CtoMemory = new Queue();

	Memory() {
		memory_Tag = 0;
		memory_Index = 12;
		memory_Offset = 5;
		memory_CpuBits = 17;
		memory_BlockCount = 4096;
		memory_BlockSize = 32;
		memory = new Block[4096];

		for (int i = 0; i < 4096; i++) {
			char[] value = new char[32];
			for (int j = 0; j < 32; j++) {
				value[j] = (char) (65 + (j % 26));
			}
			memory[i] = new Block(value);
		}

		System.out.println("Instructions added to Memory");

	}
}
