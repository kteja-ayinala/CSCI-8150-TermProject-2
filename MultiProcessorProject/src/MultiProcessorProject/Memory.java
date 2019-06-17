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
			// memory[i] = new Block();
			char[] value = new char[32];
			for (int j = 0; j < 32; j++) {
				value[j] = (char) (65 + (j % 26));
			}
			memory[i] = new Block(value);
		}

		// ReadInstruction rdIns0 = new ReadInstruction();
		// rdIns0.setProcessorInstructionKind(0);
		// rdIns0.setAddress(formatAddress(3, memory_Tag, memory_Index,
		// memory_Offset));
		// rdIns0.setByteEnables(4);
		// rdIns0.setInstructioNum(1);
		// rdIns0.setCommand("Read 3 4");
		//
		// ReadInstruction rdIns1 = new ReadInstruction();
		// rdIns1.setProcessorInstructionKind(0);
		// rdIns1.setAddress(formatAddress(4, memory_Tag, memory_Index,
		// memory_Offset));
		// rdIns1.setByteEnables(4);
		// rdIns1.setInstructioNum(1);
		// rdIns1.setCommand("Read 4 4");
		//
		// ReadInstruction rdIns2 = new ReadInstruction();
		// rdIns2.setProcessorInstructionKind(0);
		// rdIns2.setAddress(formatAddress(10, memory_Tag, memory_Index,
		// memory_Offset));
		// rdIns2.setByteEnables(4);
		// rdIns2.setInstructioNum(1);
		// rdIns2.setCommand("Read 10 4");
		//
		// ReadInstruction rdIns3 = new ReadInstruction();
		// rdIns3.setProcessorInstructionKind(0);
		// rdIns3.setAddress(formatAddress(25, memory_Tag, memory_Index,
		// memory_Offset));
		// rdIns3.setByteEnables(4);
		// rdIns3.setInstructioNum(1);
		// rdIns3.setCommand("Read 25 4");
		//
		// ReadInstruction rdIns4 = new ReadInstruction();
		// rdIns4.setProcessorInstructionKind(0);
		// rdIns4.setAddress(formatAddress(50, memory_Tag, memory_Index,
		// memory_Offset));
		// rdIns4.setByteEnables(4);
		// rdIns4.setInstructioNum(1);
		// rdIns4.setCommand("Read 50 4");
		//
		// ReadInstruction rdIns5 = new ReadInstruction();
		// rdIns5.setProcessorInstructionKind(0);
		// rdIns5.setAddress(formatAddress(100, memory_Tag, memory_Index,
		// memory_Offset));
		// rdIns5.setByteEnables(4);
		// rdIns5.setInstructioNum(1);
		// rdIns5.setCommand("Read 100 4");
		//
		// ReadInstruction rdIns6 = new ReadInstruction();
		// rdIns6.setProcessorInstructionKind(0);
		// rdIns6.setAddress(formatAddress(200, memory_Tag, memory_Index,
		// memory_Offset));
		// rdIns6.setByteEnables(4);
		// rdIns6.setInstructioNum(1);
		// rdIns6.setCommand("Read 200 4");
		//
		// ReadInstruction rdIns7 = new ReadInstruction();
		// rdIns7.setProcessorInstructionKind(0);
		// rdIns7.setAddress(formatAddress(400, memory_Tag, memory_Index,
		// memory_Offset));
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

		// memory[0] = new Block(values);
		System.out.println("Instructions added to Memory");

	}
}
