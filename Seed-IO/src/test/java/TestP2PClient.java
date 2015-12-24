import com.opdar.seed.io.p2p.P2pClient;
import com.opdar.seed.io.protocol.P2pProtocol;
import com.opdar.seed.io.utils.ZookeeperUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * Created by 俊帆 on 2015/12/16.
 */
public class TestP2PClient {
    public static final String ZOOKEEPER = "121.199.40.54";
    public static final String HOST = "121.199.40.54";
    public static void main(String[] args) throws Exception{
        P2pClient client3 = new P2pClient();
        client3.send(("DIGPORT:").getBytes(),"180.175.138.108", 1780);
    }
}
