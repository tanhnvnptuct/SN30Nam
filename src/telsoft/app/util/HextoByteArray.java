package telsoft.app.util;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author DinhLV
 * @version 1.0
 */

public class HextoByteArray {
	public synchronized static byte[] fromHexString(final String encoded) {
		if((encoded.length() % 2) != 0) {
			throw new IllegalArgumentException(
				"Input string must contain an even number of characters");
		}
		final byte result[] = new byte[encoded.length() / 2];
		final char enc[] = encoded.toCharArray();
		for(int i = 0; i < enc.length; i += 2) {
			StringBuilder curr = new StringBuilder(2);
			curr.append(enc[i]).append(enc[i + 1]);
			result[i / 2] = (byte)Integer.parseInt(curr.toString(), 16);
		}
		return result;
	}
}
