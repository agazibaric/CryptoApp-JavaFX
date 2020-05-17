import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class SHA3CryptoHash implements CryptoHash {
	
	public static final List<Integer> HASH_SIZES = Arrays.asList(new Integer[] { 224, 256, 384, 512 });
	
	private int hashSize;

	public SHA3CryptoHash(int hashSize) {
		if (!HASH_SIZES.contains(hashSize))
			throw new RuntimeException("Invalid hash size: " + hashSize);
		this.hashSize = hashSize;
	}

	public SHA3CryptoHash() {
		this(HASH_SIZES.get(0));
	}


	public String hash(String msg) {
		try {
			MessageDigest digest = MessageDigest.getInstance(this.getHashName());
			return Base64.getEncoder().encodeToString(digest.digest(msg.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	@Override
	public String getHashName() {
		return "SHA3-" + this.hashSize;
	}

	@Override
	public int getSize() {
		return hashSize;
	}

	@Override
	public String toString() {
		return "SHA3";
	}
}
