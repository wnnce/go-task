package ink.task.core.system;

import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.Enumeration;


/**
 * @Author: lisang
 * @DateTime: 2023-11-06 10:53:01
 * @Description: 系统信息接口实现类
 */
public class HandControlService extends AbstractControlService{
    private static final Logger logger = LoggerFactory.getLogger(HandControlService.class);
    private static final Iterable<FileStore> fileStores = FileSystems.getDefault().getFileStores();
    private static final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    @Override
    public double getCpuUsed() {
        return osBean.getSystemCpuLoad() * 100;
    }

    @Override
    public long[] getMemoryUsed() {
        long totalMemorySize = osBean.getTotalMemorySize();
        long freeMemorySize = osBean.getFreeMemorySize();
        return new long[] {totalMemorySize, totalMemorySize - freeMemorySize};
    }

    @Override
    public long[] getDiskUsed() {
        long total = 0, free = 0;
        try {
            for (FileStore fileStore : fileStores) {
                if (fileStore.name().startsWith("portal")) {
                    continue;
                }
                total += fileStore.getTotalSpace();
                free += fileStore.getUsableSpace();
            }
        }catch (IOException e) {
            logger.error("获取硬盘空间异常，错误信息：{}", e.getMessage());
        }
        return new long[] {total, total - free};
    }

    @Override
    public String getHostIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress ia = inetAddresses.nextElement();
                    if (!ia.isLoopbackAddress() && !ia.isLinkLocalAddress() && ia.isSiteLocalAddress()) {
                        return ia.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            logger.error("获取网络接口IP地址异常，错误信息：{}", e.getMessage());
        }
        return "";
    }
}
