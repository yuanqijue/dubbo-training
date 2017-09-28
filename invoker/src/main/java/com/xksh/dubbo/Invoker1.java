package com.xksh.dubbo;

import com.caucho.hessian.client.HessianProxyFactory;
import com.xksh.dubbo.service.PublishService;

import java.net.MalformedURLException;

/**
 * Created by aaron on 2017/9/27.
 */
public class Invoker1 {

    public static void main(String[] args) throws MalformedURLException {
        String url = "http://localhost:8080/hello";
        HessianProxyFactory factory = new HessianProxyFactory();
        PublishService service = (PublishService) factory.create(PublishService.class, url);
        System.out.println(service.sayHello("aaron wang"));
    }
}
