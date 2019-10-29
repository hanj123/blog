package com.hanj.blog.web.admin;

import com.hanj.blog.po.User;
import com.hanj.blog.service.UserService;
import com.hanj.blog.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
@SessionAttributes("user")
public class UserController {

    @Autowired
    private UserService userService;

    //不写路径，默认访问admin就会到这里
    @GetMapping
    public String login(){
        return "admin/login";
    }

    @RequestMapping("/login")
    public ModelAndView login(@RequestParam("username") String username, @RequestParam("password") String password,Model model){
        User user = userService.checkUser(username, password);
        ModelAndView mv = new ModelAndView();
        if(user!=null){
            user.setPassword(null);
            model.addAttribute("user",user);
            mv.setViewName("admin/index");
            return mv;
        }else {
            mv.addObject("message","用户名和密码错误");
            mv.setViewName("admin/login.html");
            return mv;
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession httpSession){
        httpSession.invalidate();
        return "admin/login";


    }
}

