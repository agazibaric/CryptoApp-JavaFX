public interface CryptoHash {
	
	String hash(String msg);
	
	String getHashName();

	int getSize();

}
