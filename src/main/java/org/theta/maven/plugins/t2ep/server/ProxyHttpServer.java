package org.theta.maven.plugins.t2ep.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.maven.plugin.logging.Log;
import org.theta.maven.plugins.t2ep.config.ConfigData;

import com.sun.net.httpserver.*;
import com.sun.net.httpserver.spi.*;

/**
 *
 * @author Ranger 2015年6月10日
 */
public class ProxyHttpServer {

    private static ProxyHttpServer proxyHttpServer = new ProxyHttpServer();

    private ConfigData             config          = null;

    private Log                    logger          = null;

    private ProxyHttpServer() {
    }

    public static ProxyHttpServer getInstance(ConfigData config, Log log) {
        proxyHttpServer.config = config;
        proxyHttpServer.logger = log;
        return proxyHttpServer;
    }

    @SuppressWarnings("restriction")
    public void httpserverService() throws IOException {
        HttpServerProvider provider = HttpServerProvider.provider();
        HttpServer httpserver = provider.createHttpServer(new InetSocketAddress(config.getServerPort()), 100);

        ProxyHttpHandler handler = new ProxyHttpHandler(config.getProxies(), logger);
        httpserver.createContext(config.getServerContext(), handler);
        httpserver.setExecutor(null);
        httpserver.start();
        logger.info("Server started.");
        logger.info("Port:" + config.getServerPort());
        logger.info("Context:" + config.getServerContext());
        hold();
    }

    private void hold() {
        // TODO better holdings.
        Runnable holdingRun = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread holdingThread = new Thread(holdingRun);
        holdingThread.setDaemon(true);
        holdingThread.start();
    }

}
