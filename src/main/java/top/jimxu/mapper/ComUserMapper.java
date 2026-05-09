package top.jimxu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.jimxu.entity.domain.ComUser;

import java.util.List;

public interface ComUserMapper extends BaseMapper<ComUser> {

    List<ComUser> getNeedUpdatePasswordUser();

}
