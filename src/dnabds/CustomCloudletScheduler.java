package dnabds;





import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.Vm;

import java.util.List;
import java.util.Map;

public class CustomCloudletScheduler extends CloudletSchedulerTimeShared {

    // Map to store VM IDs and their corresponding VMs
    private Map<Integer, Vm> vmMap;

    public CustomCloudletScheduler(Map<Integer, Vm> vmMap) {
        this.vmMap = vmMap;
    }

    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        double nextEvent = super.updateVmProcessing(currentTime, mipsShare);

        // Process each executing cloudlet and adjust resources after completion
        for (ResCloudlet rcl : getCloudletExecList()) {
            // Retrieve VM based on Cloudlet's VM ID
            Vm vm = vmMap.get(rcl.getCloudlet().getVmId());

            // Setup user information and key generator
            String userId = "AuthorizedUserID";  // Replace with actual user ID
            String password = "examplePassword";  // Replace with actual password
            String macAddress = "MAC:00:1B:44:11:3A:B7";  // Replace with actual MAC address

            // Generate secret key using DNAKeyGenerator
            DNABDSCloudEncryption.DNAKeyGenerator keyGen = new DNABDSCloudEncryption().new DNAKeyGenerator(userId, password, macAddress);
            String secretKey = keyGen.generateSecretKey();

            // Instantiate DNAEncryption using the generated key hash code
            DNABDSCloudEncryption.DNAEncryption encryption = new DNABDSCloudEncryption().new DNAEncryption(secretKey.hashCode());

            // Encrypt cloudlet data
            String cloudletData = "Sample data for Cloudlet " + rcl.getCloudletId();
            String encryptedData = encryption.encrypt(cloudletData);
            System.out.println("Encrypted Data for Cloudlet " + rcl.getCloudletId() + ": " + encryptedData);

            // Adjust resources using hypothetical resource factor
            double resourceFactor = 1.1;  // Adjust dynamically based on needs
            DynamicMemory.adjustVmResources(vm, resourceFactor);
        }

        return nextEvent;
    }

}
