package dnabds;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DNABDSCloudSimEnvironment {
    public static void main(String[] args) throws IOException {
        int numExperiments = 50;

        // File where results will be logged
        String fileName = "experiment_results.csv";

        // Check if the file exists, if not, write the header
        try (FileWriter headerWriter = new FileWriter(fileName, false)) {  // false ensures overwriting the file if it already exists
            headerWriter.append("Experiment,Key Generation Time,Encryption Time,Decryption Time\n");
        } catch (IOException e) {
            System.err.println("Error writing CSV header: " + e.getMessage());
            return;
        }

        for (int experiment = 1; experiment <= numExperiments; experiment++) {
            // Simulate CloudSim environment setup
            CloudSim.init(1, Calendar.getInstance(), false);

            // Set up Datacenters and Broker
            List<Datacenter> datacenters = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                datacenters.add(createDatacenter("Datacenter_" + i));
            }
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            // Create VMs and Cloudlets
            List<Vm> vmList = createVMs(brokerId);
            broker.submitVmList(vmList);
            List<Cloudlet> cloudletList = createCloudlets(brokerId);
            broker.submitCloudletList(cloudletList);

            // Key Generation
            DNABDSCloudEncryption encryptionSystem = new DNABDSCloudEncryption();
            DNABDSCloudEncryption.DNAKeyGenerator keyGen = encryptionSystem.new DNAKeyGenerator("AuthorizedUserID", "password", "MAC:00:1B:44:11:3A:B7");
            long startKeyGen = System.nanoTime();
            String secretKey = keyGen.generateSecretKey();
            long endKeyGen = System.nanoTime();
            double keyGenerationTime = (endKeyGen - startKeyGen) / 1e6;  // Convert to milliseconds

            // Encryption
            DNABDSCloudEncryption.DNAEncryption dnaEncryption = encryptionSystem.new DNAEncryption(secretKey.hashCode());
            long startEncryption = System.nanoTime();
            String encryptedText = dnaEncryption.encrypt("Hello DNA World");
            long endEncryption = System.nanoTime();
            double encryptionTime = (endEncryption - startEncryption) / 1e6;  // Convert to milliseconds

            // Decryption
            long startDecryption = System.nanoTime();
            String decryptedText = dnaEncryption.decrypt(encryptedText);
            long endDecryption = System.nanoTime();
            double decryptionTime = (endDecryption - startDecryption) / 1e6;  // Convert to milliseconds

            // Log the results to the CSV
            logToCSV(fileName, experiment, keyGenerationTime, encryptionTime, decryptionTime);
        }

    }

    private static Datacenter createDatacenter(String name) {
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(1000)));

        List<Host> hostList = new ArrayList<>();
        hostList.add(new Host(
                0,
                new RamProvisionerSimple(2048),
                new BwProvisionerSimple(10000),
                1000000,
                peList,
                new VmSchedulerTimeShared(peList)
        ));

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86",
                "Linux",
                "Xen",
                hostList,
                10.0,
                3.0,
                0.05,
                0.1,
                0.1
        );

        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datacenter;
    }

    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return broker;
    }

    private static List<Vm> createVMs(int brokerId) {
        List<Vm> vmList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            vmList.add(new Vm(i, brokerId, 1000, 1, 512, 1000, 10000, "Xen", new CloudletSchedulerTimeShared()));
        }
        return vmList;
    }

    private static List<Cloudlet> createCloudlets(int brokerId) {
        List<Cloudlet> cloudletList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Cloudlet cloudlet = new Cloudlet(i, 40000, 1, 300, 300, new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
            cloudlet.setUserId(brokerId);
            cloudletList.add(cloudlet);
        }
        return cloudletList;
    }
    private static FileWriter csvWriter;

    public static void initializeCSV() throws IOException {
        csvWriter = new FileWriter("experiment_results.csv");
        csvWriter.append("Experiment,Scheme,Key Generation Time,Encryption Time,Decryption Time,Key Retrieval Time\n");
    }

    private static void logToCSV(String fileName, int experimentNumber, double keyGenTime, double encryptionTime, double decryptionTime) throws IOException {
        FileWriter csvWriter = new FileWriter(fileName, true);  // Append mode
        csvWriter.append(experimentNumber + "," + keyGenTime + "," + encryptionTime + "," + decryptionTime + "\n");
        csvWriter.flush();
        csvWriter.close();
    }


    public static void closeCSV() throws IOException {
        csvWriter.flush();
        csvWriter.close();
    }
}
