import javax.crypto.SecretKey;

public interface SymmetricCryptoTool extends CryptoTool {
	
	SecretKey getSecretKey();

	/**
	 * Method stores secret key info
	 * in parser as key value pairs
	 * @param parser
	 */
	void storeSecretKey(CryptoParser parser);

	/**
	 * Method reads secret key info form parser and stores it.
	 * @param parser
	 */
	void readSecretKey(CryptoParser parser);

}
