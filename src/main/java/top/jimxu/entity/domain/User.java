package top.jimxu.entity.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("users")
public class User {
    @TableId("user_id")
    private int userId;
    private String userName;
    private int userAge;
}
