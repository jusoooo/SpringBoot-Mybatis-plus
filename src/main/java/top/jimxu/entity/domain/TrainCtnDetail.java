package top.jimxu.entity.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "TRAIN_CTN_DETAIL")
@KeySequence("SEQ_TRAIN_CTN_DETAIL")
public class TrainCtnDetail {


    @TableId(type = IdType.INPUT)
    private String ctnid;
    /**
     *
     */
    private String planid;
    /**
     * 计划日期
     */
    private String plandate;
    /**
     * 计划号
     */
    private String planno;
    /**
     * 进入某站的截止日期/还箱截止日期
     */
    private String applyenddate;
    /**
     * 火车车号
     */
    private String traincode;
    /**
     * 火车车名
     */
    private String trainname;
    /**
     * 火车航次
     */
    private String trainvoyage;
    /**
     * 火车车名车次
     */
    @TableField(exist = false)
    private String trainInfo;
    /**
     * 火车航向（SEND-铁路发送，ARRIVE-铁路到达）
     */
    private String traindirect;
    /**
     * 英文船名
     */
    private String vesselname;
    /**
     * 航次
     */
    private String voyage;
    /**
     * 船名/航次
     */
    @TableField(exist = false)
    private String vesselInfo;
    /**
     * 船舶航向（I-进口，E-出口）
     */
    private String direction;
    /**
     * 到港日期
     */
    private String arrivetime;
    /**
     * 预计码头截单日期
     */
    private String portclosetime;
    /**
     * 提单号
     */
    private String billno;
    /**
     * 进港提单号
     */
    private String inbillno;
    /**
     * 重量
     */
    private String weight;
    /**
     * 件数
     */
    private String packetnum;
    /**
     * 体积
     */
    private String measure;
    /**
     * 起运港
     */
    private String loadport;
    /**
     * 中转港（卸货港）
     */
    private String disport;
    /**
     * 目的港
     */
    private String desport;
    /**
     * 箱主代码
     */
    private String ctnowner;
    /**
     * 进港箱主代码
     */
    private String inctnowner;
    /**
     * 空重
     */
    private String ctnstatus;
    /**
     * 箱型尺寸
     */
    private String ctnsizetype;
    /**
     * 箱号
     */
    private String ctnno;
    /**
     * 内外贸
     */
    private String tradenw;
    /**
     * 卸船时间（卸车时间
     */
    private String dischargetime;
    /**
     * 出门时间
     */
    private String outgatetime;
    /**
     * 进门时间
     */
    private String ingatetime;
    /**
     * 查验时间
     */
    private String cusmovtime;
    /**
     * 海关放行时间
     */
    private String custompasstime;
    /**
     * 码头反馈放行时间
     */
    private String portpasstime;
    /**
     * 装船时间（装车时间）
     */
    private String loadtime;
    /**
     * 火车车皮号
     */
    private String trainno;
    /**
     * 贝位
     */
    private String position;
    /**
     * 转码头海关审批时间
     */
    private String tranpasstime;
    /**
     * 海关审批人
     */
    private String operator;
    /**
     * 海关审批状态
     */
    private String trancustomstatus;
    /**
     * 承运人
     */
    private String carrier;
    /**
     * 申报单编号
     */
    private String custno;
    /**
     * 集卡车牌号
     */
    private String truckno;
    /**
     * 铅封号1
     */
    private String sealno1;
    /**
     * 铅封号2
     */
    private String sealno2;
    /**
     * 状态位(Y=卸车计划已执行，D=装车计划已执行，S=已申请，M=已审核, Z=装车报告已发送,X=卸车报告已发送)
     */
    private String flag;
    /**
     * 货代代码
     */
    private String hdagent;
    /**
     * 发货人
     */
    private String shipper;
    /**
     * 收货人
     */
    private String consigee;
    /**
     * 受票人
     */
    private String spperson;
    /**
     * 进提箱地（火车到达-进箱地，火车出发-提箱地）
     */
    private String yardname;
    /**
     * 进提箱地代码（火车到达-进箱地，火车出发-提箱地）
     */
    private String yardcode;
    /**
     * 火车停靠地
     */
    private String trainplace;
    /**
     * 客户信息
     */
    private String customer;
    /**
     * 货物品名
     */
    private String goods;
    /**
     * 铁路代理信息
     */
    private String agent;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 单证提供时间
     */
    private String danzhengtime;
    /**
     * 计划申报时间
     */
    private String plantime;
    /**
     * 计划申报人
     */
    private String planuser;
    /**
     * 计划关闭时间
     */
    private String closedate;
    /**
     * 计划关闭人
     */
    private String closeuser;
    /**
     * 有效位
     */
    private String enabled;
    /**
     * 发站
     */
    private String fz;
    /**
     * 到站
     */
    private String dz;
    /**
     * 货票号
     */
    private String hpno;
    /**
     * 零星勾选时间
     */
    private String gxtime;
    /**
     * 船舶UN
     */
    private String vesselcode;
    /**
     * 外堆场预约状态
     */
    private String obligate1;
    /**
     * 车队驳箱状态
     */
    private String obligate2;
    /**
     * 进提箱申请状态
     * -- 进箱申请已审核
     */
    private String obligate3;
    /**
     * 运单号
     */
    private String obligate4;
    /**
     * XTT报文发送状态Y-已发送 N-未发送
     */
    private String obligate5;
    /**
     * 内转外运抵发送海关标记（1已发送,其他未发送）
     */
    private String flag1;
    /**
     * 重箱预录入发送码头标记（1已发送,其他未发送）
     */
    private String flag2;
    /**
     * 船图舱单发送标记(1已发送,其他未发送）
     */
    private String flag3;
    /**
     * 日计划标记（Y已申请，N未申请， X待删除，U港站未承认，S已发送国铁）
     */
    private String dailyflag;
    /**
     * 堆场位置
     */
    private String yardlocation;
    /**
     * 火车股道
     */
    private String traintrack;
    /**
     * 预约号
     */
    private String reservation;
    /**
     * 准装指令时间
     */
    private String lcmdtime;
    /**
     * 码头是否反馈结果，是否放行（最新）（Y：可以放行，T：取消放行，其他：不能放行）
     */
    private String portflag;
    /**
     * 码头反馈意见（最新）
     */
    private String portremark;
    /**
     * 码头费用结算章里的货代代码
     */
    private String agentcode;
    /**
     * 码头费用结算章里的货代名称
     */
    private String agentname;
    /**
     * 申报单生成时间
     */
    private String custnotime;
    /**
     * 揽货单位
     */
    private String lhagent;
    /**
     * 客户单位
     */
    private String khagent;
    /**
     * 港内单位
     */
    private String portagent;
    /**
     * 装箱单码头接收FLAG
     */
    private String costcoflag;
    /**
     * 装箱单码头接收备注
     */
    private String costcoremark;
    /**
     * 发送车队指令时间
     */
    private String sendtruck;
    /**
     * 进门英文船名
     */
    private String invesselname;
    /**
     * 进门un
     */
    private String invesselcode;
    /**
     * 进门航次
     */
    private String invoyage;
    /**
     * 顺位方向2自东向西1自西向东3自南向北4自北向南
     */
    private String cisposition;
    /**
     * 顺位号
     */
    private String cispositionnum;
    /**
     * 偏载率
     */
    private String pattialload;
    /**
     * 发送初步计划ID
     */
    private String baseplanid;
    /**
     * 待驳车队,集运JY铃与LY其他QT
     */
    private String transfertruck;
    /**
     * 箱类型,海铁H,内贸N,国联G
     */
    private String ctntype;
    /**
     * 箱属性,订舱箱D-增加出口截关期,预驳箱B
     */
    private String ctnattribute;
    /**
     * 政府补贴申领单位
     */
    private String government;
    /**
     * 发送最终计划ID
     */
    private String finalplanid;
    /**
     * 龙门吊
     */
    private String gantrycrane;
    /**
     * 是否转过车次
     */
    private String transfervoyage;
    /**
     * 报关类型：N-无需报关（空箱、内贸），ZG-转关，FB-甬舟分拨箱，NZX-甬舟内支线，YTH-一体化报关，ZBG-宁波本地报关
     */
    private String declaretype;
    /**
     * 码头/通达直装标记
     */
    private String mttdflag;
    /**
     * 换箱号之前的箱号
     */
    private String oldctnno;
    /**
     * app装卸标记，Y以后无法被APP查询
     */
    private String zxflag;
    /**
     * app装卸标记时间
     */
    private String zxflagtime;
    /**
     * 股道操作记录ID
     */
    private String operationid;
    /**
     * 装卸车操作人
     */
    private String operater;
    /**
     * CCA标记
     */
    private String ccaflag;
    /**
     * 转船等通知标记
     * (Y 等通知，N 不需要等通知)
     */
    private String transferFlag;
    /**
     * 铁路箱标记
     */
    private String railwayfalg;
    /**
     * 容灾装车时间
     */
    private String appdischargetime;
    /**
     * 容灾卸车时间
     */
    private String apploadtime;
    /**
     * 堆场提箱预约号
     */
    private String yardreservation;
    /**
     * 无欧美舱单退关
     */
    private String customcancel;
    /**
     * 出口三联单时间
     */
    private String triplicatelist;
    /**
     * 空箱备注信息
     */
    private String emptyremark;
    /**
     * 海铁标记箱报文是否不带卸车时间。Y为不带，其他为带。
     */
    private String srtctndisflag;
    /**
     * CCA发船公司提单（一般用于拼箱）
     */
    private String ccabillno;
    /**
     * 海铁综合服务系统箱编号
     */
    private String srisctnno;
    /**
     * 空箱计划来源，01：海铁综合服务系统
     */
    private String emptyplaysource;
    /**
     * 通过其他系统录入用户信息
     */
    private String planotheruser;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
//    @JsonIgnore
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
//    @JsonIgnore
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.UPDATE)
//    @JsonIgnore
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonIgnore
    private Date updateTime;


    /**
     * minimal constructor
     */
    public TrainCtnDetail(String ctnid) {
        this.ctnid = ctnid;
    }

    /**
     * 危险品类别
     */
    private String dangeroustype;
    /**
     * 危险品联合国编号
     */
    private String dangerousun;
    /**
     * 偏载标记
     */
    private String partLoadFlag;
    /**
     * 业务确认 Y 确认
     */
    private String serConfirm;
    /**
     * 业务确认人
     */
    private Long serConfirmBy;
    /**
     * 业务确认时间
     */
    private Date serConfirmTime;
    /**
     * 海铁科确认 Y 确认
     */
    private String busConfirm;
    /**
     * 海铁科确认人
     */
    private Long busConfirmBy;
    /**
     * 海铁科确认时间
     */
    private Date busConfirmTime;
    /**
     * 商务确认 Y 确认
     */
    private String mngConfirm;
    /**
     * 商务确认人
     */
    private Long mngConfirmBy;
    /**
     * 商务确认时间
     */
    private Date mngConfirmTime;

    /**
     * 兴港确认 Y 确认
     */
    private String xgConfirm;
    /**
     * 兴港确认人
     */
    private Long xgConfirmBy;
    /**
     * 兴港确认时间
     */
    private Date xgConfirmTime;
    /**
     * 铁司确认 Y
     */
    private String tsConfirm;
    /**
     * 铁司确认人
     */
    private Long tsConfirmBy;
    /**
     * 铁司确认时间
     */
    private Date tsConfirmTime;

    /**
     * 计算标记 Y 已计算
     */
    private String caculateFlag;
    /**
     * 推送标记 Y 已推送
     */
    private String pushFlag;
    /**
     * 推送结果 pushFlag为N不为空
     */
    private String pushRes;
    /**
     * 业务确认 倒换箱号
     */
    private String payCtnno;
    /**
     * 业务确认 倒换箱型尺寸
     */
    private String payCtnsizetype;
    /**
     * 业务确认 倒换箱主
     */
    private String payCtnowner;
    /**
     * 业务确认 中转码头 坐驳船去涌州的
     */
    private String payTransPort;

    private String interlineWaybillNo;

    @TableField(exist = false)
    private String applyvesselcode;

    @TableField(exist = false)
    private String applyvoyage;

    @TableField(exist = false)
    private String ctnStartTime;

    @TableField(exist = false)
    private String eta;

    @TableField(exist = false)
    private String etd;

    @TableField(exist = false)
    private String ata;

    @TableField(exist = false)
    private String atd;

    /**
     * 业务确认 开箱照片状态
     */
    private Long unboxingPhotoStatus;

    /**
     * 倒换类型
     * A 一大 -> 一大
     * B 一大 -> 一小
     * C 一小 -> 一小
     * D 一小 -> 一大
     * E 一大 -> 两小
     */
    private String changeFlag;

    @TableField(exist = false)
    private List<String> photoDirs;

    @TableField(exist = false)
    private Integer yzZgStatus;

    // 甬舟转关用户输入车牌号
    @TableField(exist = false)
    private String yzZgLicense;

    // 北一高箱异物查验标记
    @TableField(exist = false)
    private String blctCheckFlag;
    @TableField(exist = false)
    private String blctCheckUser;
    @TableField(exist = false)
    private String blctCheckTime;

    // 驳船申请状态
    @TableField(exist = false)
    private Integer bargeApplyStatus;

    // 驳船状态
    @TableField(exist = false)
    private Integer bargeStatus;
    //易港通支付标记
    @TableField(exist = false)
    private String paystatus;
    /**
     * 落空标记 N 非落空  Y 落空
     */
    private String lkFlag;

    /**
     * 终止标记 Y 已终止 N 未终止
     */
    private String finishFlag;
    private Long checkId;

    /**
     * 实装船名
     */
    private String bcVesselname;

    /**
     * 实装航次
     */
    private String bcVoyage;

    /**
     * 实装UN
     */
    private String bcVesselcode;

    /**
     * 驳船装船时间
     */
    private String bcVesselLoadtime;

    /**
     * 驳船卸船时间
     */
    private String bcVesselDistime;


    // 中转清单发送标志
    @TableField(exist = false)
    private String tranListStatus;

    public void setPhotoDirs(String photoDirs) {
        if (StrUtil.isBlank(photoDirs)) {
            this.photoDirs = new ArrayList<>();
        } else {
            this.photoDirs = Arrays.asList(photoDirs.split(","));
        }
    }

}
