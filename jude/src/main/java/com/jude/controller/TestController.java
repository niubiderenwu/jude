package com.jude.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 生成验证码
 * @author jude
 *
 */
@Controller
public class TestController {


	 /**
     * 测试
     * @param request
     * @param response
     */
	@GetMapping("/hello")
	public void helloworld(HttpServletResponse response) throws IOException {
        response.getWriter().write("Hello Spring-boot");
    }
    
  
    
}
