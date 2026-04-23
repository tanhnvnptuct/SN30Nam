package vnp.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * <p>Title: He thong doi soat so lieu</p>
 *
 * <p>Description: He thong doi soat so lieu thue bao tra truoc</p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: Billing Center - Vinaphone</p>
 *
 * @author Nguyen Ngoc Tuan
 * @version 1.0
 */

public class SecretWriting {
//	public void SecretWriting() {
//	}
//
//	////////////////////////////////////////////////////////
//	public static Key getKeyFile(String strKeyFile) throws Exception {
//		Key keyReturn = null;
//		ObjectInputStream in = null;
//		try {
//			in = new ObjectInputStream(new FileInputStream(strKeyFile));
//			keyReturn = (Key)in.readObject();
//		} catch(Exception e) {
//			e.printStackTrace();
//		} finally {
//			in.close();
//			return keyReturn;
//		}
//	}
//
//	////////////////////////////////////////////////////////
//	/**
//	 * Get Key file, if Key file is not exist, create new Key file
//	 * @param strKeyFile String
//	 * @return Key
//	 * @throws Exception
//	 */
//	public static Key getKeyResource(InputStream str) throws Exception {
//		Key keyReturn = null;
//		try {
//			ObjectInputStream in = new ObjectInputStream(str);
//			keyReturn = (Key)in.readObject();
//			in.close();
//		} catch(Exception fnfe) {
//			KeyGenerator generator = KeyGenerator.getInstance("DES");
//			generator.init(new SecureRandom());
//			keyReturn = generator.generateKey();
//		}
//		return keyReturn;
//	}
//
//	////////////////////////////////////////////////////////
//	public static String encryptString(String strKeyFile,
//									   String strInput) throws Exception {
//		try {
//			Key myKey = getKeyFile(strKeyFile);
//			// Get a cipher object.
//			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//			// Encrypt the input string.
//			cipher.init(Cipher.ENCRYPT_MODE, myKey);
//			byte[] stringBytes = strInput.getBytes("UTF8");
//			byte[] raw = cipher.doFinal(stringBytes);
//			BASE64Encoder encoder = new BASE64Encoder();
//			String base64 = encoder.encode(raw);
//			return base64;
//		} catch(Exception ex) {
//			ex.printStackTrace();
//			throw new Exception("Error encrypting string: " + ex.getMessage());
//		}
//	}
//
//	////////////////////////////////////////////////////////
//	/**
//	 * decrypt File
//	 * @param strInput String
//	 * @return String
//	 * @throws Exception
//	 */
//	public static String decryptString(String strKeyFile,
//									   String strInput) throws Exception {
//		try {
//			Key myKey = getKeyFile(strKeyFile);
//			// Get a cipher object.
//			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//			// Decrypt the input string.
//			cipher.init(Cipher.DECRYPT_MODE, myKey);
//			BASE64Decoder decoder = new BASE64Decoder();
//			byte[] raw = decoder.decodeBuffer(strInput);
//			byte[] stringBytes = cipher.doFinal(raw);
//			String result = new String(stringBytes, "UTF8");
//			return result;
//		} catch(Exception ex) {
//			throw new Exception("Error decrypting string: " + ex.getMessage());
//		}
//	}
//
//	////////////////////////////////////////////////////////
//	public static String decryptResourceString(InputStream strKeyFile,
//											   String strInput) throws Exception {
//		try {
//			Key myKey = SecretWriting.getKeyResource(strKeyFile);
//			// Get a cipher object.
//			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//			// Decrypt the input string.
//			cipher.init(Cipher.DECRYPT_MODE, myKey);
//			BASE64Decoder decoder = new BASE64Decoder();
//			byte[] raw = decoder.decodeBuffer(strInput);
//			byte[] stringBytes = cipher.doFinal(raw);
//			String result = new String(stringBytes, "UTF8");
//			return result;
//		} catch(Exception ex) {
//			throw new Exception("Error decrypting string: " + ex.getMessage());
//		}
//	}
//
//	////////////////////////////////////////////////////////
//	public static String encryptString(InputStream strKeyFile,
//									   String strInput) throws Exception {
//		try {
//			Key myKey = SecretWriting.getKeyResource(strKeyFile);
//			// Get a cipher object.
//			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//			// Encrypt the input string.
//			cipher.init(Cipher.ENCRYPT_MODE, myKey);
//			byte[] stringBytes = strInput.getBytes("UTF8");
//			byte[] raw = cipher.doFinal(stringBytes);
//			BASE64Encoder encoder = new BASE64Encoder();
//			String base64 = encoder.encode(raw);
//			return base64;
//		} catch(Exception ex) {
//			ex.printStackTrace();
//			throw new Exception("Error encrypting string: " + ex.getMessage());
//		}
//	}
//
//	////////////////////////////////////////////////////////
//	public static String EncryptVoucherGate(String strKey,
//								 			String strData) throws Exception {
//		try {
//			// Get a cipher object.
//			Cipher cipher = Cipher.getInstance("TripleDES");
//			MessageDigest md5 = MessageDigest.getInstance("MD5");
//			md5.update(strKey.getBytes(), 0, strKey.length());
//			String keymd5 = new BigInteger(1, md5.digest()).toString(16).substring(0, 24);
//			SecretKeySpec keyspec = new SecretKeySpec(keymd5.getBytes(), "TripleDES");
//			// Encrypt the input string.
//			cipher.init(Cipher.ENCRYPT_MODE, keyspec);
//			byte[] stringBytes = strData.getBytes();
//			byte[] raw = cipher.doFinal(stringBytes);
//			BASE64Encoder encoder = new BASE64Encoder();
//			return encoder.encode(raw);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			throw new Exception("Error encrypting string: " + ex.getMessage());
//		}
//	}
//
//	////////////////////////////////////////////////////////
//	public static String DecryptVoucherGate(String strKey,
//								 			String strData) throws Exception {
//		try {
//			// Get a cipher object.
//			Cipher cipher = Cipher.getInstance("TripleDES");
//			MessageDigest md5 = MessageDigest.getInstance("MD5");
//			md5.update(strKey.getBytes(), 0, strKey.length());
//			String keymd5 = new BigInteger(1, md5.digest()).toString(16).substring(0, 24);
//			SecretKeySpec keyspec = new SecretKeySpec(keymd5.getBytes(), "TripleDES");
//			// Decrypt the input string.
//			cipher.init(Cipher.DECRYPT_MODE, keyspec);
//			BASE64Decoder decoder = new BASE64Decoder();
//			byte[] raw = decoder.decodeBuffer(strData);
//			byte[] stringBytes = cipher.doFinal(raw);
//			return new String(stringBytes);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			throw new Exception("Error encrypting string: " + ex.getMessage());
//		}
//	}
}
