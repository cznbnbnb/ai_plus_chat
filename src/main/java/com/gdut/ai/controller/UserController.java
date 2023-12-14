package com.gdut.ai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gdut.ai.common.R;
import com.gdut.ai.entity.User;
import com.gdut.ai.service.UserService;
import com.gdut.ai.utils.JwtUtil;
import com.gdut.ai.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    //发送手机短信验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取邮箱
        String email = user.getEmail();
        if(!StringUtils.isEmpty(email)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码：{}",code);
            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("<签名>","<模板>",email,code);

            //将生成的验证码保存到session
            session.setAttribute(email,code);
            return R.success("手机短信验证码发送成功");
        }
        return R.error("手机短信验证码发送失败");
    }

    //移动端用户登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        log.info("登录信息：{}",map);
        //获取邮箱
        String email = map.get("email").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获取保存的验证码
        Object codeInSession = session.getAttribute(email);
        //进行验证码比对（页面提交的验证码 与 session中保存的验证码 比对）
        if(codeInSession != null && codeInSession.equals(code)){
            //比对成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail,email);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                //未在user表找到该登录用户，说明这是一个新用户，则令他自动完成注册，即将其添入表中
                user = new User();
                user.setEmail(email);
                user.setStatus(1);
                userService.save(user);
            }
            String token = JwtUtil.generateToken(user.getEmail());
            session.setAttribute("user",user.getId());
            return R.success(user).add("token",token);
        }
        return R.error("登录失败");
    }

    //移动端用户退出登录
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        //清理session中的用户id
        request.getSession().removeAttribute("user");
        return R.success("退出登录成功");
    }
}
