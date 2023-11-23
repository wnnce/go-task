package ink.task.spring;

import ink.task.core.TaskProcessorSelector;
import ink.task.core.model.TaskInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * @Author: lisang
 * @DateTime: 2023-11-23 14:12:27
 * @Description:
 */
@SpringBootTest
public class TaskTest {
    @Autowired
    TaskProcessorSelector selector;
    @Test
    public void testSelector() {
        TaskInfo taskInfo = new TaskInfo(1, 0, 0, "beanProcessor", null, 0, LocalDateTime.now());
        selector.doSelect(taskInfo);
    }
}
