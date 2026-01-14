package top.jimxu.controller;


import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.jimxu.entity.dto.AvgPassDto;
import top.jimxu.entity.domain.R;
import top.jimxu.entity.vo.AvgPassVo;
import top.jimxu.service.ReportService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/report")
public class ReportController {


    @Autowired
    private ReportService reportService;



    @PostMapping("/listAvgPass")
    public R listAvgPass(@RequestBody AvgPassDto avgPassDto){
        List<AvgPassVo> list = reportService.listAvgPass(avgPassDto);
        Map<String, List<AvgPassVo>> collect = list.stream().collect(Collectors.groupingBy(AvgPassVo::getStationcn));

        //一个总共的，一个大碶+宝幢的，一个慈城+乔司+蜀山的，一个金华东+合肥东德
        List<Map<String,Object>> mapList = new ArrayList();
        for (String stationcn : collect.keySet()) {
            HashMap<String, Object> map = new HashMap<>();
            //平均总时长
            BigDecimal passtimeTotal = collect.get(stationcn).stream().map(item -> new BigDecimal(item.getPasstime()).multiply(new BigDecimal(item.getStationnum()))).reduce(BigDecimal::add).get();
            BigDecimal stationnumTotal = collect.get(stationcn).stream().map(item -> new BigDecimal(item.getStationnum())).reduce(BigDecimal::add).get();
            BigDecimal divide = passtimeTotal.divide(stationnumTotal, 2, BigDecimal.ROUND_DOWN);
            map.put("passtimeTotal",passtimeTotal);
            map.put("stationnumTotal",stationnumTotal);
            map.put("divide",divide);
            map.put("stationcn",stationcn);
            mapList.add(map);
        }

        List<AvgPassVo.AvgPassCout> resultList = new ArrayList<>();
        //按照这个分吧，一个总共的，一个大碶+宝幢的，一个慈城+乔司+蜀山的，一个金华东+合肥东德
        //总共
        if(mapList.size() > 0){
            BigDecimal allpasstimeTotal = mapList.stream().filter(item -> avgPassDto.getStationcnList().contains(item.get("stationcn").toString())).map(item -> (BigDecimal) item.get("passtimeTotal")).reduce(BigDecimal::add).get();
            BigDecimal allstationnumTotal = mapList.stream().map(item -> (BigDecimal) item.get("stationnumTotal")).reduce(BigDecimal::add).get();
            AvgPassVo.AvgPassCout avgPassCout = new AvgPassVo.AvgPassCout();
            avgPassCout.setStationcn("总均");
            avgPassCout.setCount(allpasstimeTotal.divide(allstationnumTotal, 2, RoundingMode.DOWN));
            resultList.add(avgPassCout);
        }
//        List<Map<String, Object>> collect1 = mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "大碶") || StrUtil.equals((String) item.get("stationcn"), "宝幢")).collect(Collectors.toList());
//        if(collect1.size() > 0){
//            BigDecimal bzdqpasstimeTotal = mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "大碶") || StrUtil.equals((String) item.get("stationcn"), "宝幢")).map(item -> (BigDecimal) item.get("passtimeTotal")).reduce(BigDecimal::add).get();
//            BigDecimal bzdqstationnumTotal = mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "大碶") || StrUtil.equals((String) item.get("stationcn"), "宝幢")).map(item -> (BigDecimal) item.get("stationnumTotal")).reduce(BigDecimal::add).get();
//            AvgPassVo.AvgPassCout avgPassCout1 = new AvgPassVo.AvgPassCout();
//            avgPassCout1.setStationcn("大碶/宝幢平均");
//            avgPassCout1.setCount(bzdqpasstimeTotal.divide(bzdqstationnumTotal, 2, RoundingMode.DOWN));
//
//            BigDecimal bigDecimal1 = (BigDecimal) mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "大碶")).findFirst().orElse(new HashMap<String, Object>()).get("divide");
//            BigDecimal bigDecimal2 = (BigDecimal) mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "宝幢")).findFirst().orElse(new HashMap<String, Object>()).get("divide");
//
//            AvgPassVo.AvgPassCout avgPassCout2 = new AvgPassVo.AvgPassCout();
//            avgPassCout2.setStationcn("大碶平均");
//            avgPassCout2.setCount(bigDecimal1);
//
//            AvgPassVo.AvgPassCout avgPassCout3 = new AvgPassVo.AvgPassCout();
//            avgPassCout3.setStationcn("宝幢平均");
//            avgPassCout3.setCount(bigDecimal2);
//
//
//            resultList.add(avgPassCout1);
//            resultList.add(avgPassCout2);
//            resultList.add(avgPassCout3);
//        }
//
//        List<Map<String, Object>> collect2 = mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "慈城") || StrUtil.equals((String) item.get("stationcn"), "乔司") || StrUtil.equals((String) item.get("stationcn"), "蜀山")).collect(Collectors.toList());
//        if(collect2.size() > 0){
//            BigDecimal passtimeTotal1 = mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "慈城") || StrUtil.equals((String) item.get("stationcn"), "乔司") || StrUtil.equals((String) item.get("stationcn"), "蜀山")).map(item -> (BigDecimal) item.get("passtimeTotal")).reduce(BigDecimal::add).get();
//            BigDecimal stationnumTotal1 = mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "慈城") || StrUtil.equals((String) item.get("stationcn"), "乔司") || StrUtil.equals((String) item.get("stationcn"), "蜀山")).map(item -> (BigDecimal) item.get("stationnumTotal")).reduce(BigDecimal::add).get();
//            AvgPassVo.AvgPassCout avgPassCout1 = new AvgPassVo.AvgPassCout();
//            avgPassCout1.setStationcn("慈城/乔司/蜀山平均");
//            avgPassCout1.setCount(passtimeTotal1.divide(stationnumTotal1, 2, RoundingMode.DOWN));
//
//            BigDecimal bigDecimal1 = (BigDecimal) mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "慈城")).findFirst().orElse(new HashMap<String, Object>()).get("divide");
//            BigDecimal bigDecimal2 = (BigDecimal) mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "乔司")).findFirst().orElse(new HashMap<String, Object>()).get("divide");
//            BigDecimal bigDecimal3 = (BigDecimal) mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "蜀山")).findFirst().orElse(new HashMap<String, Object>()).get("divide");
//            AvgPassVo.AvgPassCout avgPassCout2 = new AvgPassVo.AvgPassCout();
//            avgPassCout2.setStationcn("慈城平均");
//            avgPassCout2.setCount(bigDecimal1);
//
//            AvgPassVo.AvgPassCout avgPassCout3 = new AvgPassVo.AvgPassCout();
//            avgPassCout3.setStationcn("乔司平均");
//            avgPassCout3.setCount(bigDecimal2);
//
//            AvgPassVo.AvgPassCout avgPassCout4 = new AvgPassVo.AvgPassCout();
//            avgPassCout4.setStationcn("蜀山平均");
//            avgPassCout4.setCount(bigDecimal3);
//
//            resultList.add(avgPassCout1);
//            resultList.add(avgPassCout2);
//            resultList.add(avgPassCout3);
//            resultList.add(avgPassCout4);
//        }
//        List<Map<String, Object>> collect3 = mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "金华东") || StrUtil.equals((String) item.get("stationcn"), "合肥东")).collect(Collectors.toList());
//        if(collect3.size() > 0){
//            BigDecimal passtimeTotal2 = mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "金华东") || StrUtil.equals((String) item.get("stationcn"), "合肥东")).map(item -> (BigDecimal) item.get("passtimeTotal")).reduce(BigDecimal::add).get();
//            BigDecimal stationnumTotal2 = mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "金华东") || StrUtil.equals((String) item.get("stationcn"), "合肥东")).map(item -> (BigDecimal) item.get("stationnumTotal")).reduce(BigDecimal::add).get();
//
//            AvgPassVo.AvgPassCout avgPassCout1 = new AvgPassVo.AvgPassCout();
//            avgPassCout1.setStationcn("金华东/合肥东平均");
//            avgPassCout1.setCount(passtimeTotal2.divide(stationnumTotal2, 2, RoundingMode.DOWN));
//
//            BigDecimal bigDecimal1 = (BigDecimal) mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "金华东")).findFirst().orElse(new HashMap<String, Object>()).get("divide");
//            BigDecimal bigDecimal2 = (BigDecimal) mapList.stream().filter(item -> StrUtil.equals((String) item.get("stationcn"), "合肥东")).findFirst().orElse(new HashMap<String, Object>()).get("divide");
//
//            AvgPassVo.AvgPassCout avgPassCout2 = new AvgPassVo.AvgPassCout();
//            avgPassCout2.setStationcn("金华东平均");
//            avgPassCout2.setCount(bigDecimal1);
//
//            AvgPassVo.AvgPassCout avgPassCout3 = new AvgPassVo.AvgPassCout();
//            avgPassCout3.setStationcn("合肥东平均");
//            avgPassCout3.setCount(bigDecimal2);
//
//            resultList.add(avgPassCout1);
//            resultList.add(avgPassCout2);
//            resultList.add(avgPassCout3);
//        }

        if(!list.isEmpty()){
            list.get(0).setStationAvgCount(resultList);
        }
        return R.success(list);
    }


}
