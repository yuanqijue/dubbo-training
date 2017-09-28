package com.xksh.dubbo;

import com.xksh.dubbo.service.PublishService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by aaron on 2017/9/27.
 */
@WebListener
public class Provider1 implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().getServletRegistrations();

        RegistryFactory.register(PublishService.class, "http://127.0.0.1:8080/hello");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        RegistryFactory.unRegister(PublishService.class);
    }
}
