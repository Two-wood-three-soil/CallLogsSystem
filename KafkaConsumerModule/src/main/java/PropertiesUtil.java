import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置工具类
 */
public class PropertiesUtil {
    // 单例模式
    private PropertiesUtil(){}
    // 静态配置对象
    public static Properties props = null;
    static{
        InputStream in = ClassLoader.getSystemResourceAsStream("project.properties");
        props = new Properties();
        try{
            props.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getProperty(String key){
        return props.getProperty(key);
    }
}
