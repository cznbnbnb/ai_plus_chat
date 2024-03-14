package com.gdut.ai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gdut.ai.common.R;
import com.gdut.ai.entity.User;
import com.gdut.ai.entity.UserSettings;
import com.gdut.ai.service.UserService;
import com.gdut.ai.utils.EmailUtil;
import com.gdut.ai.utils.JwtUtil;
import com.gdut.ai.utils.ValidateCodeUtils;
import com.gdut.ai.view.FriendView;
import com.gdut.ai.view.RequestView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    //发送邮箱验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取邮箱
        String email = user.getEmail();
        if(!StringUtils.isEmpty(email)){
            //生成随机的6位验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("验证码：{}",code);
            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("<签名>","<模板>",email,code);
            //将生成的验证码保存到session
            session.setAttribute(email,code);
            EmailUtil.sendEmail(email,code);
            return R.success("手机短信验证码发送成功");
        }
        return R.error("手机短信验证码发送失败");
    }

    //用户使用邮箱或密码登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map,HttpSession session){
        log.info("登录信息：{}",map);
        //获取邮箱
        String email = map.get("email");
        //获取验证码
        String code = map.get("code");
        String password = map.get("password");
        if (!StringUtils.isEmpty(password)) {
            User user = userService.loginByPassword(email, password);
            if (user != null) {
                String token = JwtUtil.generateToken(user.getEmail());
                session.setAttribute("user", user.getId());
                return R.success(user).add("token", token);
            }
            return R.error("登录失败,邮箱或密码错误");
        }

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
            user.setPassword("");
            return R.success(user).add("token",token);
        }
        return R.error("登录失败,验证码错误");
    }

    //用户注册
    @PostMapping("/register")
    public R<String> register(@RequestBody Map<String, String> map, HttpSession session){
        log.info("注册信息：{}",map);
        //获取邮箱
        String email = map.get("email");
        String password = map.get("password");
        //获取验证码
        String code = map.get("code");
        //从session中获取保存的验证码
        Object codeInSession = session.getAttribute(email);
        //进行验证码比对（页面提交的验证码 与 session中保存的验证码 比对）
        if(codeInSession != null && codeInSession.equals(code)){
            //比对成功，说明注册成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail,email);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                //未在user表找到该登录用户，说明这是一个新用户，则令他自动完成注册，即将其添入表中
                user = new User();
                user.setEmail(email);
                user.setPassword(password);
                user.setStatus(1);
                userService.save(user);
                return R.success("注册成功");
            }
        }
        return R.error("注册失败");
    }

    //移动端用户退出登录
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        //清理session中的用户id
        request.getSession().removeAttribute("user");
        return R.success("退出登录成功");
    }

    //用户好友申请
    @PostMapping("/friendRequest")
    public R<String> friendRequest(@RequestBody Map<String, String> map,HttpSession session){
        log.info("好友申请信息：{}",map);
        //获取用户id
        Long userId = (Long) session.getAttribute("user");
        //获取好友id
        String friendEmail = map.get("friendId");
        //获取附加消息
        String message = map.get("message");
        //调用service层方法完成好友申请
        boolean flag = userService.friendRequest(userId,friendEmail,message);
        if(flag){
            return R.success("好友申请成功");
        }
        return R.error("好友申请失败");
    }

    //获取用户收到的申请列表
    @GetMapping("/getFriendRequest")
    public R<List<RequestView>> getFriendRequest(HttpSession session){
        //获取用户id
        Long userId = (Long) session.getAttribute("user");
        //调用service层方法获取用户收到的申请列表
        List<RequestView> list = userService.getRequest(userId);
        if (list == null || list.size() == 0){
            return R.error("暂无好友申请");
        }
        return R.success(list);
    }

    // 同意或拒绝好友申请
    @PostMapping("/handleFriendRequest")
    public R<String> handleFriendRequest(@RequestBody Map<String, String> map, HttpSession session) {
        // 获取用户id
        Long userId = (Long) session.getAttribute("user");
        // 获取好友申请id
        Long requestId = Long.parseLong(map.get("requestId"));
        // 获取操作类型
        int type = Integer.parseInt(map.get("type"));
        // 调用service层方法处理好友申请
        boolean flag = userService.handleFriendRequest(userId, requestId, type);
        if (flag) {
            return R.success("好友申请处理成功");
        }
        return R.error("好友申请处理失败");
    }

    // 获取有过交流的好友列表
    @GetMapping("/getFriendList")
    public R<List<FriendView>> getFriendList(HttpSession session) {
        // 获取用户id
        Long userId = (Long) session.getAttribute("user");
        // 调用service层方法获取用户好友列表
        List<FriendView> list = userService.getFriendList(userId);
        if (list == null || list.size() == 0) {
            return R.error("暂无已交流的好友，快去与好友聊天吧");
        }
        return R.success(list);
    }

    // 获取全部好友列表
    @GetMapping("/getAllFriendList")
    public R<List<User>> getAllFriendList(HttpSession session) {
        // 获取用户id
        Long userId = (Long) session.getAttribute("user");
        // 调用service层方法获取用户好友列表
        List<User> list = userService.getAllFriendList(userId);
        if (list == null || list.size() == 0) {
            return R.error("暂无好友，快去添加好友吧");
        }
        return R.success(list);
    }

    @PostMapping("/settings")
    public R<?> updateUserSettings(@RequestBody UserSettings settings, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("user");
            if (userId == null) {
                return R.error("请先登录");
            }
            // 更新用户信息
            userService.updateSettings(settings, userId);
            return R.success("设置更新成功");
        } catch (Exception e) {
            log.error("更新设置失败", e);
            return R.error("更新设置失败："+e.getMessage());
        }
    }

    //删除好友
    @PostMapping("/deleteContact")
    public R<String> deleteFriend(@RequestBody Map<String, String> map,HttpSession session){
        //获取用户id
        Long userId = (Long) session.getAttribute("user");
        //获取好友id
        Long friendId = Long.parseLong(map.get("contactId"));
        //调用service层方法删除好友
        boolean flag = userService.deleteFriend(userId,friendId);
        if(flag){
            return R.success("删除好友成功");
        }
        return R.error("删除好友失败");
    }



}
