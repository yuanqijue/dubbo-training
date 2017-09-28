package com.xksh.dubbo;

import com.caucho.hessian.client.HessianProxyFactory;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.zookeeper.Watcher.Event.EventType.NodeDataChanged;

/**
 * 注册中心
 * Created by aaron on 2017/9/28.
 */
public class RegistryFactory {

    static HessianProxyFactory factory = new HessianProxyFactory();

    static ZooKeeper zk;

    private static final String ROOT = "/pub";

    static {
        try {
            zk = new ZooKeeper("127.0.0.1:2181", 200, new Watcher() {
                public void process(WatchedEvent event) {
                    if (NodeDataChanged == event.getType()) {
                        event.getType();
                    }
                }
            });
            if (zk.exists(ROOT, false) == null) {
                zk.create(ROOT, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册服务
     *
     * @param cls 服务接口
     * @param url URL地址
     */
    public static void register(Class cls, String url) {
        try {
            zk.create(ROOT + "/" + cls.getName(), url.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消注册服务
     *
     * @param cls 服务接口
     */
    public static void unRegister(Class cls) {
        try {
            if (zk.exists(ROOT, false) != null) {
                zk.delete(ROOT + "/" + cls.getName(), -1);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 订阅服务
     *
     * @param cls
     */
    public static void subscribe(Class cls) {

        // 实现自动服务注入
    }


    void unSubscribe(URL url) {

        // 取消订阅
    }

    public static <T> T getService(Class<T> cls) {
        String url = "";
        try {
            byte[] bs = zk.getData("/pub/" + cls.getName(), false, null);
            if (bs != null) {
                url = new String(bs);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

        if ("".equals(url)) {
            return null;
        }
        try {
            T  service = (T) factory.create(cls, url);
            Monitor.count(cls);
            return service;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
