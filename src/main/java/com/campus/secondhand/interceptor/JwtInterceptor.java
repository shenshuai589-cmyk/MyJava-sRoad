package com.campus.secondhand.interceptor;

import com.campus.secondhand.utils.JwtUtil;
import com.campus.secondhand.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头token
        String token = request.getHeader("token");

        // 2.判断token是否存在
        if(token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }

        // 3.去掉Bearer
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try{
            //4.解析token
            Long userId = JwtUtil.parseToken(token);

            // 5.保存用户id
            UserContext.setUserId(userId);
        }catch (Exception e){
            response.setStatus(401);
            return false;
        }
        return true;
    }
    // 请求结束清除
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.remove();
    }
}
