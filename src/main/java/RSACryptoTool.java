import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;

public class RSACryptoTool implements AsymmetricCryptoTool {

	public static final List<Integer> KEY_SIZES = Arrays.asList(new Integer[] { 1024, 2048, 4096 });

	private int keySize;

	private PrivateKey privateKey;

	private PublicKey publicKey;

	public RSACryptoTool(int keySize) {
		this.keySize = keySize;
		this.generateKey();
	}

	public RSACryptoTool() {
		this(KEY_SIZES.get(0));
	}

	private void generateKey() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(this.keySize);
			KeyPair pair = keyGen.generateKeyPair();
			this.privateKey = pair.getPrivate();
			this.publicKey = pair.getPublic();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String encrypt(String msg) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes("UTF-8")));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public String decrypt(String encrypted) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)), "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public String getChipherName() {
		return "RSA";
	}

	@Override
	public int getKeySize() {
		return keySize;
	}

	@Override
	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	@Override
	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	@Override
	public void storePrivateKey(CryptoParser parser) {
		parser.clear();
		parser.addPair("Description", "Private key");
		parser.addPair("Method", "RSA");
		parser.addPair("Key length", Integer.toHexString(this.keySize));
		parser.addPair("Modulus", getModulus());
		parser.addPair("Private exponent", getPrivateExponent());
	}

	@Override
	public void storePublicKey(CryptoParser parser) {
		parser.clear();
		parser.addPair("Description", "Public key");
		parser.addPair("Method", "RSA");
		parser.addPair("Key length", Integer.toHexString(this.keySize));
		parser.addPair("Modulus", getModulus());
		parser.addPair("Public exponent", getPublicExponent());
	}

    @Override
    public void readPrivateKey(CryptoParser parser) {
        this.keySize = Integer.parseInt(parser.getValue("Key length"), 16);
        BigInteger modulus = new BigInteger(Utils.hexToByte(parser.getValue("Modulus")));
        BigInteger exponent = new BigInteger(Utils.hexToByte(parser.getValue("Private exponent")));
		RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, exponent);
		try {
			KeyFactory kf = KeyFactory.getInstance("RSA");
			this.privateKey = kf.generatePrivate(spec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
    }

    @Override
    public void readPublicKey(CryptoParser parser) {
        this.keySize = Integer.parseInt(parser.getValue("Key length"), 16);
	    BigInteger modulus = new BigInteger(Utils.hexToByte(parser.getValue("Modulus")));
        BigInteger exponent = new BigInteger(Utils.hexToByte(parser.getValue("Public exponent")));
        //this.publicKey = (PublicKey) new RSAPublicKeySpec(modulus, exponent);
		RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
		try {
			KeyFactory kf = KeyFactory.getInstance("RSA");
			this.publicKey = kf.generatePublic(spec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}

    public String getPublicExponent() {
		RSAPublicKey rsaPublic  = (RSAPublicKey) this.publicKey;
		return Utils.byteToHex(rsaPublic.getPublicExponent().toByteArray());
	}

	public String getPrivateExponent() {
		RSAPrivateKey rsaPrivate = (RSAPrivateKey) this.privateKey;
		return Utils.byteToHex(rsaPrivate.getPrivateExponent().toByteArray());
	}

	public String getModulus() {
		RSAPublicKey rsaPublic  = (RSAPublicKey) this.publicKey;
		return Utils.byteToHex(rsaPublic.getModulus().toByteArray());
	}

    @Override
    public String toString() {
        return "RSA";
    }
}
