/**
 * 
 */
package MultiProcessorProject;

/**
 * @author krishnatejaayinala, sindhura
 *
 */
public class L1Data {

	int l1_Tag;
	int l1_Index;
	int l1_Offset;
	int l1_SetCount;
	int l1_BlockSize;
	int l1_CpuBits;
	public Block way1[];
	public Block way2[];
	public Block way3[];
	public Block way4[];
	public Queue queueL1CtoL1D = new Queue();
	public Queue queueL1DtoL1C = new Queue();

	public L1Data() {
		l1_Tag = 6;
		l1_Index = 6;
		l1_Offset = 5;
		l1_SetCount = 64;
		l1_BlockSize = 32;
		l1_CpuBits = 17;
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
}
