package com.enfi.pvtool.process;

import com.alibaba.excel.EasyExcel;
import com.enfi.pvtool.entity.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 计算累积放电值，累积充电值
 */
@Component
public class CalculateNum {

    float K1 = 1;
    float K2 = 1;
    float time = (float) 1 / (float) 60;

    private final ConfigNum configNum;
    
    List<Const> result = new LinkedList<>();

    public CalculateNum(ConfigNum configNum) {
        this.configNum = configNum;
    }

    //读取目录
    //打开文件 定义电池量 累积放电量 累积充电量
    //循环读取每行
    //取有功目标值3，样机可发值9
    //执行逻辑T

    public void reloadFile() {
        String inputPath = "D:\\pv-tool";
        File file = new File(inputPath);      //获取其file对象
        File[] fs = file.listFiles();     //遍历path下的文件和目录，放在File数组中
        for (File f : fs) {                //遍历File[]数组
            if (!f.isDirectory()) {  //另外可用fileName.endsWith("txt")来过滤出以txt结尾的文件
                Const c = parseFile(f);
                result.add(c);
            }
        }
        String fileName =  inputPath + "\\结果" + ".xlsx";
        EasyExcel.write(fileName, Const.class).sheet("模板").doWrite(result);
    }

    private Const parseFile(File file) {
        BufferedReader reader = null;
        float cumulativeCharge = 0;
        float cumulativDischarge = 0;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            reader = new BufferedReader(isr);
            String line;
            float battery = 0;

            while ((line = reader.readLine()) != null) {
                float temp = 0;
                float t = 0;
                String[] split = line.split("\\t");
                try {
                    temp = Float.parseFloat(split[3]) - Float.parseFloat(split[9]);
                } catch (Exception e) {
                    continue;
                }


                if (temp >= configNum.getPowerNum()) {
                    if (battery > 0) {
                        t = battery / configNum.getPowerNum();
                        if (t > time) {
                            battery -= K1 * time * configNum.getPowerNum();
                            cumulativDischarge += K1 * time * configNum.getPowerNum();
                        } else {
                            battery -= K1 * t * configNum.getPowerNum();
                            cumulativDischarge += K1 * t * configNum.getPowerNum();
                        }

                    }
                } else if (temp >= 0) {
                    if (battery > 0) {
                        t = battery / temp;
                        if (t > time) {
                            battery -= K1 * time * temp;
                            cumulativDischarge += K1 * time * temp;
                        } else {
                            battery -= K1 * t * temp;
                            cumulativDischarge += K1 * t * temp;
                        }
                    }
                } else if (temp >= -configNum.getPowerNum()) {
                    if (battery < configNum.getBatteryCapacity()) {
                        t = Math.abs((configNum.getBatteryCapacity() - battery) / temp);
                        if (t > time) {
                            battery += K2 * time * Math.abs(temp);
                            cumulativeCharge += K2 * time * Math.abs(temp);
                        } else {
                            battery += K2 * time * Math.abs(temp);
                            cumulativeCharge += K2 * time * Math.abs(temp);
                        }
                    }
                } else {
                    if (battery < configNum.getBatteryCapacity()) {
                        t = Math.abs((configNum.getBatteryCapacity() - battery) / configNum.getPowerNum());
                        if (t > time) {
                            battery += K2 * time * configNum.getPowerNum();
                            cumulativeCharge += K2 * time * configNum.getPowerNum();
                        } else {
                            battery += K2 * t * configNum.getPowerNum();
                            cumulativeCharge += K2 * t * configNum.getPowerNum();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        Const c = new Const();
        c.setDate(parseStringToDateTime(file.getName().replace(".","-"),"yyyy-M-d"));
        c.setCumulativDischarge(cumulativDischarge);
        c.setCumulativeCharge(cumulativeCharge);
        return c;
    }

    public static Date parseStringToDateTime(String time, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return Date.from(LocalDate.parse(time, df).atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
    }
}
