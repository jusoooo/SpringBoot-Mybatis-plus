package top.jimxu.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import top.jimxu.entity.domain.R;
import top.jimxu.entity.domain.SrAppointPoolDetail;
import top.jimxu.entity.domain.TrainCtnDetail;
import top.jimxu.mapper.SrAppointPoolDetailMapper;
import top.jimxu.mapper.TrainCtnDetailDao;

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


    // 测试定时autoGetZsInfo
    @GetMapping("/autoGetZsInfo")
    public R autoGetZsInfo(String param) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if(StrUtil.isBlank(param)){
            param = format.format(new Date());
        }
        Date date = DateUtils.addDays(new Date(), -1);
        String lastDate = format.format(date);
        Date date3 = DateUtils.addDays(new Date(), -3);
        String last3Date = format.format(date3);

        //查询前一天镇司发送的散改机箱量
        List<TrainCtnDetail> list = trainCtnDetailDao.selectList(new QueryWrapper<TrainCtnDetail>().lambda().eq(TrainCtnDetail::getTraindirect, "SEND")
                .eq(TrainCtnDetail::getTrainname, "HAITIESANGAIJI")
                .eq(TrainCtnDetail::getCtntype, "H")
                .eq(TrainCtnDetail::getCtnstatus, "F")
                .eq(TrainCtnDetail::getTrainno,"5349069")
                .eq(TrainCtnDetail::getTrainplace, "ZHCT")
                .between(TrainCtnDetail::getLoadtime,lastDate.replace("-","") + "000000",param.replace("-","") + "000000" ));

        if(list.isEmpty()){
            log.error("未查询到散改集发送数据");
            return R.error();
        }

        String result = "{\"data\":[\n" +
                "{\"mar_no\":\"48653\", \"snd_arv_tim\":\"2026-06-17 07:13\", \"seq_no\":\"24\", \"trn_no\":\"5349069\", \"trn_typ\":\"X70\", \"oil_typ\":\"\", \"slf_wgt\":\"22.400000000000002\", \"cnv_len\":\"1.2\", \"lod_wgt\":\"5\", \"dep_sat\":\"XBU\", \"dep_sat_nam\":\"新昌北\", \"arv_sat\":\"MCH\", \"arv_sat_nam\":\"煤场\", \"cag_nam\":\"敞二空2\", \"notes\":\"部/局令93016号;敞顶箱;循环班列\", \"paulin\":\"\", \"snd_per_des\":\"\", \"rcv_per_des\":\"宁波港铁路有限公司\"}," +
                "]}";

        if(StrUtil.isNotBlank(result)){
            JSONObject obj = JSON.parseObject(result);
            if(StrUtil.isNotBlank(obj.getString("data"))){
                JSONArray obja = JSON.parseArray(obj.getString("data"));
                // 入库数据日志
                List<TrainCtnDetail> dumpTrainCtnDetail = new ArrayList<>();
                for(int i = 0; i < list.size() ; i++){
                    JSONArray jsontemp = new JSONArray();
                    // 寻找匹配的到空数据
                    for(int j = 0; j < obja.size() ; j++){
                        JSONObject objj = obja.getJSONObject(j);
                        if(StrUtil.equals(list.get(i).getTrainno(), objj.getString("trn_no")) && compareDate2(objj.getString("snd_arv_tim"),list.get(i).getLoadtime()) == 1){
                            //车号相同，且在装车时间之前
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
                        //BeanUtils.copyProperties(list.get(i), trainCtnDetail);
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
                        trainCtnDetail.setRemarks("镇司到达散改集用空箱");
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
//                            trainCtnDetailDao.insert(trainCtnDetail);
                            dumpTrainCtnDetail.add(trainCtnDetail);
                        }

                    }

                }
                log.info("调用颐博镇司到达数据入库{}条,具体数据:{}",dumpTrainCtnDetail.size(),JSON.toJSONString(dumpTrainCtnDetail));
            }
        }else{
           log.error("调用颐博镇司到达数据异常:{}",DateUtil.now());
        }
        return R.success();
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
