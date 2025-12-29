package top.jimxu.entity.dto;

import lombok.Data;

import java.util.List;


@Data
public class AvgPassDto {

    private List<String> passTimeArr;
    private List<String> dzcnList;
    private List<String> stationcnList;
    private String ids;
    private String fzcn;
    private Double downAvgTime;

}
