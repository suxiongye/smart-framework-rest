package org.su.plugin.rest;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.su.framework.helper.ClassHelper;
import org.su.framework.util.CollectionUtil;
import org.su.framework.util.StringUtil;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import java.util.Set;

@WebServlet(urlPatterns = RestConstant.SERVLET_URL, loadOnStartup = 0)
public class RestServlet extends CXFNonSpringServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestServlet.class);
    @Override
    protected void loadBus(ServletConfig sc) {
        super.loadBus(sc);
        Bus bus = getBus();
        BusFactory.setDefaultBus(bus);
        //发布rest服务
        publishRestService();
    }

    private void publishRestService(){
        //遍历所有标注rest注解的类
        Set<Class<?>> restClassSet = ClassHelper.getClassSetByAnnotation(Rest.class);
        if (CollectionUtil.isNotEmpty(restClassSet)){
            for (Class<?> restClass : restClassSet){
                //获取rest地址
                String address = getAddress(restClass);
                //发布rest服务
                RestHelper.pulishService(address, restClass);
                LOGGER.error("address: "+ address + " restClass: " + restClass.getSimpleName());
            }
        }
    }

    private String getAddress(Class<?> restClass){
        String address;
        //若rest注解value不为空，则获取当前值，否则获取类名
        String value = restClass.getAnnotation(Rest.class).value();
        if (StringUtil.isNotEmpty(value)){
            address = value;
        }else {
            address = restClass.getSimpleName();
        }
        if (!address.startsWith("/")){
            address = "/" + address;
        }
        address = address.replaceAll("/+", "/");
        return address;
    }
}
