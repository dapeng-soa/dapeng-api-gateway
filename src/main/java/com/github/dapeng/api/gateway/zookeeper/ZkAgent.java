package com.github.dapeng.api.gateway.zookeeper;

import com.github.dapeng.api.gateway.util.WhiteListUtil;
import com.github.dapeng.core.helper.SoaSystemEnvProperties;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author with struy.
 * Create by 2018/5/18 16:28
 * email :yq1724555319@gmail.com
 */

public class ZkAgent {
    private Logger LOGGER = LoggerFactory.getLogger(ZkAgent.class);
    private String SERVICE_WITHELIST_PATH = "/gateway/white_list/services";
    private String zkHost = SoaSystemEnvProperties.SOA_ZOOKEEPER_HOST;
    private static Set<String> whitelist = Collections.synchronizedSet(new HashSet<>());
    private static ZkAgent zkAgent = null;
    private ZooKeeper zk;

    public static Set<String> getWhitelist() {
        return whitelist;
    }

    private ZkAgent() {
    }

    public static ZkAgent getInstance() {
        if (null == zkAgent) {
            zkAgent = new ZkAgent();
        }
        return zkAgent;
    }


    /**
     * 注册服务白名单到zookeeper
     *
     * @param services
     */
    private void registerServiceWhiteList(List<String> services) {
        if (null != services) {
            services.forEach(s -> {
                create(SERVICE_WITHELIST_PATH + "/" + s, false);
            });
            whitelist.addAll(services);
            watchInstanceChange();
        }
    }

    /**
     * 重连
     */
    public void reConnection() {
        destroy();
        connect();
    }

    /**
     * zk 客户端实例化
     * 使用 CountDownLatch 门闩 锁，保证zk连接成功后才返回
     */
    public void connect() {
        try {
            CountDownLatch semaphore = new CountDownLatch(1);

            zk = new ZooKeeper(zkHost, 30000, watchedEvent -> {

                switch (watchedEvent.getState()) {

                    case Expired:
                        LOGGER.info("Gateway ServerZk session timeout to  {} [Zookeeper]", zkHost);
                        destroy();
                        connect();
                        break;

                    case SyncConnected:
                        semaphore.countDown();
                        //创建根节点
                        create(SERVICE_WITHELIST_PATH, false);
                        LOGGER.info("Gateway ServerZk connected to  {} [Zookeeper]", zkHost);
                        // 注册白名单
                        registerServiceWhiteList(WhiteListUtil.initWhiteList());
                        break;

                    case Disconnected:
                        //zookeeper重启或zookeeper实例重新创建
                        LOGGER.error("[Disconnected]: ServerZookeeper Registry zk 连接断开，可能是zookeeper重启或重建");
                        destroy();
                        connect();
                        break;
                    case AuthFailed:
                        LOGGER.info("Zookeeper connection auth failed ...");
                        destroy();
                        break;
                    default:
                        break;
                }
            });
            //hold 10 s
            semaphore.await(10000, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 关闭 zk 连接
     */
    private void destroy() {
        if (zk != null) {
            try {
                LOGGER.info("ServerZk closing connection to zookeeper {}", zkHost);
                zk.close();
                zk = null;
                whitelist.clear();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

    }

    /**
     * @param path
     * @param ephemeral
     */
    private void create(String path, boolean ephemeral) {

        int i = path.lastIndexOf("/");
        if (i > 0) {
            String parentPath = path.substring(0, i);
            //判断父节点是否存在...
            if (!checkExists(parentPath)) {
                create(parentPath, false);
            }
        }
        if (ephemeral) {
            // 创建临时节点
        } else {
            createPersistent(path, "");
        }
    }

    /**
     * 异步添加持久化的节点
     *
     * @param path
     * @param data
     */
    private void createPersistent(String path, String data) {
        Stat stat = exists(path);

        if (stat == null) {
            zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, persistNodeCreateCallback, data);
        }
    }

    private Stat exists(String path) {
        Stat stat = null;
        try {
            stat = zk.exists(path, false);
        } catch (KeeperException | InterruptedException e) {
        }
        return stat;
    }

    /**
     * 检查节点是否存在
     */
    private boolean checkExists(String path) {
        try {
            Stat exists = zk.exists(path, false);
            if (exists != null) {
                return true;
            }
            return false;
        } catch (Throwable t) {
        }
        return false;
    }

    /**
     * watch 白名单节点变化
     */
    private void watchInstanceChange() {

        try {
            List<String> children = zk.getChildren(SERVICE_WITHELIST_PATH, event -> {
                //Children发生变化，则重新获取最新的services列表
                if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    LOGGER.info("[{}] gateway服务白名单发生变化，重新获取...", event.getPath());
                    whitelist.clear();
                    watchInstanceChange();
                }
            });
            whitelist.addAll(children);
            LOGGER.info("当前白名单个数:[{}]", whitelist.size());
            LOGGER.info(">>>>>>>>>>>>>>>>>>");
            whitelist.forEach(w -> {
                LOGGER.info(w);
            });
            LOGGER.info(">>>>>>>>>>>>>>>>>>");
        } catch (Exception e) {
            LOGGER.error("获取gateway服务白名单失败");
        }

    }


    /**
     * 异步添加持久化节点回调方法
     */
    private AsyncCallback.StringCallback persistNodeCreateCallback = (rc, path, ctx, name) -> {
        switch (KeeperException.Code.get(rc)) {
            case CONNECTIONLOSS:
                LOGGER.info("创建节点:{},连接断开，重新创建", path);
                createPersistent(path, (String) ctx);
                break;
            case OK:
                LOGGER.info("创建节点:{},成功", path);
                // 添加watcher
                if (path.equals(SERVICE_WITHELIST_PATH)) {
                    watchInstanceChange();
                }
                break;
            case NODEEXISTS:
                LOGGER.info("创建节点:{},已存在", path);
                break;
            default:
                LOGGER.info("创建节点:{},失败", path);
        }
    };
}
