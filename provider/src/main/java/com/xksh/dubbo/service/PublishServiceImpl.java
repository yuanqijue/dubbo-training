package com.xksh.dubbo.service;

import com.caucho.hessian.server.HessianServlet;

import javax.servlet.annotation.WebServlet;

/**
 * Created by aaron on 2017/9/27.
 */

@WebServlet(urlPatterns = "/hello", loadOnStartup = 1)
public class PublishServiceImpl extends HessianServlet implements PublishService {

    public String sayHello(String username) {
        return "Hello " + username;
    }
}
