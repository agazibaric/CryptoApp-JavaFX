public interface CryptoTool {

	/**
	 * Encrypts messsage msg.
	 * @param msg
	 * @return
	 */
	String encrypt(String msg);

	/**
	 * Decrypts message encrypted.
	 * @param encrypted
	 * @return
	 */
	String decrypt(String encrypted);

	/**
	 * Returns appropriate name for Cipher object.
	 * @return
	 */
	String getChipherName();

	/**
	 * Returns key size.
	 * @return
	 */
	int getKeySize();

}
