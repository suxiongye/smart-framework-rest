package org.su.plugin.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpInInterceptor;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpPostStreamInterceptor;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpPreStreamInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharingFilter;
import org.su.framework.helper.BeanHelper;

import java.util.ArrayList;
import java.util.List;

public class RestHelper {
    private static final List<Object> providerList = new ArrayList<>();
    private static final List<Interceptor<? extends Message>> inInterceptorList = new ArrayList<Interceptor<? extends Message>>();
    private static final List<Interceptor<? extends Message>> outInterceptorList = new ArrayList<Interceptor<? extends Message>>();

    static {
        //添加json provider
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider();
        providerList.add(jsonProvider);
        //添加Logging Interceptor
        if (RestConfig.isLog()){
            LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
            inInterceptorList.add(loggingInInterceptor);
            LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
            outInterceptorList.add(loggingOutInterceptor);
        }
        //添加jsonp interceptor
        if (RestConfig.isJsonp()){
            JsonpInInterceptor jsonpInInterceptor = new JsonpInInterceptor();
            jsonpInInterceptor.setCallbackParam(RestConfig.getJsonpFunction());
            inInterceptorList.add(jsonpInInterceptor);
            JsonpPreStreamInterceptor jsonpPreStreamInterceptor = new JsonpPreStreamInterceptor();
            outInterceptorList.add(jsonpPreStreamInterceptor);
            JsonpPostStreamInterceptor jsonpPostStreamInterceptor = new JsonpPostStreamInterceptor();
            outInterceptorList.add(jsonpPostStreamInterceptor);
        }

        //添加cors provider
        if (RestConfig.isCors()){
            CrossOriginResourceSharingFilter crossOriginResourceSharingFilter = new CrossOriginResourceSharingFilter();
            crossOriginResourceSharingFilter.setAllowOrigins(RestConfig.getCorsOriginList());
            providerList.add(crossOriginResourceSharingFilter);
        }
    }

    //发布rest服务
    public static void pulishService(String wadl, Class<?> resourceClass){
        JAXRSServerFactoryBean jaxrsServerFactoryBean = new JAXRSServerFactoryBean();
        jaxrsServerFactoryBean.setAddress(wadl);
        jaxrsServerFactoryBean.setResourceClasses(resourceClass);
        jaxrsServerFactoryBean.setResourceProvider(resourceClass, new SingletonResourceProvider(BeanHelper.getBean(resourceClass)));
        jaxrsServerFactoryBean.setProviders(providerList);
        jaxrsServerFactoryBean.setInInterceptors(inInterceptorList);
        jaxrsServerFactoryBean.setOutInterceptors(outInterceptorList);
        jaxrsServerFactoryBean.create();
    }

    //创建Rest客户端
    public static <T> T createClient(String wadl, Class<? extends T> resourceClass){
        return JAXRSClientFactory.create(wadl, resourceClass, providerList);
    }
}
