package org.theta.maven.plugins.t2ep.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author Ranger 2015年6月11日
 */
@XStreamAlias("proxy")
public class ProxyData {

    @XStreamAlias("source")
    private String source;

    @XStreamAlias("sink")
    private String sink;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSink() {
        return sink;
    }

    public void setSink(String sink) {
        this.sink = sink;
    }

}
