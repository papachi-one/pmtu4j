# PMTU4j
Cross-platform MTU discovery Java library.
## Supported platforms
- Windows
- Linux
- macOS
## How it works
Library runs `ping` command in the background with IP flag set to `Don't fragment`.
```shell
# Windows
ping papachi.one -f -n 1 -w 500 -l 1500
```
```shell
# Linux
ping papachi.one -M do -c 1 -W 0.5 -s 1500
```
```shell
# macOS
ping papachi.one -D -c 1 -W 0.5 -s 1500
```
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