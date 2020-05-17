import java.io.UnsupportedEncodingException;

public class DigitalSignature {
	
	private SymmetricCryptoTool stool;
	
	private CryptoHash hasher;

	public DigitalSignature(SymmetricCryptoTool stool, CryptoHash hasher) {
		this.stool = stool;
		this.hasher = hasher;
	}
	
	public String process(String msg) {
		// Make HEX representation of signature
		try {
			String msgHash = this.hasher.hash(msg);
			String cryptKey = stool.encrypt(msgHash);
			return Utils.byteToHex(cryptKey.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void storeData(CryptoParser parser, String filename) {
		parser.clear();
		parser.addPair("Description", "Signature");
		parser.addPair("File name", filename);
		parser.addPair("Method", hasher.toString() + "\n    " + stool.toString());
		parser.addPair("Key length",
				Integer.toHexString(hasher.getSize()) + "\n    " +
						Integer.toHexString(stool.getKeySize()));

		parser.addPair("Signature", process(Utils.readFile(filename)));
	}

	public void storeData(CryptoParser parser, CryptoParser envelopeParser) {
		parser.clear();
		parser.addPair("Description", "Signature");
		parser.addPair("File name", envelopeParser.getValue("File name"));
		parser.addPair("Method", hasher.toString() + "\n    " + stool.toString());
		parser.addPair("Key length",
				Integer.toHexString(hasher.getSize()) + "\n    " +
						Integer.toHexString(stool.getKeySize()));

		parser.addPair("Signature", process(envelopeParser.toString()));
	}

	public boolean verify(CryptoParser parser) {
		String filename = parser.getValue("File name");
		String calculatedSig = process(Utils.readFile(filename));
		String signature = parser.getValue("Signature");
		return calculatedSig.equals(signature);
	}

}
