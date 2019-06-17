/**
 * 
 */
package MultiProcessorProject;

/**
 * @author ${Krishna Teja Ayinala, Sindhura Bonthu}
 *
 */
public class CommonImpl {
	static int cpubits = 17;
	static String curDir = System.getProperty("user.dir");

	public static Address formatAddress(int address, int tagBits, int indexBits, int offsetBits) {
		Address fAddress = new Address();
		String bAddress = convertToBinary(address);
		// System.out.println(bAddress);
		fAddress.setBinaryAddress(bAddress);
		fAddress.setAddress(address);
		fAddress.setTag(bAddress.substring(0, tagBits));
		fAddress.setIndex(bAddress.substring(tagBits, tagBits + indexBits));
		fAddress.setOffset(bAddress.substring(tagBits + indexBits, tagBits + indexBits + offsetBits));
		return fAddress;
	}

	private static String convertToBinary(int address) {
		String bAddress = Integer.toBinaryString(address);
		String finalbAddress = null;
		int bitlen = bAddress.length();
		if (bitlen != cpubits) {
			int additionalBits = cpubits - bitlen;
			String ap = "0";
			for (int i = 0; i < additionalBits - 1; i++) {
				ap = ap + "0";
			}
			finalbAddress = ap + bAddress;
		} else {
			finalbAddress = bAddress;
		}
		return finalbAddress;
	}
}
