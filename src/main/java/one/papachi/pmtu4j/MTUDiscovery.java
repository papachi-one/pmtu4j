package one.papachi.pmtu4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class MTUDiscovery {

    public static void main(String[] args) throws Exception {
        System.out.print(getMTU(InetAddress.getByName("papachi.one")));
    }

    public static int getMTU(InetAddress address) {
        int mtu = address.getAddress().length == 4 ? 576 : 1280;;
        int step = 500;
        int passedMTU = 0;
        while (true) {
            boolean passed = passed(address, mtu);
            if (passed) {
                passedMTU = mtu;
                mtu += step;
                if (step == 0)
                    break;
            } else {
                step /= 2;
                if (step > 0) {
                    mtu -= step;
                } else {
                    mtu -= 1;
                }
            }
        }
        return passedMTU;
    }

    public static Future<Integer> getMTUAsync(InetAddress address) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.completeAsync(() -> getMTU(address));
        return future;
    }

    public static <A> void getMTUAsync(InetAddress address, A attachment, CompletionHandler<Integer, ? super A> handler) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.completeAsync(() -> getMTU(address));
        future.whenComplete((result, exception) -> {
            if (exception != null)
                handler.failed(exception, attachment);
            else
                handler.completed(result, attachment);
        });
    }

    private static boolean passed(InetAddress address, int mtu) {
        try {
            return switch (getOperatingSystemFamily()) {
                case WINDOWS -> {
                    Process ping = new ProcessBuilder().command("ping", address.getHostAddress(), "-f", "-n", "1", "-w", "500", "-l", Integer.toString(mtu)).start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(ping.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("DF set"))
                            yield false;
                    }
                    yield true;
                }
                case LINUX -> {
                    Process ping = new ProcessBuilder().command("ping", address.getHostAddress(), "-M", "do", "-c", "1", "-W", "0.5", "-s", Integer.toString(mtu)).start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(ping.getErrorStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("message too long"))
                            yield  false;
                    }
                    reader = new BufferedReader(new InputStreamReader(ping.getInputStream()));
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("100% packet loss"))
                            yield  false;
                    }
                    yield true;
                }
                case MAC -> {
                    Process ping = new ProcessBuilder().command("ping", address.getHostAddress(), "-D", "-c", "1", "-W", "0.5", "-s", Integer.toString(mtu)).start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(ping.getErrorStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("Message too long"))
                            yield  false;
                    }
                    reader = new BufferedReader(new InputStreamReader(ping.getInputStream()));
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("100.0% packet loss"))
                            yield  false;
                    }
                    yield true;
                }
                case UNKNOWN -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }

    private enum OperatingSystemFamily {
        WINDOWS, LINUX, MAC, UNKNOWN;

    }

    private static OperatingSystemFamily getOperatingSystemFamily() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OperatingSystemFamily.WINDOWS;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return OperatingSystemFamily.LINUX;
        } else if (osName.contains("mac")) {
            return OperatingSystemFamily.MAC;
        }
        return OperatingSystemFamily.UNKNOWN;
    }

}
