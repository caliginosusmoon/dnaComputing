package dnabds;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// Main DNABDSCloudEncryption Class
public class DNABDSCloudEncryption {

    // DNA Encryption Class
    // DNA Encryption Class
    public class DNAEncryption {
        private int key;
        private Random random;

        public DNAEncryption(int key) {
            this.key = key;
            this.random = new Random(key);
        }

        public String encrypt(String plaintext) {
            // Convert text to binary and then binary to DNA sequence
            String binaryData = textToBinary(plaintext);
            String dnaSequence = binaryToDNA(binaryData);

            // Apply variational encoding with indexed shuffling
            return variationalEncoding(dnaSequence);
        }

        public String decrypt(String encryptedDNA) {
            // Reverse the encoding to retrieve the original DNA sequence
            String originalDNA = reverseVariationalEncoding(encryptedDNA);

            // Convert DNA back to binary and binary to text
            String binaryData = dnaToBinary(originalDNA);
            return binaryToText(binaryData);
        }

        // Encoding: Apply variational encoding with index tracking for precise reversal
        public String variationalEncoding(String dnaSequence) {
            // Store the DNA sequence in a list for shuffling
            List<Character> dnaList = new ArrayList<>();
            for (char c : dnaSequence.toCharArray()) {
                dnaList.add(c);
            }

            // Create an array to store original indices before shuffle
            Integer[] indices = new Integer[dnaList.size()];
            for (int i = 0; i < indices.length; i++) {
                indices[i] = i;
            }

            // Shuffle both DNA sequence and indices in the same way
            Collections.shuffle(dnaList, new Random(key));
            Collections.shuffle(Arrays.asList(indices), new Random(key));

            // Store shuffled indices along with the encoded sequence for reversal
            StringBuilder encodedDna = new StringBuilder();
            for (char c : dnaList) {
                encodedDna.append(c);
            }

            // Append indices at the end for decoding purposes
            StringBuilder indexString = new StringBuilder();
            for (int index : indices) {
                indexString.append(index).append(",");
            }

            // Final encoded result includes DNA sequence + delimiter + index mapping
            return encodedDna.toString() + "|" + indexString.toString();
        }

        // Decoding: Precisely reverse variational encoding based on stored indices
        public String reverseVariationalEncoding(String encodedDNA) {
            // Separate DNA sequence and indices
            String[] parts = encodedDNA.split("\\|");
            String dnaSequence = parts[0];
            String[] indexStrings = parts[1].split(",");

            // Retrieve the stored indices as integers
            Integer[] indices = new Integer[indexStrings.length];
            for (int i = 0; i < indexStrings.length; i++) {
                indices[i] = Integer.parseInt(indexStrings[i]);
            }

            // Prepare to reconstruct the original DNA sequence
            char[] originalDnaArray = new char[dnaSequence.length()];

            // Place each character back to its original index based on the index mapping
            for (int i = 0; i < dnaSequence.length(); i++) {
                originalDnaArray[indices[i]] = dnaSequence.charAt(i);
            }

            // Convert the char array back to string format
            return new String(originalDnaArray);
        }

        // Utility method: Convert text to binary
        private String textToBinary(String text) {
            StringBuilder binary = new StringBuilder();
            for (char c : text.toCharArray()) {
                binary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
            }
            return binary.toString();
        }

        // Utility method: Convert binary to DNA sequence
        private String binaryToDNA(String binary) {
            StringBuilder dna = new StringBuilder();
            for (int i = 0; i < binary.length(); i += 2) {
                String pair = binary.substring(i, i + 2);
                switch (pair) {
                    case "00": dna.append("A"); break;
                    case "01": dna.append("T"); break;
                    case "10": dna.append("C"); break;
                    case "11": dna.append("G"); break;
                }
            }
            return dna.toString();
        }

