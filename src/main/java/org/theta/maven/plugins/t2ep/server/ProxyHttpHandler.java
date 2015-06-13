package org.theta.maven.plugins.t2ep.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.apache.maven.plugin.logging.Log;
import org.theta.maven.plugins.t2ep.config.ProxyData;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 *
 * @author Ranger 2015骞�6鏈�11鏃�
 */
@SuppressWarnings("restriction")
class ProxyHttpHandler implements HttpHandler {

    private List<ProxyData>    proxies;

    private Log                logger             = null;

    public static final String PROXY_REGISTRY     = "ProxyRegistr";

    public static final String PROXY_REGISTRY_OK  = "OK";

    public static final String PROXY_SPLIT_INNER  = ",";

    public static final String PROXY_SPLIT_OUTTER = ";";

    public ProxyHttpHandler(List<ProxyData> proxies, Log logger) {
        this.logger = logger;
        this.proxies = proxies;
    }

    public void handle(HttpExchange httpExchange) throws IOException {

        String requestUriPath = httpExchange.getRequestURI().getPath();

        StringBuffer responseString = new StringBuffer();
        if (requestUriPath.contains(PROXY_REGISTRY)) {
            doRegistry(requestUriPath, responseString, httpExchange);
        } else {
            doProxy(requestUriPath, responseString, httpExchange);
        }

        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, responseString.toString().getBytes().length);

        OutputStream responseBody = httpExchange.getResponseBody();

        responseBody.write(responseString.toString().getBytes());
        responseBody.close();

        logger.info("Response:\n" + responseString);
    }

    private void doRegistry(String requestUriPath, StringBuffer responseString, HttpExchange httpExchange) {
        logger.info("Request URI Path:" + requestUriPath);
        StringBuilder requestBody = new StringBuilder();
        try {
            InputStream is = httpExchange.getRequestBody();
            Scanner sc = new Scanner(is);
            while (sc.hasNextLine()) {
                requestBody.append(sc.nextLine());
            }

            sc.close();
            is.close();

            logger.info("Request Body:" + requestBody.toString());

            String[] regProxies = requestBody.toString().split(PROXY_SPLIT_OUTTER);
            for (String regProxy : regProxies) {
                String[] regParams = regProxy.split(PROXY_SPLIT_INNER);
                ProxyData proxyData = new ProxyData();
                proxyData.setSource(regParams[0]);
                proxyData.setSink(regParams[1]);
                this.proxies.add(proxyData);
            }
            logger.info("New registed proxies size:" + this.proxies.size());
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
                    ProxyHttpHandler.PROXY_REGISTRY_OK.getBytes().length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(ProxyHttpHandler.PROXY_REGISTRY_OK.getBytes());
            os.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void doProxy(String requestUriPath, StringBuffer returnResponseString, HttpExchange httpExchange) {
        logger.info("Request URI Path:" + requestUriPath);

        int pos = -1;
        for (int i = 0; i < this.proxies.size(); i++) {
            if (requestUriPath.startsWith(this.proxies.get(i).getSource())) {
                pos = i;
                break;
            }
        }
        logger.info("pos:" + pos);
        String proxyRequestUriPath = requestUriPath;
        if (pos != -1) {
            proxyRequestUriPath = proxyRequestUriPath.replace(this.proxies.get(pos).getSource(), this.proxies.get(pos)
                    .getSink());
            logger.info("Proxy Request URI:" + proxyRequestUriPath);
        }

        try {
            URL url = new URL(proxyRequestUriPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Headers requestHeaders = httpExchange.getRequestHeaders();
            for (String fieldName : requestHeaders.keySet()) {
                List<String> fieldValues = requestHeaders.get(fieldName);
                logger.info("Request FieldName:" + fieldName);
                logger.info("Request FieldValue:" + fieldValues);
                String fieldValueString = "";
                for (String fieldValue : fieldValues) {
                    fieldValueString += fieldValue + ";";
                }
                if (Objects.equals(fieldName, HttpConsts.Headers.COOKIE)) {
                    connection.setRequestProperty(fieldName, fieldValueString);
                }
            }
            // connection.setRequestMethod(httpExchange.getRequestMethod());
            connection.setRequestProperty(HttpConsts.Headers.X_FORWARDED_FOR, url.getProtocol()+"://"+url.getHost()+":"+url.getPort());
            connection.connect();

            Headers returnHeaders = httpExchange.getResponseHeaders();
            if (returnHeaders == null) {
                returnHeaders = new Headers();
            }            
            
            for (String fieldName : connection.getHeaderFields().keySet()) {
                String fieldValue = connection.getHeaderField(fieldName);
                logger.info("Response FieldName:" + fieldName);
                logger.info("Response FieldValue:" + fieldValue);
                if (Objects.equals(fieldName, HttpConsts.Headers.SET_COOKIE)) {
                    returnHeaders.add(fieldName, fieldValue);
                }
            }

            logger.info("----------");
            logger.info("getContentType: " + connection.getContentType());
            logger.info("getContentLength: " + connection.getContentLength());
            logger.info("getContentEncoding: " + connection.getContentEncoding());
            logger.info("getDate: " + connection.getDate());
            logger.info("getExpiration: " + connection.getExpiration());
            logger.info("getLastModifed: " + connection.getLastModified());
            logger.info("----------");

            InputStream is = connection.getInputStream();
            Scanner sc = new Scanner(is);
            while (sc.hasNextLine()) {
            	String nextLine=sc.nextLine();
                returnResponseString.append(nextLine);
            }

            sc.close();
            is.close();                        
            
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
