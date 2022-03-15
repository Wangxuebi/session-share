package com.springredis.sessionshare.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

/**
 * @author wxb
 * @date 2022-03-14 11:12
 */
@RestController
public class UserController {
    private Logger logger = Logger.getAnonymousLogger();

    @Value("${server.port}")
    private String server_port;


    @GetMapping("userLogin")
    public String userLogin(HttpSession httpSession){
        logger.info("用户登录...");
        httpSession.setAttribute("user","wxb");

        JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;

        ObjectNode jsonNodes = jsonNodeFactory.objectNode();

        jsonNodes.put("port",server_port);
        jsonNodes.put("time",httpSession.getLastAccessedTime());

        jsonNodes.put("sessionId",httpSession.getId());

        jsonNodes.put("message", "用户登陆，添加 session 数据，user=wxb");

        return jsonNodes.toString();
    }

    @GetMapping("userQuit")
    public String userQuit(HttpSession httpSession){
        logger.info("用户退出...");
        Object user = httpSession.getAttribute("user");//当前session不存在user时，返回未登录


        JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;

        ObjectNode jsonNodes = jsonNodeFactory.objectNode();
        jsonNodes.put("port",server_port);
        jsonNodes.put("sessionId",httpSession.getId());
        jsonNodes.put("time",httpSession.getLastAccessedTime());
        if (user != null) {
            jsonNodes.put("user",String.valueOf(user));
            jsonNodes.put("message","用户退出，移除 session 数据");
            httpSession.removeAttribute("user");
        }else {
            jsonNodes.put("user", "null");
            jsonNodes.put("message","用户未登录，当前 HttpSession 并没有保持数据");
        }
        return jsonNodes.toString();


    }

}
