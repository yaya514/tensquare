package com.tensquare.qa.interceptor;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader("Authorization");

        if (header != null && !"".equals(header)) {
            //如果请求中有包含Authorization头信息，就对其进行解析
            if (header.startsWith("Bearer ")) {
                //获得token
                String token = header.substring(7);
                try {
                    Claims claims = jwtUtil.parseJWT(token);
                    String roles = (String) claims.get("roles");
                    if (roles != null && roles.equals("admin")) {
                        request.setAttribute("claim_admin", token);
                    }
                    if (roles != null && roles.equals("user")) {
                        request.setAttribute("claim_user", token);
                    }

                } catch (Exception e) {
                    throw new RuntimeException("令牌不正确！");
                }
            }
        }

        System.out.println("经过了拦截器");
        return true;
    }
}
