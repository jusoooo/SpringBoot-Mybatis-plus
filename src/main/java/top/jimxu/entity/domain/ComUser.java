package top.jimxu.entity.domain;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "COM_USER")
@Data
public class ComUser {

    @TableId
    private Long id;
    private String userid;
    private String userame;
    private String password;
    private String sex;
    private String tel;
    private String email;
    private String companyid;
    private Boolean enable;
    private String passtime;
    private String isLock;
    private String pwErrorTime;
    private String lockTime;
}
