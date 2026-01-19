package top.jimxu.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "SR_APPOINT_POOL_DETAIL")
@KeySequence("SEQ_SR_APPOINT_POOL_DETAIL")
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SrAppointPoolDetail implements Serializable {
    private static final long serialVersionUID =  1483442122581078631L;

    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 预约ID
     */
    private Long appointId;

    /**
     * 预约号
     */
    private String appointNo;

    /**
     * 预约单位ID
     */
    private Long appointDeptId;

    /**
     * 预约时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date appointTime;

    /**
     * 取消标记（1-已取消，0未取消）
     */
    private String isCancel;

    /**
     * 取消时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    /**
     * 取消原因
     */
    private String cancelRemark;

    /**
     * 核销标记（1-已核销，0-未核销）
     */
    private String isCheck;

    /**
     * 核销时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

    /**
     * 核销铅封
     */
    private String checkSealno;

    /**
     * 核销
     */
    private String checkCtnno;

    /**
     * 核销回退原因
     */
    private String checkRevokeRemark;

    /**
     * 指定标记（1-已指定，0-未指定）
     */
    private String isAssigned;

    /**
     * 指定提空箱号，指定标记为1时不能为空
     */
    private String assignedCtnno;

    /**
     * 在场箱子ID
     */
    private Long ctnInYardNodeId;

    /**
     * 委托单箱ID
     */
    private Long srContainerId;

    /**
     * 过期时间
     */
    private Date expireDate;

    /**
     * 外部铅封标记
     * 针对全部订舱箱和部分预驳箱
     * 默认为N
     */
    private String sealNoFlag;
}

