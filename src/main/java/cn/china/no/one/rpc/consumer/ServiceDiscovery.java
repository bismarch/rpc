package cn.china.no.one.rpc.consumer;

import cn.china.no.one.rpc.common.Constant;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author chenjuzhi
 * @Title: rpc
 * @Package cn.china.no.one.rpc.consumer
 * @Description: ${TODO}
 * @Nimitz
 * @date 2018/1/12
 */
public class ServiceDiscovery {

    private String remoteAddress;
    private CountDownLatch count = new CountDownLatch(1);
    private List<String> rpcServerList = new ArrayList<>();

    public ServiceDiscovery(String remoteAddress) {
        this.remoteAddress = remoteAddress;
        ZooKeeper zk = connect2Zk();
        if (null != zk) {
            watchNode(zk);
        }
    }

    public String discovery() {
        String rpcServer = null;
        int listLen = rpcServerList.size();
        if (listLen > 0) {
            if (listLen == 1) {
                rpcServer = rpcServerList.get(0);
            } else {
                rpcServer = rpcServerList.get(new Random().nextInt(listLen));
            }
        }
        return rpcServer;
    }

    private ZooKeeper connect2Zk() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(remoteAddress, Constant.ZK_TIMEOUT, watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    count.countDown();
                }
            });
            count.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return zk;
    }

    private void watchNode(ZooKeeper zk) {
        try {
            List<String> nodeList = zk.getChildren(Constant.ZK_REGISTER_PATH, watchedEvent -> {
                if (watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    watchNode(zk);
                }
            });
            List<String> dataList = new ArrayList<>();
            for (String node : nodeList) {
                byte[] data = zk.getData(Constant.ZK_DATA_PATH + "/" + node, false, null);
                dataList.add(new String(data));
            }
            this.rpcServerList = dataList;
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
