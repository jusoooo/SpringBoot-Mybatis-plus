package top.jimxu.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.Constants;
import org.apache.axis.client.Service;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import top.jimxu.Util.DateUtilsExternal;
import top.jimxu.entity.domain.R;
import top.jimxu.entity.domain.SrAppointPoolDetail;
import top.jimxu.entity.domain.TrainCtnDetail;
import top.jimxu.entity.domain.TrainCtnDetailHistory;
import top.jimxu.mapper.SrAppointPoolDetailMapper;
import top.jimxu.mapper.TrainCtnDetailDao;
import top.jimxu.job.JobLogUtil;

import javax.xml.namespace.QName;
import org.apache.axis.client.Call;
import top.jimxu.mapper.TrainCtnDetailHistoryDao;

import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.encoding.XMLType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

@RequestMapping("/testBug")
@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {


    private final SrAppointPoolDetailMapper srAppointPoolDetailMapper;

    private final TrainCtnDetailDao trainCtnDetailDao;

    private final TrainCtnDetailHistoryDao trainCtnDetailHistoryDao;

    private final JmsTemplate jmsTemplate;

    @GetMapping("/getSrAppointPoolDetailById")
    public R getSrAppointPoolDetailById(@RequestParam String id){
        SrAppointPoolDetail srAppointPoolDetail = srAppointPoolDetailMapper.selectById(id);
        if (StrUtil.equals(srAppointPoolDetail.getIsCheck(),"1")){
            throw new RuntimeException("预约号状态不为核销，无法取消核销!");
        }
        return R.success(srAppointPoolDetail);
    }


    // 测试发送 Topic 消息
    @GetMapping("/test/topic")
    public String sendTestTopic() {
        // 模拟对方发送的 JSON 字符串，并带上 token
        String mockJson = "{\"orderId\":\"12345\",\"status\":\"PUSHED\",\"token\":\"76cfa4d25fb8b84fb6e396446d33a857\"}";

        jmsTemplate.convertAndSend("SRPM/PUSH/TIANMENG/FVqOT", mockJson, message -> {
            message.setStringProperty("token", "76cfa4d25fb8b84fb6e396446d33a857"); // 模拟带上 token
            return message;
        });
        return "Topic 测试消息已发送，请查看控制台日志！";
    }

    // 测试发送 Queue 消息
    @GetMapping("/test/queue")
    public String sendTestQueue() {
        jmsTemplate.convertAndSend("SRPM.QUEUE.TIANMENG.TASK", "这是一条测试队列消息");
        return "Queue 测试消息已发送，请查看控制台日志！";
    }


    // 镇司历史数据同步（2021-01-01 ~ 2022-12-31），由 xxl-job 调度，JobHandler 名称：autoGetZsInfoHisJob
    public void autoGetZsInfoHis() {
        // 21-01-01 到 22-12-31
        List<String> days = DateUtilsExternal.getDatesBetween("2021-01-01", "2022-12-31");
        JobLogUtil.info("autoGetZsInfoHis 开始执行，共需处理 " + days.size() + " 天(2021-01-01 ~ 2022-12-31)，数据将写入 TRAIN_CTN_DETAIL_HISTORY 表");
        int skipDays = 0;
        int totalInsert = 0;
        for (String param : days) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if(StrUtil.isBlank(param)){
                param = format.format(new Date());
            }
            // 以当前处理的日期 param 为基准，取其前一天、前三天
            // 注意：不能用 new Date()，否则历史补录时会拿到任务运行当天的日期，时间窗口全错
            Date paramDate;
            try {
                paramDate = DateUtil.parse(param, "yyyy-MM-dd");
            } catch (Exception e) {
                JobLogUtil.error("[" + param + "] 日期格式解析失败，跳过该日期: " + e.getMessage());
                skipDays++;
                continue;
            }
            String lastDate = format.format(DateUtils.addDays(paramDate, -1));
            String last3Date = format.format(DateUtils.addDays(paramDate, -3));

            //查询前一天镇司发送的散改机箱量
            List<TrainCtnDetailHistory> list = trainCtnDetailHistoryDao.selectList(new QueryWrapper<TrainCtnDetailHistory>().lambda().eq(TrainCtnDetailHistory::getTraindirect, "SEND")
                    .eq(TrainCtnDetailHistory::getTrainname, "HAITIESANGAIJI")
                    .eq(TrainCtnDetailHistory::getCtntype, "H")
                    .eq(TrainCtnDetailHistory::getCtnstatus, "F")
                    .eq(TrainCtnDetailHistory::getTrainplace, "ZHCT")
                    .eq(TrainCtnDetailHistory::getDz,"皋埠")
                    .between(TrainCtnDetailHistory::getLoadtime,lastDate.replace("-","") + "180000",param.replace("-","") + "180000" ));

            if(list.isEmpty()){
                log.error("未查询到散改集发送数据");
                JobLogUtil.error("[" + param + "] 未查询到镇司发送的散改集数据，跳过该日期，继续下一天");
                skipDays++;
                continue;
            }
            List<String> sendCtnNos = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                sendCtnNos.add(list.get(i).getCtnno());
            }
            JobLogUtil.info("[" + param + "] 查到镇司发送的散改集数据 " + list.size() + " 条，箱号: " + String.join(",", sendCtnNos) + "，查询装船时间窗口: " + lastDate + " 00:00:00 ~ " + param + " 00:00:00");

            String result = "";
            try {
                org.apache.axis.client.Service service = new org.apache.axis.client.Service();
                Call call = (Call) service.createCall();
                call.setTargetEndpointAddress("http://tbms.nbport.com.cn:9081/eportServ/services/XT_WebServices?wsdl");
                QName qName = new QName("http://xtwebserver.service.eport.com","getWrkTrnArvZH");
                call.setOperationName(qName);
                call.addParameter(new QName("http://xtwebserver.service.eport.com", "start_time"), Constants.XSD_STRING, ParameterMode.IN);
                call.addParameter(new QName("http://xtwebserver.service.eport.com", "end_time"), Constants.XSD_STRING, ParameterMode.IN);
                call.setReturnType(XMLType.XSD_STRING);
                result = (String) call.invoke(new Object[] { last3Date,param + " 18:00" });
                log.info("时间:{},{}获取颐博镇司到达数据为{}",param,"getWrkTrnArvZH",result);
                JobLogUtil.info("[" + param + "] 调用镇司接口 getWrkTrnArvZH, 入参: start_time=" + last3Date + ", end_time=" + param + " 18:00");
                JobLogUtil.info("[" + param + "] 调用镇司接口 getWrkTrnArvZH 返回: " + result);
            } catch (Exception e) {
                e.printStackTrace();
                JobLogUtil.error("[" + param + "] 调用镇司接口异常: " + e.getMessage(), e);
            }

            if(StrUtil.isNotBlank(result)){
                JSONObject obj = JSON.parseObject(result);
                if(StrUtil.isNotBlank(obj.getString("data"))){
                    JSONArray obja = JSON.parseArray(obj.getString("data"));
                    // 入库数据日志
                    List<TrainCtnDetailHistory> dumpTrainCtnDetail = new ArrayList<>();
                    for(int i = 0; i < list.size() ; i++){
                        JSONArray jsontemp = new JSONArray();
                        for(int j = 0; j < obja.size() ; j++){
                            JSONObject objj = obja.getJSONObject(j);
                            if(StrUtil.equals(list.get(i).getTrainno(), objj.getString("trn_no"))
                                    && compareDate2(objj.getString("snd_arv_tim"),list.get(i).getLoadtime()) == 1
                                    && compareDate2(objj.getString("snd_arv_tim"),"20251231180000") == 1){
                                jsontemp.add(objj);
                            }
                        }
                        if(!jsontemp.isEmpty()){
                            JSONObject objFinal;
                            if(jsontemp.size() > 1){
                                int maxnum = 0;
                                for (int k = 0; k < jsontemp.size() - 1; k++) {
                                    if(compareDate(jsontemp.getJSONObject(k).getString("snd_arv_tim"), jsontemp.getJSONObject(k + 1).getString("snd_arv_tim")) == 1){
                                        maxnum = k+1;
                                    }
                                }
                                objFinal = jsontemp.getJSONObject(maxnum);
                            }else{
                                objFinal = jsontemp.getJSONObject(0);
                            }

                            //准备入库
                            TrainCtnDetailHistory trainCtnDetail = new TrainCtnDetailHistory();
                            trainCtnDetail.setTraincode(list.get(i).getTraincode());
                            trainCtnDetail.setTrainname(list.get(i).getTrainname());
                            trainCtnDetail.setTrainvoyage(list.get(i).getTrainvoyage());
                            trainCtnDetail.setTraindirect("ARRIVE");
                            trainCtnDetail.setCtnowner(list.get(i).getCtnowner());
                            trainCtnDetail.setCtnstatus("E");
                            trainCtnDetail.setLoadport(list.get(i).getDesport());
                            trainCtnDetail.setDisport("CNNGB");
                            trainCtnDetail.setDesport("CNNGB");
                            trainCtnDetail.setCtnsizetype(list.get(i).getCtnsizetype());
                            trainCtnDetail.setCtnno(list.get(i).getCtnno());
                            trainCtnDetail.setTradenw("N");
                            trainCtnDetail.setDischargetime((objFinal.getString("snd_arv_tim") + ":00").replace("-","").replace(":","").replace(" ",""));
                            trainCtnDetail.setTrainno(list.get(i).getTrainno());
                            trainCtnDetail.setShipper(list.get(i).getConsigee());
                            trainCtnDetail.setConsigee(list.get(i).getShipper());
                            trainCtnDetail.setSpperson(list.get(i).getSpperson());
                            trainCtnDetail.setTrainplace(list.get(i).getTrainplace());
                            trainCtnDetail.setGoods("集装箱");
                            trainCtnDetail.setRemarks("镇司到达散改集用空箱历史");
                            trainCtnDetail.setPlantime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                            trainCtnDetail.setFz(list.get(i).getDz());
                            trainCtnDetail.setDz(list.get(i).getFz());
                            trainCtnDetail.setZxflag("Y");
                            trainCtnDetail.setCtntype("H");

                            //判断是否有重复项
                            List<TrainCtnDetailHistory> listCheck = trainCtnDetailHistoryDao.selectList(new QueryWrapper<TrainCtnDetailHistory>().lambda()
                                    .eq(TrainCtnDetailHistory::getCtnno, trainCtnDetail.getCtnno())
                                    .eq(TrainCtnDetailHistory::getDischargetime, trainCtnDetail.getDischargetime())
                                    .eq(TrainCtnDetailHistory::getTraindirect, "ARRIVE"));
                            List<TrainCtnDetailHistory> listCheck2 = trainCtnDetailHistoryDao.selectList(new QueryWrapper<TrainCtnDetailHistory>().lambda()
                                    .eq(TrainCtnDetailHistory::getCtnno, trainCtnDetail.getCtnno())
                                    .eq(TrainCtnDetailHistory::getLoadtime, trainCtnDetail.getDischargetime())
                                    .eq(TrainCtnDetailHistory::getTraindirect, "SEND"));


                            if(listCheck.isEmpty() && listCheck2.isEmpty()){
                                trainCtnDetail.setCtnid(nextHistoryCtnId());
                                trainCtnDetailHistoryDao.insert(trainCtnDetail);
                                dumpTrainCtnDetail.add(trainCtnDetail);
                                JobLogUtil.info("[" + param + "] 入库成功(TRAIN_CTN_DETAIL_HISTORY): 箱号=" + trainCtnDetail.getCtnno()
                                        + ", 到达时间=" + trainCtnDetail.getDischargetime()
                                        + ", 船名=" + trainCtnDetail.getTrainname()
                                        + ", 航线=" + trainCtnDetail.getTrainvoyage()
                                        + ", 目的港=" + trainCtnDetail.getDesport());
                            }else{
                                JobLogUtil.info("[" + param + "] 箱号=" + trainCtnDetail.getCtnno() + " 已存在到达/发送记录，跳过入库");
                            }

                        }

                    }
                    totalInsert = totalInsert + dumpTrainCtnDetail.size();
                    log.info("日期:{},调用颐博镇司到达数据入库{}条,具体数据:{}",param,dumpTrainCtnDetail.size(),JSON.toJSONString(dumpTrainCtnDetail));
                    JobLogUtil.info("[" + param + "] 本日入库汇总: " + dumpTrainCtnDetail.size() + " 条，明细: " + JSON.toJSONString(dumpTrainCtnDetail));
                }
            }else{
                log.error("调用颐博镇司到达数据异常:{}",DateUtil.now());
                JobLogUtil.error("[" + param + "] 镇司接口返回为空，本次无到达数据可处理");
            }
        }
        JobLogUtil.info("autoGetZsInfoHis 执行结束，本次累计入库 TRAIN_CTN_DETAIL_HISTORY " + totalInsert + " 条，跳过无数据日期 " + skipDays + " 天");
    }

    // 镇司数据同步（2023-01-01 ~ 2025-12-31），由 xxl-job 调度，JobHandler 名称：autoGetZsInfoJob
    public void autoGetZsInfo() {
        List<String> days = DateUtilsExternal.getDatesBetween("2023-12-24", "2025-12-31");
        JobLogUtil.info("autoGetZsInfo 开始执行，共需处理 " + days.size() + " 天(2023-01-01 ~ 2025-12-31)");
        int totalInsert = 0;
        int skipDays = 0;
        for (String param : days) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if(StrUtil.isBlank(param)){
                param = format.format(new Date());
            }
            // 以当前处理的日期 param 为基准，取其前一天、前三天
            // 注意：不能用 new Date()，否则历史补录时会拿到任务运行当天的日期，时间窗口全错
            Date paramDate;
            try {
                paramDate = DateUtil.parse(param, "yyyy-MM-dd");
            } catch (Exception e) {
                JobLogUtil.error("[" + param + "] 日期格式解析失败，跳过该日期: " + e.getMessage());
                skipDays++;
                continue;
            }
            String lastDate = format.format(DateUtils.addDays(paramDate, -1));
            String last3Date = format.format(DateUtils.addDays(paramDate, -3));

            //查询前一天镇司发送的散改机箱量
            List<TrainCtnDetail> list = trainCtnDetailDao.selectList(new QueryWrapper<TrainCtnDetail>().lambda().eq(TrainCtnDetail::getTraindirect, "SEND")
                    .eq(TrainCtnDetail::getTrainname, "HAITIESANGAIJI")
                    .eq(TrainCtnDetail::getCtntype, "H")
                    .eq(TrainCtnDetail::getCtnstatus, "F")
                    .eq(TrainCtnDetail::getTrainplace, "ZHCT")
                    .eq(TrainCtnDetail::getDz,"皋埠")
                    .between(TrainCtnDetail::getLoadtime,lastDate.replace("-","") + "180000",param.replace("-","") + "180000" ));

            if(list.isEmpty()){
                log.error("未查询到散改集发送数据");
                JobLogUtil.error("[" + param + "] 未查询到镇司发送的散改集数据，跳过该日期，继续下一天");
                skipDays++;
                continue;
            }
            List<String> sendCtnNos2 = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                sendCtnNos2.add(list.get(i).getCtnno());
            }
            JobLogUtil.info("[" + param + "] 查到镇司发送的散改集数据 " + list.size() + " 条，箱号: " + String.join(",", sendCtnNos2) + "，查询装船时间窗口: " + lastDate + " 00:00:00 ~ " + param + " 00:00:00");

            String result = "";
            try {
                org.apache.axis.client.Service service = new org.apache.axis.client.Service();
                Call call = (Call) service.createCall();
                call.setTargetEndpointAddress("http://tbms.nbport.com.cn:9081/eportServ/services/XT_WebServices?wsdl");
                QName qName = new QName("http://xtwebserver.service.eport.com","getWrkTrnArvZH");
                call.setOperationName(qName);
                call.addParameter(new QName("http://xtwebserver.service.eport.com", "start_time"), Constants.XSD_STRING, ParameterMode.IN);
                call.addParameter(new QName("http://xtwebserver.service.eport.com", "end_time"), Constants.XSD_STRING, ParameterMode.IN);
                call.setReturnType(XMLType.XSD_STRING);
                result = (String) call.invoke(new Object[] { last3Date,param + " 18:00" });
//                log.info("时间:{},{}获取颐博镇司到达数据为{}",param,"getWrkTrnArvZH",result);
                JobLogUtil.info("[" + param + "] 调用镇司接口 getWrkTrnArvZH, 入参: start_time=" + last3Date + ", end_time=" + param + " 18:00");
                JobLogUtil.info("[" + param + "] 调用镇司接口 getWrkTrnArvZH 返回: " + result);
            } catch (Exception e) {
                e.printStackTrace();
                JobLogUtil.error("[" + param + "] 调用镇司接口异常: " + e.getMessage(), e);
            }

            if(StrUtil.isNotBlank(result)){
                JSONObject obj = JSON.parseObject(result);
                if(StrUtil.isNotBlank(obj.getString("data"))){
                    JSONArray obja = JSON.parseArray(obj.getString("data"));
                    // 入库数据日志
                    List<TrainCtnDetail> dumpTrainCtnDetail = new ArrayList<>();
                    for(int i = 0; i < list.size() ; i++){
                        JSONArray jsontemp = new JSONArray();
                        for(int j = 0; j < obja.size() ; j++){
                            JSONObject objj = obja.getJSONObject(j);
                            if(StrUtil.equals(list.get(i).getTrainno(), objj.getString("trn_no"))
                                    && compareDate2(objj.getString("snd_arv_tim"),list.get(i).getLoadtime()) == 1
                                    && compareDate2(objj.getString("snd_arv_tim"),"20251231180000") == 1){
                                jsontemp.add(objj);
                            }
                        }
                        if(!jsontemp.isEmpty()){
                            JSONObject objFinal;
                            if(jsontemp.size() > 1){
                                int maxnum = 0;
                                for (int k = 0; k < jsontemp.size() - 1; k++) {
                                    if(compareDate(jsontemp.getJSONObject(k).getString("snd_arv_tim"), jsontemp.getJSONObject(k + 1).getString("snd_arv_tim")) == 1){
                                        maxnum = k+1;
                                    }
                                }
                                objFinal = jsontemp.getJSONObject(maxnum);
                            }else{
                                objFinal = jsontemp.getJSONObject(0);
                            }
                            //准备入库
                            TrainCtnDetail trainCtnDetail = new TrainCtnDetail();
                            trainCtnDetail.setTraincode(list.get(i).getTraincode());
                            trainCtnDetail.setTrainname(list.get(i).getTrainname());
                            trainCtnDetail.setTrainvoyage(list.get(i).getTrainvoyage());
                            trainCtnDetail.setTraindirect("ARRIVE");
                            trainCtnDetail.setCtnowner(list.get(i).getCtnowner());
                            trainCtnDetail.setCtnstatus("E");
                            trainCtnDetail.setLoadport(list.get(i).getDesport());
                            trainCtnDetail.setDisport("CNNGB");
                            trainCtnDetail.setDesport("CNNGB");
                            trainCtnDetail.setCtnsizetype(list.get(i).getCtnsizetype());
                            trainCtnDetail.setCtnno(list.get(i).getCtnno());
                            trainCtnDetail.setTradenw("N");
                            trainCtnDetail.setDischargetime((objFinal.getString("snd_arv_tim") + ":00").replace("-","").replace(":","").replaceAll("\\s", ""));
                            trainCtnDetail.setTrainno(list.get(i).getTrainno());
                            trainCtnDetail.setShipper(list.get(i).getConsigee());
                            trainCtnDetail.setConsigee(list.get(i).getShipper());
                            trainCtnDetail.setSpperson(list.get(i).getSpperson());
                            trainCtnDetail.setTrainplace(list.get(i).getTrainplace());
                            trainCtnDetail.setPortagent("船货代公司");
                            trainCtnDetail.setGoods("集装箱");
                            trainCtnDetail.setRemarks("镇司到达散改集用空箱1");
                            trainCtnDetail.setPlantime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                            trainCtnDetail.setFz(list.get(i).getDz());
                            trainCtnDetail.setDz(list.get(i).getFz());
                            trainCtnDetail.setZxflag("Y");
                            trainCtnDetail.setCtntype("H");

                            //判断是否有重复项
                            List<TrainCtnDetail> listCheck = trainCtnDetailDao.selectList(new QueryWrapper<TrainCtnDetail>().lambda()
                                    .eq(TrainCtnDetail::getCtnno, trainCtnDetail.getCtnno())
                                    .eq(TrainCtnDetail::getDischargetime, trainCtnDetail.getDischargetime())
                                    .eq(TrainCtnDetail::getTraindirect, "ARRIVE"));
                            List<TrainCtnDetail> listCheck2 = trainCtnDetailDao.selectList(new QueryWrapper<TrainCtnDetail>().lambda()
                                    .eq(TrainCtnDetail::getCtnno, trainCtnDetail.getCtnno())
                                    .eq(TrainCtnDetail::getLoadtime, trainCtnDetail.getDischargetime())
                                    .eq(TrainCtnDetail::getTraindirect, "SEND"));


                            if(listCheck.isEmpty() && listCheck2.isEmpty()){
                                trainCtnDetail.setCtnid(nextCtnId());
                                trainCtnDetailDao.insert(trainCtnDetail);
                                dumpTrainCtnDetail.add(trainCtnDetail);
                                JobLogUtil.info("[" + param + "] 入库成功: 箱号=" + trainCtnDetail.getCtnno()
                                        + ", 到达时间=" + trainCtnDetail.getDischargetime()
                                        + ", 船名=" + trainCtnDetail.getTrainname()
                                        + ", 航线=" + trainCtnDetail.getTrainvoyage());
                            }else{
                                JobLogUtil.info("[" + param + "] 箱号=" + trainCtnDetail.getCtnno() + " 已存在到达/发送记录，跳过入库");
                            }

                        }

                    }
                    totalInsert = totalInsert + dumpTrainCtnDetail.size();
                    log.info("日期:{},调用颐博镇司到达历史数据入库{}条,具体数据:{}",param,dumpTrainCtnDetail.size(),JSON.toJSONString(dumpTrainCtnDetail));
                    JobLogUtil.info("[" + param + "] 本日入库汇总: " + dumpTrainCtnDetail.size() + " 条，明细: " + JSON.toJSONString(dumpTrainCtnDetail));
                }
            }else{
                log.error("调用颐博镇司到达数据异常:{}",DateUtil.now());
                JobLogUtil.error("[" + param + "] 镇司接口返回为空，本次无到达数据可处理");
            }
        }
        JobLogUtil.info("autoGetZsInfo 执行结束，本次累计入库 " + totalInsert + " 条，跳过无数据日期 " + skipDays + " 天");
    }


    /**
     * 取 TRAIN_CTN_DETAIL 主键序列值；序列不可用时兜底用 UUID，保证定时任务不中断
     */
    private String nextCtnId() {
        try {
            String ctnId = trainCtnDetailDao.selectNextCtnId();
            if (StrUtil.isNotBlank(ctnId)) {
                return ctnId;
            }
        } catch (Exception e) {
            JobLogUtil.error("取序列 seq_train_ctn_detail.nextval 失败，改用 UUID 作为 ctnid: " + e.getMessage());
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 取 TRAIN_CTN_DETAIL_HISTORY 主键序列值；序列不可用时兜底用 UUID，保证定时任务不中断
     */
    private String nextHistoryCtnId() {
        try {
            String ctnId = trainCtnDetailHistoryDao.selectNextCtnId();
            if (StrUtil.isNotBlank(ctnId)) {
                return ctnId;
            }
        } catch (Exception e) {
            JobLogUtil.error("取序列 seq_train_ctn_detail.nextval 失败(历史表)，改用 UUID 作为 ctnid: " + e.getMessage());
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    private int compareDate(String date1 ,String date2){
        int i = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date d1 = simpleDateFormat.parse(date1);
            Date d2 = simpleDateFormat.parse(date2);
            if(d1.equals(d2)){
                i = 0 ;
            }else if(d1.before(d2)){
                i = 1 ;
            }else if(d1.after(d2)){
                i = 2 ;
            }
        }catch(ParseException e){
            i = 9;
        }
        return i;
    }

    private int compareDate2(String date1 ,String date2){
        int i = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date d1 = simpleDateFormat.parse(date1);
            Date d2 = simpleDateFormat2.parse(date2);
            if(d1.equals(d2)){
                i = 0 ;
            }else if(d1.before(d2)){
                i = 1 ;
            }else if(d1.after(d2)){
                i = 2 ;
            }
        }catch(ParseException e){
            i = 9;
        }
        return i;
    }

}
