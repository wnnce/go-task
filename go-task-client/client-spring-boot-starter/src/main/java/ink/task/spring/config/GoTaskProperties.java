package ink.task.spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * @Author: lisang
 * @DateTime: 2023-11-22 21:36:32
 * @Description: SpringBoot 自动配置参数类
 */
@ConfigurationProperties(prefix = "go-task")
public class GoTaskProperties {
    private final String address;
    private final String nodeName;
    private final int port;
    private final int intervals;
    private final int timeout;
    private final boolean singleMain;
    @ConstructorBinding
    public GoTaskProperties(String address, String nodeName, @DefaultValue("24221") int port, @DefaultValue("10") int intervals,
                            @DefaultValue("5000") int timeout, @DefaultValue("true") boolean singleMain) {
        this.address = address;
        this.nodeName = nodeName;
        this.port = port;
        this.intervals = intervals;
        this.timeout = timeout;
        this.singleMain = singleMain;
    }

    public String address() {
        return address;
    }

    public String nodeName() {
        return nodeName;
    }

    public int port() {
        return port;
    }

    public int intervals() {
        return intervals;
    }

    public int timeout() {
        return timeout;
    }

    public boolean singleMain() {
        return singleMain;
    }
}

