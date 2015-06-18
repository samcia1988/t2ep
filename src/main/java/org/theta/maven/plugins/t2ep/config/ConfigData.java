package org.theta.maven.plugins.t2ep.config;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author Ranger 2015年6月11日
 */
@XStreamAlias("t2ep")
public class ConfigData {

    @XStreamAlias("serverPort")
    private int             serverPort    = -1;

    @XStreamAlias("serverContext")
    private String          serverContext = null;

    @XStreamAlias("daemonHolding")
    private Boolean         daemonHolding;

    @XStreamAlias("proxies")
    private List<ProxyData> proxies;

    public Boolean getDaemonHolding() {
        return daemonHolding;
    }

    public void setDaemonHolding(Boolean daemonHolding) {
        this.daemonHolding = daemonHolding;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerContext() {
        return serverContext;
    }

    public void setServerContext(String serverContext) {
        this.serverContext = serverContext;
    }

    public List<ProxyData> getProxies() {
        return proxies;
    }

    public void setProxies(List<ProxyData> proxies) {
        this.proxies = proxies;
    }

    public String getViewString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("serverPort:").append(serverPort).append(",");
        builder.append("serverContext:").append(serverContext).append(",");
        builder.append("daemonHolidng:").append(daemonHolding).append(",");
        builder.append("proxies:");
        if (proxies != null) {
            for (ProxyData proxy : proxies) {
                builder.append("{");
                builder.append("source:").append(proxy.getSource());
                builder.append(",");
                builder.append("sink:").append(proxy.getSink());
                builder.append("}");
                if (proxies.indexOf(proxy) != (proxies.size() - 1)) {
                    builder.append(",");
                }
            }
        }
        builder.append("}");
        return builder.toString();
    }

}
