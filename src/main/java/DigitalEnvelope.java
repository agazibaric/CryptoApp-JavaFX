import java.io.UnsupportedEncodingException;

public class DigitalEnvelope {
	
	private SymmetricCryptoTool stool;
	
	private AsymmetricCryptoTool atool;

	public DigitalEnvelope(SymmetricCryptoTool stool, AsymmetricCryptoTool atool) {
		this.stool = stool;
		this.atool = atool;
	}

	public void storeData(CryptoParser parser, String filename) {
		parser.clear();
		parser.addPair("Description", "Envelope");
		parser.addPair("File name", filename);
		parser.addPair("Method", stool.toString() + "\n    " + atool.toString());
		parser.addPair("Key length",
				Integer.toHexString(stool.getKeySize()) + "\n    " +
				Integer.toHexString(atool.getKeySize()));

		parser.addPair("Envelope data", stool.encrypt(Utils.readFile(filename)));
		try {
			String cryptKey = atool.encrypt(new String(stool.getSecretKey().getEncoded(), "UTF-8"));
			cryptKey = Utils.byteToHex(cryptKey.getBytes("UTF-8"));
			parser.addPair("Envelope crypt key", cryptKey);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String decryptEnvelope(CryptoParser parser) {
		// Original file from which envelope was created

		String encryptedData = parser.getValue("Envelope data");
		/*String encryptedSymmetricKey = parser.getValue("Envelope crypt key");
		String decryptedSymmetricKey = atool.decrypt(encryptedSymmetricKey);*/
		String decryptedData = stool.decrypt(encryptedData);
		return decryptedData;
	}

}
