package top.jimxu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import top.jimxu.entity.domain.TrainCtnDetailHistory;

public interface TrainCtnDetailHistoryDao extends BaseMapper<TrainCtnDetailHistory> {

    /**
     * 取主键序列下一个值
     * （MyBatis-Plus 3.1.0 的 Insert 注入器不支持 @KeySequence 自动取 Oracle 序列，IdType.INPUT 时需手动赋值）
     */
    @Select("select seq_train_ctn_detail.nextval from dual")
    String selectNextCtnId();
}
