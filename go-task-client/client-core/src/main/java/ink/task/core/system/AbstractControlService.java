package ink.task.core.system;

/**
 * @Author: lisang
 * @DateTime: 2023-11-06 10:53:37
 * @Description:
 */
public abstract class AbstractControlService implements ControlService {
    @Override
    public ControlInfo getAllInfo() {
        double cpuUsed = this.getCpuUsed();
        long[] memoryUsed = this.getMemoryUsed();
        long[] diskUsed = this.getDiskUsed();
        String ip = this.getHostIp();
        return ControlInfo.builder()
                .usedCpu(cpuUsed)
                .totalMemory(memoryUsed[0])
                .usedMemory(memoryUsed[1])
                .totalDisk(diskUsed[0])
                .usedDisk(diskUsed[1])
                .ipAddress(ip)
                .build();
    }

    @Override
    public abstract double getCpuUsed();

    @Override
    public abstract long[] getMemoryUsed();

    @Override
    public abstract long[] getDiskUsed();

    @Override
    public abstract String getHostIp();
}
