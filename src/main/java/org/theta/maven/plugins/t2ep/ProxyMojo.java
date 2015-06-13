package org.theta.maven.plugins.t2ep;

import java.util.Objects;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.theta.maven.plugins.t2ep.config.ConfigData;
import org.theta.maven.plugins.t2ep.config.XmlUtils;
import org.theta.maven.plugins.t2ep.server.ProxyHttpServer;
import org.theta.maven.plugins.t2ep.server.RegistryUtils;

/**
 * @author Ranger
 *
 * @goal proxy
 * 
 * @phase validate
 */
public class ProxyMojo extends AbstractMojo {

    private ConfigData   config = null;

    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;

    private Log          logger = null;

    /**
     * 
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.logger = this.getLog();
        boot();
    }

    private void boot() {
        try {
            setProperties();
            boolean registed = RegistryUtils.registry(config, logger);
            if (!registed) {
                ProxyHttpServer.getInstance(config, logger).httpserverService();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws Exception {
        String configPath = this.detectConfigPath();
        this.config = XmlUtils.toBeanFromFile(configPath, "t2ep.xml", ConfigData.class);
        logger.info(this.config.getViewString());
    }

    private String detectConfigPath() {
        String codePath = (String) this.project.getCompileSourceRoots().get(0);
        String[] paths = codePath.split("/");
        if (Objects.equals(paths[paths.length - 1], "java")) {
        	// Unix case.
            paths[paths.length - 1] = "resources";
        } else {
        	// Windows case.
            return codePath.replace("src\\main\\java", "src\\main\\resources\\");
        }
        String resourcesPath = "";
        for (int i = 0; i < paths.length; i++) {
            resourcesPath += "/" + paths[i];
        }
        resourcesPath += "/";
        return resourcesPath;
    }

}
