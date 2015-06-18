package org.theta.maven.plugins.t2ep.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 *
 * @author Ranger 2015/06/11
 */
public class XmlUtils {

    @SuppressWarnings("unchecked")
    public static <T> T toBeanFromFile(String absPath, String fileName, Class<T> cls) throws Exception {
        String filePath = absPath + fileName;
        InputStream ins = null;
        try {
            ins = new FileInputStream(new File(filePath));
        } catch (Exception e) {
            throw new Exception("Read {" + filePath + "} failed！", e);
        }

        XStream xstream = new XStream(new DomDriver("UTF8"));
        xstream.processAnnotations(cls);
        T obj = null;
        try {
            obj = (T) xstream.fromXML(ins);
        } catch (Exception e) {
            throw new Exception("Parse {" + filePath + "} failed！", e);
        }
        if (ins != null)
            ins.close();
        return obj;
    }

}
