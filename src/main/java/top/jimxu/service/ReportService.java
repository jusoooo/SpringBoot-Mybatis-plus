package top.jimxu.service;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.jimxu.entity.dto.AvgPassDto;
import top.jimxu.entity.vo.AvgPassVo;
import top.jimxu.mapper.CommonMapper;

import java.util.List;

@Component
@AllArgsConstructor
public class ReportService {

    @Autowired
    private CommonMapper commonMapper;


    public List<AvgPassVo> listAvgPass(AvgPassDto dto){
        return commonMapper.listAvgPass(dto);
    }


}


