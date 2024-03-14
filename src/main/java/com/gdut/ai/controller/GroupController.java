package com.gdut.ai.controller;

import com.gdut.ai.common.R;
import com.gdut.ai.entity.FriendRequest;
import com.gdut.ai.entity.GroupTable;
import com.gdut.ai.entity.User;
import com.gdut.ai.service.GroupsService;
import com.gdut.ai.view.FriendView;
import com.gdut.ai.view.GroupView;
import kotlin.contracts.Returns;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/group")
@Slf4j
public class GroupController {

    @Autowired
    private GroupsService groupsService;

    @PostMapping("/create")
    public R<String> createGroup(@RequestBody Map<String, String> map, HttpSession session) {
        // 获取用户id
        Long userId = (Long) session.getAttribute("user");
        // 获取群组名称
        String groupName = map.get("name");
        // 获取群组头像
        String groupAvatar = map.get("avatar");
        // 创建群组
        return groupsService.createGroup(userId, groupName, groupAvatar);

    }

    //入群申请
    @PostMapping("/joinGroup")
    public R<String> joinGroup(@RequestBody Map<String, String> map, HttpSession session){
        log.info("入群申请信息：{}",map);
        //获取用户id
        Long userId = (Long) session.getAttribute("user");
        //获取群组名称
        String name = map.get("name");
        //获取附加消息
        String message = map.get("message");
        //调用service层方法完成入群申请
        boolean flag = groupsService.joinGroup(userId,name,message);
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
        boolean flag = groupsService.handleGroupRequest(userId, requestId, type);
        if (flag) {
            return R.success("入群申请处理成功");
        }
        return R.error("入群申请处理失败");
    }

    //获取全部群组列表
    @GetMapping("/getAllGroupList")
    public R<List<GroupTable>> getAllGroupList(HttpSession session){
        //获取用户id
        Long userId = (Long) session.getAttribute("user");
        //调用service层方法获取用户群组列表
        List<GroupTable> list = groupsService.getAllGroupList(userId);
        if (list == null || list.size() == 0){
            return R.error("暂无群组");
        }
        return R.success(list);
    }

    //获取有过交流的群组列表
    @GetMapping("/getGroupList")
    public R<List<GroupView>> getGroupList(HttpSession session){
        //获取用户id
        Long userId = (Long) session.getAttribute("user");
        //调用service层方法获取用户群组列表
        List<GroupView> list = groupsService.getGroupList(userId);
        if (list == null || list.size() == 0){
            return R.error("暂无已交流的群组");
        }
        return R.success(list);
    }

    //将用户移出群聊
    @PostMapping("/removeUser")
    public R<String> removeUser(@RequestBody Map<String, String> map, HttpSession session){
        //获取用户id
        Long userId = (Long) session.getAttribute("user");
        //获取群组id
        Long groupId = Long.parseLong(map.get("groupId"));
        //获取被移出的用户id
        Long removeUserId = Long.parseLong(map.get("removeUserId"));
        //调用service层方法将用户移出群聊
        boolean flag = groupsService.removeUser(userId,groupId,removeUserId);
        if(flag){
            return R.success("移出群聊成功");
        }
        return R.error("移出群聊失败");
    }

    //解散群聊
    @PostMapping("/deleteGroup")
    public R<String> deleteGroup(@RequestBody Map<String, String> map, HttpSession session){
        //获取用户id
        Long userId = (Long) session.getAttribute("user");
        //获取群组id
        Long groupId = Long.parseLong(map.get("groupId"));
        //调用service层方法解散群聊
        boolean flag = groupsService.removeUser(userId,groupId,userId);
        if(flag){
            return R.success("解散群聊成功");
        }
        return R.error("解散群聊失败");
    }

    //获取群成员列表
    @GetMapping("/getGroupMemberList")
    public R<List<User>> getGroupMemberList(@RequestParam("groupId") Long groupId){
        //调用service层方法获取群成员列表
        List<User> list = groupsService.getGroupMemberList(groupId);
        if (list == null || list.size() == 0){
            return R.error("暂无群成员");
        }
        return R.success(list);
    }



}
