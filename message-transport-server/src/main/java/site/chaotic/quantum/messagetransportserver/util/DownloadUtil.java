package site.chaotic.quantum.messagetransportserver.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class DownloadUtil {
    public static byte[] downloadImage(String url) throws IOException {
        // Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10809));
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        try (InputStream in = connection.getInputStream()) {
            return IOUtils.toByteArray(in);
        }
    }
}
