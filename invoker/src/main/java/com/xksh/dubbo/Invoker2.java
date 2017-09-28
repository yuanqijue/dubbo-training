package com.xksh.dubbo;

import com.xksh.dubbo.service.PublishService;

import java.net.MalformedURLException;

/**
 * Created by aaron on 2017/9/28.
 */
public class Invoker2 {

    public static void main(String[] args) throws MalformedURLException {
        PublishService service = RegistryFactory.getService(PublishService.class);
        System.out.println(service.sayHello("aaron"));
    }

}
