package top.jimxu.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AvgPassVo {

    private String fzcn;

    private String stationcn;

    private String trainnum;

    private String stationnum;

    private String passtime;

    private String ingatetime;

    private String outgatetime;

    private String ids;


    private List<AvgPassCout> stationAvgCount;

    @Data
    public static class AvgPassCout{
        private String stationcn;
        private BigDecimal count;
    }
}
