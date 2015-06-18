package org.theta.maven.plugins.t2ep.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

import org.apache.maven.plugin.logging.Log;
import org.theta.maven.plugins.t2ep.config.ConfigData;
import org.theta.maven.plugins.t2ep.config.ProxyData;

/**
 *
 * @author Ranger 2015/06/11
 */
public abstract class RegistryUtils {

    /**
     * 
     * @param config
     * @param logger
     * @return
     */
    public static boolean registry(ConfigData config, Log logger) {
        boolean registed = false;
        try {
            URL url = new URL("http://127.0.0.1:" + config.getServerPort() + config.getServerContext()
                    + ProxyHttpHandler.PROXY_REGISTRY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setChunkedStreamingMode(5);
            connection.connect();

            OutputStream requestOs = connection.getOutputStream();

            StringBuilder requestBody = new StringBuilder();
            buildRequest(requestBody, config);
            logger.info("Request body:" + requestBody.toString());

            requestOs.write(requestBody.toString().getBytes());

            requestOs.close();

            StringBuilder returnResponseString = new StringBuilder();

            InputStream is = connection.getInputStream();
            Scanner sc = new Scanner(is);
            while (sc.hasNextLine()) {
                returnResponseString.append(sc.nextLine());
            }

            sc.close();
            is.close();

            logger.info("Response body:" + returnResponseString.toString());
            if (Objects.equals(returnResponseString.toString(), ProxyHttpHandler.PROXY_REGISTRY_OK)) {
                registed = true;
            }

            connection.disconnect();

        } catch (Exception e) {
            logger.info(e.getMessage());
            registed = false;
        }
        logger.info("Registed:" + registed);
        return registed;
    }

    private static void buildRequest(StringBuilder requestBody, ConfigData config) {
        for (ProxyData proxy : config.getProxies()) {
            requestBody.append(proxy.getSource()).append(ProxyHttpHandler.PROXY_SPLIT_INNER).append(proxy.getSink())
                    .append(ProxyHttpHandler.PROXY_SPLIT_OUTTER);
        }
    }

}
