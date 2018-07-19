package com.github.dapeng.api.gateway.controller;

import com.github.dapeng.api.gateway.util.ContainerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * desc: 健康检查 controller
 *
 * @author hz.lei
 * @since 2018年06月29日 上午11:01
 */
@Controller
public class HealthCheckController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    /***
     * Gateway 容器状态,即将关闭时显示 GREEN
     */
    public static ContainerStatus status = ContainerStatus.GREEN;

    @RequestMapping(value = "/health/check", method = RequestMethod.HEAD)
    public ResponseEntity healthCheck() {
        logger.debug("health check,container status: " + status);
        ResponseEntity<String> response;
        if (status == ContainerStatus.YELLOW) {
            logger.info("health check,container status: " + status);
            response = ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("container maybe shutdown soon");
        } else {
            response = ResponseEntity
                    .status(HttpStatus.OK)
                    .body("container is  running");
        }
        return response;
    }
}
