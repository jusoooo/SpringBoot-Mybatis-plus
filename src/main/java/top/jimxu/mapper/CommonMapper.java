package top.jimxu.mapper;

import top.jimxu.entity.dto.AvgPassDto;
import top.jimxu.entity.vo.AvgPassVo;

import java.util.List;

public interface CommonMapper {


    List<AvgPassVo> listAvgPass(AvgPassDto avgPassDto);

}
