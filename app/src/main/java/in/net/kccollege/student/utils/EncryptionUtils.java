package in.net.kccollege.student.utils;


import se.simbio.encryption.Encryption;

/**
 * Created by Sahil on 01/11/2015.
 */
public class EncryptionUtils {

	private static Encryption enc;


	public static String encrypt(String key, String salt, String data) {
		String encrypted;

		enc = Encryption.getDefault(key, salt, new byte[16]);
		encrypted = enc.encryptOrNull(data);
		return encrypted;
	}

	public static String decrypt(String key, String salt, String data) {
		String decrypted;

		enc = Encryption.getDefault(key, salt, new byte[16]);
		decrypted = enc.decryptOrNull(data);


		return decrypted;
//        return decrypted==null?"":decrypted;
	}

}
