import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCrpytoTool implements SymmetricCryptoTool {

	private int keySize;
	
	public static final List<Integer> KEY_SIZES = Arrays.asList(new Integer[] { 128, 192, 256 });

	public static final List<String> MODES = Arrays.asList(new String[] {
			"ECB", "CBC", "OFB", "CFB", "CTR"
	});

	private final static String DEFAULT_MODE = "CBC";

	private final static List<String> NOT_IVSPEC_MODES = Arrays.asList(new String[] { "ECB" });

	private String mode;

	private SecretKey secretKey;

	private IvParameterSpec ivspec;

	public AESCrpytoTool(int keySize, String mode) {
		this.keySize = keySize;
		this.mode = mode;
		
		this.generateKey();
		this.generateIvSpec();
	}

	public AESCrpytoTool(int keySize) {
		this(keySize, DEFAULT_MODE);
	}
	
	public AESCrpytoTool() {
		this(KEY_SIZES.get(0), DEFAULT_MODE);
	}

	/**
	 * Generates random key.
	 */
	private void generateKey() {
		SecureRandom secureRandom = new SecureRandom();
		KeyGenerator keyGen = null;
		try {
			keyGen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyGen.init(this.keySize, secureRandom);
		this.secretKey = keyGen.generateKey();
	}

	/**
	 * Generates random initialization vector.
	 */
	private void generateIvSpec() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] iv = new byte[16];
		secureRandom.nextBytes(iv);
		this.ivspec = new IvParameterSpec(iv);
	}

	@Override
	public String encrypt(String msg) {
		try {
			Cipher cipher = Cipher.getInstance(this.getChipherName());
			if (NOT_IVSPEC_MODES.contains(this.mode)) {
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			} else {
				cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
			}
			return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	@Override
	public String decrypt(String encrypted) {
		try {
			Cipher cipher = Cipher.getInstance(this.getChipherName());
			if (NOT_IVSPEC_MODES.contains(this.mode)) {
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
			} else {
				cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
			}
			return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}

	@Override
	public String getChipherName() {
		return "AES/" + this.mode + "/PKCS5Padding";
	}

	@Override
	public int getKeySize() {
		return keySize;
	}

	@Override
	public SecretKey getSecretKey() {
		return this.secretKey;
	}

	@Override
	public void storeSecretKey(CryptoParser parser) {
		parser.clear();
		parser.addPair("Description", "Secret key");
		parser.addPair("Method", "AES");
		parser.addPair("Mode", this.mode);
		parser.addPair("Initialization vector", Utils.byteToHex(this.ivspec.getIV()));
		parser.addPair("Key length", Integer.toHexString(this.keySize));
		parser.addPair("Secret key", Utils.byteToHex(secretKey.getEncoded()));
	}

	@Override
	public void readSecretKey(CryptoParser parser) {
		this.mode = parser.getValue("Mode");
		this.keySize = Integer.parseInt(parser.getValue("Key length"), 16);
		byte[] keyBytes = Utils.hexToByte(parser.getValue("Secret key"));
		this.secretKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");

		if (!NOT_IVSPEC_MODES.contains(this.mode)) {
			byte[] ivBytes = Utils.hexToByte(parser.getValue("Initialization vector"));
			this.ivspec = new IvParameterSpec(ivBytes);
		}
	}

	@Override
	public String toString() {
		return "AES";
	}
}
