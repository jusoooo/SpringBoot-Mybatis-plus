package top.jimxu.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import top.jimxu.Util.RandomPasswordUtil;
import top.jimxu.Util.Sha256Util;
import top.jimxu.entity.domain.ComUser;
import top.jimxu.entity.domain.R;
import top.jimxu.entity.domain.User;
import top.jimxu.service.SmsService;
import top.jimxu.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SmsService smsService;


    @GetMapping("/{id}")
    public User selById(@PathVariable int id){
        return userService.findUserById(id);
    }
    @GetMapping()
    public List<User> selUser(){
        return userService.selectList(null);
    }
    @PostMapping()
    public void addUser(@RequestBody User user){
        userService.insertUser(user);
    }
    @PutMapping()
    public void updateUser(@RequestBody User user){
        userService.insertUser(user);
    }
    @DeleteMapping()
    public void delUser(User user){
        userService.deleteUser(user);
    }

    @GetMapping("/updatePassword")
    public R updatePassword(){
        List<ComUser> needUpdatePasswordUser = userService.getNeedUpdatePasswordUser();
        log.info("needUpdatePasswordUser:{}", JSON.toJSONString(needUpdatePasswordUser));
        for (ComUser comUser : needUpdatePasswordUser) {
            // 生成6位随机密码
            String password = RandomPasswordUtil.generatePwd(6);
            // sha256加密
            String sha256Password = Sha256Util.sha256(password);
            comUser.setPassword(sha256Password);
            userService.updateComUser(comUser);
            // 发送短信通知
            log.info("用户{}的密码改为{}",comUser.getTel(),password);
            String smsContent = String.format("(海铁联运系统)尊敬的用户,因安全需要,您的[%s]账号密码已修改为%s。",comUser.getTel(),password);
            smsService.sendSms(smsContent,comUser.getTel());
        }
        return R.success("修改成功!");
    }
}
