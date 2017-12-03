package org.su.plugin.rest;

import org.su.framework.helper.ConfigHelper;
import java.util.Arrays;
import java.util.List;

//获取配置文件相关属性
public class RestConfig {
    public static boolean isLog(){
        return ConfigHelper.getBoolean(RestConstant.LOG);
    }

    public static boolean isJsonp(){
        return ConfigHelper.getBoolean(RestConstant.JSONP);
    }

    public static String getJsonpFunction(){
        return ConfigHelper.getString(RestConstant.JSONP_FUNCTION);
    }

    public static boolean isCors(){
        return ConfigHelper.getBoolean(RestConstant.CORS);
    }

    public static List<String> getCorsOriginList(){
        String corsOrigin = ConfigHelper.getString(RestConstant.CORS_ORIGIN);
        return Arrays.asList(corsOrigin.split(","));
    }
}
