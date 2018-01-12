package cn.china.no.one.rpc.provider;

import cn.china.no.one.rpc.common.Constant;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author chenjuzhi
 * @Title: rpc
 * @Package cn.china.no.one.rpc.provider
 * @Description: ${TODO}
 * @Nimitz
 * @date 2018/1/11
 */
public class ServiceRegister {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegister.class);

    private String registerAddress;
    private CountDownLatch count = new CountDownLatch(1);

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public void register(String data) {
        if (null != data) {
            ZooKeeper zk = connect2Zk();
            if (null != zk) {
                createNode(zk, data);
            }
        }
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
            zk.create(Constant.ZK_REGISTER_PATH, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.info("Successfully create node,data:" + data);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private ZooKeeper connect2Zk() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registerAddress, Constant.ZK_TIMEOUT, watchedEvent -> {
                if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                    count.countDown();
                    LOGGER.info("Successfully connect to Zookeeper!");
                }
            });
            count.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return zk;
    }
    

}
