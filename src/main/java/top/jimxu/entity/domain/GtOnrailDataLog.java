package top.jimxu.entity.domain;

import java.io.Serializable;

/**
 * 铁总在途数据抓取日志(GtOnrailDataLog)实体类
 *
 * @author makejava
 * @since 2026-06-18 09:51:52
 */
public class GtOnrailDataLog implements Serializable {
    private static final long serialVersionUID = 585335349306104016L;
/**
     * 抓取时间
     */
    private String getdate;
/**
     * 抓取日志
     */
    private String remark;
/**
     * 回写日志
     */
    private String remarks;


    public String getGetdate() {
        return getdate;
    }

    public void setGetdate(String getdate) {
        this.getdate = getdate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}

