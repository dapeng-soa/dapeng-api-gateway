package com.github.dapeng.api.gateway;

import com.github.dapeng.api.gateway.controller.HealthCheckController;
import com.github.dapeng.api.gateway.util.ContainerStatus;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * desc: GracefulShutdown 优雅停机
 *
 * @author hz.lei
 * @since 2018年06月23日 下午5:29
 */
@Component
public class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private volatile Connector connector;

    private final int waitTime = 60;


    @Override
    public void customize(Connector connector) {
        this.connector = connector;
    }

    /**
     * 3次,每次3s
     *
     * @param contextClosedEvent
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        HealthCheckController.status = ContainerStatus.YELLOW;
        logger.info("睡眠15s,等待tengine踢出当前web服务");
        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            logger.error("睡眠线程被打断: " + e.getMessage(), e);
        }
        logger.info("准备关闭容器，先关闭线程!");
        this.connector.pause();
        Executor executor = this.connector.getProtocolHandler().getExecutor();

        if (executor instanceof ThreadPoolExecutor) {
            try {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                threadPoolExecutor.shutdown();

                if (!threadPoolExecutor.awaitTermination(waitTime, TimeUnit.SECONDS)) {

                    logger.warn("Tomcat thread pool did not shut down gracefully within " + waitTime
                            + " seconds. Proceeding with forceful shutdown");
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
