# PMTU4j
MTU discovery Java library.
## Sample code
### Example #1
```java
import one.papachi.pmtu4j.MTUDiscovery;

import java.net.InetAddress;

public class App {

    public static void main(String[] args) {
        String host = "papachi.one";
        InetAddress address = InetAddress.getByName(host);
        int mtu = MTUDiscovery.getMTU(address);
        System.out.print("MTU = " + mtu);
    }

}
```
### Example #2

```java
import one.papachi.pmtu4j.MTUDiscovery;

import java.net.InetAddress;
import java.util.concurrent.Future;

public class App {

    public static void main(String[] args) {
        String host = "papachi.one";
        InetAddress address = InetAddress.getByName(host);
        Future<Integer> future = MTUDiscovery.getMTUAsync(address);
        int mtu = future.get();
        System.out.print("MTU = " + mtu);
    }

}
```
### Example #3

```java
import one.papachi.pmtu4j.MTUDiscovery;

import java.net.InetAddress;
import java.nio.channels.CompletionHandler;

public class App {

    public static void main(String[] args) {
        String host = "papachi.one";
        InetAddress address = InetAddress.getByName(host);
        MTUDiscovery.getMTUAsync(address, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                System.out.print("MTU = " + result);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                exc.printStackTrace();
            }
        });
    }

}
```