import java.security.PrivateKey;
import java.security.PublicKey;

public interface AsymmetricCryptoTool extends CryptoTool {
	
	PrivateKey getPrivateKey();
	
	PublicKey getPublicKey();

	/**
	 * Method stores private key info into parser.
	 * @param parser
	 */
	void storePrivateKey(CryptoParser parser);

	/**
	 * Method stores public key info into parser.
	 * @param parser
	 */
	void storePublicKey(CryptoParser parser);

	/**
	 * Method reads private key info from parser and stores it.
	 * @param parser
	 */
	void readPrivateKey(CryptoParser parser);

	/**
	 * Method reads public key info from parser and stores it.
	 * @param parser
	 */
	void readPublicKey(CryptoParser parser);

}
