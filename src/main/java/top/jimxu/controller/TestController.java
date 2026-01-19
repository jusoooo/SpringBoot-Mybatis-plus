package top.jimxu.controller;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.jimxu.entity.domain.R;
import top.jimxu.entity.domain.SrAppointPoolDetail;
import top.jimxu.mapper.SrAppointPoolDetailMapper;

@RequestMapping("/testBug")
@RestController
@RequiredArgsConstructor
public class TestController {


    private final SrAppointPoolDetailMapper srAppointPoolDetailMapper;


    @GetMapping("/getSrAppointPoolDetailById")
    public R getSrAppointPoolDetailById(@RequestParam String id){
        SrAppointPoolDetail srAppointPoolDetail = srAppointPoolDetailMapper.selectById(id);
        if (StrUtil.equals(srAppointPoolDetail.getIsCheck(),"1")){
            throw new RuntimeException("预约号状态不为核销，无法取消核销!");
        }
        return R.success(srAppointPoolDetail);
    }




}