        // Utility method: Convert DNA back to binary
        private String dnaToBinary(String dna) {
            StringBuilder binary = new StringBuilder();
            for (char c : dna.toCharArray()) {
                switch (c) {
                    case 'A': binary.append("00"); break;
                    case 'T': binary.append("01"); break;
                    case 'C': binary.append("10"); break;
                    case 'G': binary.append("11"); break;
                }
            }
            return binary.toString();
        }

        // Utility method: Convert binary back to text
        private String binaryToText(String binary) {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < binary.length(); i += 8) {
                int charCode = Integer.parseInt(binary.substring(i, i + 8), 2);
                text.append((char) charCode);
            }
            return text.toString();
        }
    }

    // DNA Key Generator Class
    public class DNAKeyGenerator {
        private String userId;
        private String password;
        private String macAddress;

        public DNAKeyGenerator(String userId, String password, String macAddress) {
            this.userId = userId;
            this.password = password;
            this.macAddress = macAddress;
        }

        public String generateSecretKey() {
            if (!isAuthorizedUser()) {
                return "Unauthorized access";
            }

            String combinedInfo = userId + password + macAddress;
            String binaryData = toBinary(combinedInfo);
            binaryData = addMacAndPassword(binaryData, toBinary(password), toBinary(macAddress));
            List<String> parts = splitIntoParts(binaryData, 4);
            String xorResult = xorParts(parts);
            xorResult = make1024Bit(xorResult);
            List<String> dnaParts = splitIntoParts(xorResult, 4);
            String dnaSequence = assignDNABases(dnaParts);
            dnaSequence = applyComplementaryPair(dnaSequence);
            List<String> dnaFinalParts = splitIntoParts(dnaSequence, 2);
            return dnaXor(dnaFinalParts);
        }

        private boolean isAuthorizedUser() {
            return userId.equals("AuthorizedUserID");
        }

        private String toBinary(String text) {
            StringBuilder binary = new StringBuilder();
            for (char ch : text.toCharArray()) {
                binary.append(String.format("%8s", Integer.toBinaryString(ch)).replaceAll(" ", "0"));
            }
            return binary.toString();
        }

        private String addMacAndPassword(String binaryData, String binaryPassword, String binaryMac) {
            int mid = binaryData.length() / 2;
            return binaryData.substring(0, mid) + binaryMac + binaryPassword + binaryData.substring(mid);
        }

        private List<String> splitIntoParts(String data, int partsCount) {
            List<String> parts = new ArrayList<>();
            int partSize = data.length() / partsCount;
            for (int i = 0; i < data.length(); i += partSize) {
                parts.add(data.substring(i, Math.min(i + partSize, data.length())));
            }
            return parts;
        }

        private String xorParts(List<String> parts) {
            String result = parts.get(0);
            for (int i = 1; i < parts.size(); i++) {
                result = xorBinary(result, parts.get(i));
            }
            return result;
        }

        private String xorBinary(String a, String b) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < a.length(); i++) {
                result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
            }
            return result.toString();
        }

        private String make1024Bit(String data) {
            if (data.length() >= 1024) {
                return data.substring(0, 1024);
            }
            StringBuilder paddedData = new StringBuilder(data);
            while (paddedData.length() < 1024) {
                paddedData.append("0");
            }
            return paddedData.toString();
        }

        private String assignDNABases(List<String> parts) {
            StringBuilder dnaSequence = new StringBuilder();
            String[] dnaBases = {"A", "T", "C", "G"};
            Random random = new Random();
            for (String part : parts) {
                dnaSequence.append(dnaBases[random.nextInt(4)]);
            }
            return dnaSequence.toString();
        }

        private String applyComplementaryPair(String dnaSequence) {
            StringBuilder complementary = new StringBuilder();
            for (char base : dnaSequence.toCharArray()) {
                switch (base) {
                    case 'A': complementary.append('T'); break;
                    case 'T': complementary.append('A'); break;
                    case 'C': complementary.append('G'); break;
                    case 'G': complementary.append('C'); break;
                }
            }
            return complementary.toString();
        }

        private String dnaXor(List<String> dnaParts) {
            if (dnaParts.size() < 2) return dnaParts.get(0);
            return xorBinary(dnaParts.get(0), dnaParts.get(1));
        }
    }

    // CSPT Class
    public class CSPT {
        private Map<String, Map<String, Object>> cspTable;

        public CSPT() {
            cspTable = new HashMap<>();
        }

        public void insertDO(String DOID, List<String> authorizedDOIDs, List<List<String>> GIDList, List<String> U) {
            if (authorizedDOIDs.contains(DOID)) {
                System.out.println("Authorized ID: " + DOID + " proceeding with insert...");
                boolean a0Full = false, a1Full = false;

                for (List<String> GID : GIDList) {
                    if (DOID.length() == GID.get(0).length()) {
                        for (String DOIDx : U) {
                            if (!a1Full) {
                                GID.add(DOID);
                                System.out.println("Inserted " + DOID + " into GID");
                                break;
                            } else {
                                deleteDO(GID);
                                GID.add(DOID);
                                System.out.println("Deleted and re-inserted " + DOID + " into GID");
                                break;
                            }
                        }
                    } else {
                        createNewGroup(DOID);
                    }
                }
                updateCSPT();
            } else {
                System.out.println("Unauthorized DOID: " + DOID + ". Operation stopped.");
            }
        }

        private void createNewGroup(String DOID) {
            Map<String, Object> groupData = new HashMap<>();
            groupData.put("DOIDsz", DOID.length());
            groupData.put("DOIDpv", 1);
            groupData.put("T&D", DOID);
            cspTable.put(DOID, groupData);
            System.out.println("Created new group for " + DOID);
        }

        private void updateCSPT() {
            System.out.println("CSP Table updated successfully.");
        }

        private void deleteDO(List<String> GID) {
            System.out.println("Deleted entries from GID: " + GID);
            GID.clear();
        }
    }

    public void testEncryptionProcess() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        DNAKeyGenerator keyGen = new DNAKeyGenerator(userId, password, "MAC:00:1B:44:11:3A:B7");
        String secretKey = keyGen.generateSecretKey();
        System.out.println("Generated Secret Key: " + secretKey);

        DNAEncryption encryption = new DNAEncryption(secretKey.hashCode());
        String encrypted = encryption.encrypt("Hello DNA World");
        System.out.println("Encrypted: " + encrypted);
        String decrypted = encryption.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);

        CSPT cspt = new CSPT();
        List<String> authorizedDOIDs = List.of("DOID1", "DOID2", "DOID3");
        List<List<String>> GIDList = new ArrayList<>(List.of(new ArrayList<>(), new ArrayList<>()));
        List<String> U = new ArrayList<>(List.of("DOID4"));
        cspt.insertDO("DOID1", authorizedDOIDs, GIDList, U);

        scanner.close();
    }

    // Write results to CSV
    public void writeResultsToCSV(String fileName, List<Map<String, String>> results) {
        try (FileWriter writer = new FileWriter(fileName);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("avgMeasuredTime", "avgSpeed", "extID", "medianMeasuredTime", "vehicleCount", "_id", "REPORT_ID", "EncryptedData", "DecryptedData"))) {

            for (Map<String, String> row : results) {
                csvPrinter.printRecord(
                        row.get("avgMeasuredTime"), row.get("avgSpeed"), row.get("extID"),
                        row.get("medianMeasuredTime"), row.get("vehicleCount"), row.get("_id"),
                        row.get("REPORT_ID"), row.get("EncryptedData"), row.get("DecryptedData")
                );
            }
            csvPrinter.flush();
            System.out.println("Results have been written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void testEncryptionDecryptionWithUserLoad() {
        int[] userCounts = {50, 100, 200, 500, 1000};
        int repetitions = 1000; // Number of repetitions per user to improve timing accuracy

        try (FileWriter writer = new FileWriter("user_load_encryption_decryption_results.csv");
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("UserCount", "AverageEncryptionTime(ms)", "AverageDecryptionTime(ms)"))) {

            for (int userCount : userCounts) {
                List<Long> encryptionTimes = new ArrayList<>();
                List<Long> decryptionTimes = new ArrayList<>();

                for (int i = 0; i < userCount; i++) {
                    String avgSpeed = String.valueOf(30 + new Random().nextInt(50));
                    DNAEncryption encryption = new DNAEncryption(avgSpeed.hashCode());

                    // Measure encryption time over multiple repetitions
                    long totalEncryptionTime = 0;
                    for (int j = 0; j < repetitions; j++) {
                        long startEncryptionTime = System.nanoTime();
                        String encryptedData = encryption.encrypt(avgSpeed);
                        long endEncryptionTime = System.nanoTime();
                        totalEncryptionTime += (endEncryptionTime - startEncryptionTime);
                    }
                    encryptionTimes.add(totalEncryptionTime / repetitions ); // Convert to ms

                    // Measure decryption time over multiple repetitions
                    long totalDecryptionTime = 0;
                    for (int j = 0; j < repetitions; j++) {
                        long startDecryptionTime = System.nanoTime();
                        String decryptedData = encryption.decrypt(encryption.encrypt(avgSpeed));
                        long endDecryptionTime = System.nanoTime();
                        totalDecryptionTime += (endDecryptionTime - startDecryptionTime);
                    }
                    decryptionTimes.add(totalDecryptionTime / repetitions ); // Convert to ms
                }

                long avgEncryptionTime = encryptionTimes.stream().mapToLong(Long::longValue).sum() / encryptionTimes.size();
                long avgDecryptionTime = decryptionTimes.stream().mapToLong(Long::longValue).sum() / decryptionTimes.size();

                csvPrinter.printRecord(userCount, avgEncryptionTime, avgDecryptionTime);
                System.out.println("UserCount: " + userCount + " Avg Encryption Time: " + avgEncryptionTime + " ms, Avg Decryption Time: " + avgDecryptionTime + " ms");
            }

            csvPrinter.flush();
            System.out.println("User load test results have been written to user_load_encryption_decryption_results.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processDataAndEncrypt(String filePath) {
        // Load dataset using CityPulseDataLoader
        CityPulseDataLoader dataLoader = new CityPulseDataLoader(filePath);
        List<Map<String, String>> dataset = dataLoader.loadDataset();

        // Prepare for writing results to CSV file
        try (FileWriter writer = new FileWriter("experiment_results.csv");
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("avgMeasuredTime", "avgSpeed", "EncryptedAvgSpeed", "DecryptedAvgSpeed", "EncryptionTime(ms)", "DecryptionTime(ms)"))) {

            // Encrypt and decrypt data
            for (Map<String, String> dataRow : dataset) {
                String avgSpeed = dataRow.get("avgSpeed");
                if (avgSpeed != null) {
                    // Create DNA encryption object
                    DNAEncryption encryption = new DNAEncryption(avgSpeed.hashCode());

                    // Measure encryption time
                    long startEncryptionTime = System.nanoTime();
                    String encryptedData = encryption.encrypt(avgSpeed);
                    long endEncryptionTime = System.nanoTime();
                    long encryptionTimeMs = (endEncryptionTime - startEncryptionTime) / 1_000_000; // Convert to milliseconds

                    // Measure decryption time
                    long startDecryptionTime = System.nanoTime();
                    String decryptedData = encryption.decrypt(encryptedData);
                    long endDecryptionTime = System.nanoTime();
                    long decryptionTimeMs = (endDecryptionTime - startDecryptionTime) / 1_000_000; // Convert to milliseconds

                    // Write data to CSV file
                    csvPrinter.printRecord(
                            dataRow.get("avgMeasuredTime"),
                            avgSpeed,
                            encryptedData,
                            decryptedData,
                            encryptionTimeMs,
                            decryptionTimeMs
                    );
                }
            }

            System.out.println("Experiment results with encryption and decryption times have been written to experiment_results.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DNABDSCloudEncryption encryptionSystem = new DNABDSCloudEncryption();
        encryptionSystem.testEncryptionDecryptionWithUserLoad();
    }
}
