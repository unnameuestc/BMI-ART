package com.bmi.mobileart.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.bmi.mobileart.service.utils.TextUtils;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 运行时的配置
 *
 * @author Ma
 */
public class Config {

    //服务器
    public static boolean DEBUG = true;
    public static String ServerName = "BMIServer";
    public static int PortNum = 8081;
    public static int ThreadCnt = 10;

    //静态文件目录
    public static String ImageBaseUrl = "http://localhost/upload/image/";
    public static String VideoBaseUrl = "http://localhost/upload/video/";

    //Api授权，以后改成动态的
    public static String ApiKey = "A6F7F0D6CD13058D40C1110F007E7F13";

    //短信验证码
    public static String SMS_KEY = "fcb9d8902669c504b5a00e02c094bae5";
    public static int SMS_Expired = 5;      //短信有效期(分钟)
    public static int SMS_Interval = 60;    //短信最短间隔时间(秒)
    public static int SMS_CODE_LEN = 6;     //验证码位数

    //数据库
    protected static String DB_Host = "localhost";
    protected static int DB_Port = 3336;
    protected static String DB_Name = "artservice";
    protected static String DB_Charset = "utf8";
    protected static String DB_User = "bmiadmin";
    protected static String DB_Password = "bmiadmin123";

    protected static String DB_Driver = "com.mysql.jdbc.Driver";
    protected static DruidDataSource ds = new DruidDataSource();
    protected static Dao dao = null;

    public static Dao getDao() {
        return dao;
    }

    protected static final String propertyFileName = "config.properties";
    protected static Properties properties = new Properties();

    public static boolean init() {
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(propertyFileName));
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }

            return false;
        }

        DEBUG = (Boolean) readFromCnf("DEBUG", "true", ConfigType.T_bool);
        PortNum = (Integer) readFromCnf("Port", "8081", ConfigType.T_int);
        ThreadCnt = (Integer) readFromCnf("Thread", "10", ConfigType.T_int);
        ServerName = (String) readFromCnf("ServerName", "BMIServer", ConfigType.T_str);
        ApiKey = (String) readFromCnf("ApiKey", "A6F7F0D6CD13058D40C1110F007E7F13", ConfigType.T_str);

        ImageBaseUrl = (String) readFromCnf("ImageBaseUrl", "http://localhost/upload/image/", ConfigType.T_str);
        VideoBaseUrl = (String) readFromCnf("VideoBaseUrl", "http://localhost/upload/video/", ConfigType.T_str);

        DB_Host = (String) readFromCnf("DB_Host", "localhost", ConfigType.T_str);
        DB_Port = (Integer) readFromCnf("DB_Port", "3336", ConfigType.T_int);
        DB_Name = (String) readFromCnf("DB_Name", "artservice", ConfigType.T_str);
        DB_Charset = (String) readFromCnf("DB_Charset", "utf8", ConfigType.T_str);
        DB_User = (String) readFromCnf("DB_User", "bmiadmin", ConfigType.T_str);
        DB_Password = (String) readFromCnf("DB_Password", "bmiadmin123", ConfigType.T_str);
        DB_Driver = (String) readFromCnf("DB_Driver", "com.mysql.jdbc.Driver", ConfigType.T_str);

        SMS_KEY = (String) readFromCnf("SMS_KEY", "fcb9d8902669c504b5a00e02c094bae5", ConfigType.T_str);
        SMS_Expired = (Integer) readFromCnf("SMS_Expired", "5", ConfigType.T_int);
        SMS_Interval = (Integer) readFromCnf("SMS_Interval", "60", ConfigType.T_int);
        SMS_CODE_LEN = (Integer) readFromCnf("SMS_CODE_LEN", "6", ConfigType.T_int);

        String DB_URL = "jdbc:mysql://"
                + DB_Host + ":"
                + DB_Port + "/"
                + DB_Name + "?characterEncoding="
                + DB_Charset;

        ds.setDriverClassName(DB_Driver);
        ds.setUrl(DB_URL);
        ds.setUsername(DB_User);
        ds.setPassword(DB_Password);

        dao = new NutDao(ds);

        return true;
    }

    public static enum ConfigType {T_str, T_int, T_bool}

    public static Object readFromCnf(String key, String defaultValue, ConfigType type) {
        String value = getString(key);
        if (value == null) {
            value = defaultValue;
        }

        switch (type) {
            case T_str:
                return value;
            case T_int:
                return Integer.parseInt(value);
            case T_bool:
                return Boolean.parseBoolean(value);
        }

        return value;
    }

    public static String getString(String key) {
        String result = properties.getProperty(key);
        if (result == null || result.equals("") || result.equals("null")) {
            return null;
        }

        return result;
    }

    public static Boolean getBoolean(String key) {
        String v = getString(key);
        if (TextUtils.isEmpty(v)) {
            return null;
        }

        return Boolean.parseBoolean(v);
    }

    public static Integer getInteger(String key) {
        String v = getString(key);
        if (TextUtils.isEmpty(v)) {
            return null;
        }

        return Integer.parseInt(v);
    }
}
