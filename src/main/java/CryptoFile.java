public class CryptoFile {

    private CryptoTool tool;
    private String filename;

    public CryptoFile(CryptoTool tool, String filename) {
        this.tool = tool;
        this.filename = filename;
    }

    public void storeData(CryptoParser parser) {
        parser.clear();
        parser.addPair("Description", "Crypted file");
        parser.addPair("Method", tool.toString());
        parser.addPair("File name", filename);
        parser.addPair("Data", tool.encrypt(Utils.readFile(filename)));
    }

}
