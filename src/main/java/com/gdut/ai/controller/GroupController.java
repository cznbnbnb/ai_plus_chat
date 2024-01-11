package com.gdut.ai.controller;

import com.gdut.ai.common.R;
import com.gdut.ai.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;


@RestController
@RequestMapping("/group")
@Slf4j
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/create")
    public R<String> createGroup(@RequestBody Map<String, String> map, HttpSession session) {
        // 获取用户id
        Long userId = (Long) session.getAttribute("user");
        // 获取群组名称
        String groupName = map.get("groupName");
        // 获取群组头像
        String groupAvatar = map.get("groupAvatar");
        // 创建群组
        boolean flag = groupService.createGroup(userId, groupName, groupAvatar);
        if (flag) {
            return R.success("创建群组成功");
        }
        return R.error("创建群组失败");
    }

    //入群申请
    @PostMapping("/joinGroup")
    public R<String> joinGroup(@RequestBody Map<String, String> map, HttpSession session){
        log.info("入群申请信息：{}",map);
        //获取用户id
        Long userId = (Long) session.getAttribute("user");
        //获取群组id
        Long groupId = Long.parseLong(map.get("groupId"));
        //获取附加消息
        String message = map.get("message");
        //调用service层方法完成入群申请
        boolean flag = groupService.joinGroup(userId,groupId,message);
        if(flag){
            return R.success("入群申请成功");
        }
        return R.error("入群申请失败");
    }

    // 同意或拒绝入群申请
    @PostMapping("/handleGroupRequest")
    public R<String> handleGroupRequest(@RequestBody Map<String, String> map, HttpSession session) {
        // 获取用户id
        Long userId = (Long) session.getAttribute("user");
        // 获取入群申请id
        Long requestId = Long.parseLong(map.get("requestId"));
        // 获取操作类型
        int type = Integer.parseInt(map.get("type"));
        // 调用service层方法处理入群申请
        boolean flag = groupService.handleGroupRequest(userId, requestId, type);
        if (flag) {
            return R.success("入群申请处理成功");
        }
        return R.error("入群申请处理失败");
    }






}
