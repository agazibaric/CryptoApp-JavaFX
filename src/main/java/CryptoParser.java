import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class that knows how to read and write cryptographic files.
 */
public class CryptoParser {

    public static final String BEGIN_TAG = "---BEGIN OS2 CRYPTO DATA---";
    public static final String END_TAG = "---END OS2 CRYPTO DATA---";
    public static final String INDENT = "    ";

    private Path filePath;

    private Map<String, String> map = new HashMap<>();

    private List<String> keys = new ArrayList<>();

    private boolean writeRaw = false;

    public CryptoParser(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Parses cryptographic file and stores it in a map (key - value pairs).
     */
    public void parseFile() {
        this.clear();
        try {
            StringBuilder sb = new StringBuilder();
            String currentKey = null;
            boolean start = false;
            for (String line : Files.readAllLines(filePath)) {
                if (line.isEmpty()) continue;
                if (line.equals(END_TAG)) {
                    String key = currentKey.substring(0, currentKey.length() - 1);
                    map.put(key, sb.toString());
                    keys.add(key);
                    break;
                }

                if (start) {
                    if (line.startsWith(INDENT)) {
                        int len = sb.length();
                        if (len > 0 && len < 60) {
                            sb.append("\n    ");
                        }
                        sb.append(line.trim());
                    } else {
                        if (currentKey == null) {
                            currentKey = line;
                        } else {
                            String key = currentKey.substring(0, currentKey.length() - 1);
                            map.put(key, sb.toString());
                            keys.add(key);
                            sb.setLength(0);  // Clear string builder
                            currentKey = line;
                        }
                    }
                } else if (line.equals(BEGIN_TAG)) {
                    start = true;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPair(String key, String value) {
        if (!keys.contains(key)) {
            keys.add(key);
        }
        map.put(key, value);
    }

    public void clear() {
        keys.clear();
        map.clear();
    }

    public String getValue(String key) {
        return map.get(key);
    }

    public void setFilePath(String filepath) {
        this.filePath = Paths.get(filepath);
    }

    public void writeToFile() {
        try {
            if (writeRaw) {
                List<String> rawData = List.of(map.get("Original data"));
                Files.write(this.filePath, rawData, StandardCharsets.UTF_8);
            } else {
                Files.write(this.filePath, getLines(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLines() {
        List<String> lines = new LinkedList<>();
        lines.add(BEGIN_TAG);
        for (String key : keys) {
            lines.add(key + ":");
            String value = map.get(key);
            for (String c : value.split("(?<=\\G.{60})")) {
                lines.add(INDENT + c);
            }
            lines.add("");
        }
        lines.add(END_TAG);
        return lines;
    }

    public boolean isWriteRaw() {
        return writeRaw;
    }

    public void setWriteRaw(boolean writeRaw) {
        this.writeRaw = writeRaw;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (writeRaw) {
            sb.append(map.get("Original data"));
        } else {
            for (String line : getLines()) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
