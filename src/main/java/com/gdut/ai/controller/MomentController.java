package com.gdut.ai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdut.ai.common.R;
import com.gdut.ai.entity.Moment;
import com.gdut.ai.service.MomentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/moment")
@Slf4j
public class MomentController {

    @Autowired
    private MomentService momentsService;

    // 发送朋友圈
    @PostMapping("/sendMoment")
    public R<String> sendMoment(@RequestBody Map<String, String> map, HttpSession session) {
        // 获取用户id
        Long userId = (Long) session.getAttribute("user");
        // 获取朋友圈内容
        String content = map.get("content");
        // 获取图片
        String images = map.get("images");
        // 发送朋友圈
        boolean flag = momentsService.sendMoment(userId, content, images);
        if (flag) {
            return R.success("发送朋友圈成功");
        }
        return R.error("发送朋友圈失败");
    }

    // 获取朋友圈
    @GetMapping("/getMoments")
    public R<Page<Moment>> getMoments(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "20") Integer pageSize,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        return R.success(momentsService.getMoments(userId, page, pageSize));
    }

    // 获取自己的朋友圈
    @GetMapping("/getMyMoments")
    public R<Page<Moment>> getMyMoments(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "20") Integer pageSize,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        return R.success(momentsService.getMyMoments(userId, page, pageSize));
    }

    // 删除朋友圈
    @PostMapping("/deleteMoment")
    public R<String> deleteMoment(@RequestBody Map<String, String> map, HttpSession session) {
        // 获取用户id
        Long userId = (Long) session.getAttribute("user");
        // 获取朋友圈id
        Long momentId = Long.parseLong(map.get("momentId"));
        // 删除朋友圈
        boolean flag = momentsService.deleteMoment(userId, momentId);
        if (flag) {
            return R.success("删除朋友圈成功");
        }
        return R.error("删除朋友圈失败");
    }
}
