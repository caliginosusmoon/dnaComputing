package dnabds;

import org.cloudbus.cloudsim.Vm;

public class DynamicMemory {

    // Method to adjust VM resources after a cloudlet execution
    public static void adjustVmResources(Vm vm, double factor) {
        int newRam = (int) (vm.getRam() * factor);
        int newBw = (int) (vm.getBw() * factor);

        // Set the adjusted resources
        vm.setRam(newRam);
        vm.setBw(newBw);

        System.out.println("VM " + vm.getId() + " resources updated: RAM = " + newRam + " MB, Bandwidth = " + newBw + " MBps");
    }
}
