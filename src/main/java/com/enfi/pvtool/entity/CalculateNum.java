package com.enfi.pvtool.entity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 计算累积放电值，累积充电值
 */
public class CalculateNum {

    class Const {
        /**
         * 累积充电
         */
        private float cumulativeCharge;

        /**
         * 累积放电
         */
        private float cumulativDischarge;

        private float bat;

        public float getCumulativeCharge() {
            return cumulativeCharge;
        }

        public void setCumulativeCharge(float cumulativeCharge) {
            this.cumulativeCharge = cumulativeCharge;
        }

        public float getCumulativDischarge() {
            return cumulativDischarge;
        }

        public void setCumulativDischarge(float cumulativDischarge) {
            this.cumulativDischarge = cumulativDischarge;
        }

        public float getBat() {
            return bat;
        }

        public void setBat(float bat) {
            this.bat = bat;
        }
    }

    float K1 = 1;
    float K2 = 1;
    float time = (float) 1 / (float) 60;
    Map<String, Const> map = new HashMap<>();

    //读取目录
    //打开文件 定义电池量 累积放电量 累积充电量
    //循环读取每行
    //取有功目标值3，样机可发值9
    //执行逻辑T
    public static void main(String[] args) {
        CalculateNum calculateNum = new CalculateNum();
        calculateNum.reloadFile();
    }

    private void reloadFile() {
        String inputPath = "D:\\temp1";
        File file = new File(inputPath);      //获取其file对象
        File[] fs = file.listFiles();     //遍历path下的文件和目录，放在File数组中
        for (File f : fs) {                //遍历File[]数组
            String fileName = f.getName();  //获取文件和目录名
            if (!f.isDirectory()) {  //另外可用fileName.endsWith("txt")来过滤出以txt结尾的文件
                Const c = parseFile(f);
                map.put(fileName, c);

            }
        }

        System.out.println("c = " + map);
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


                if (temp >= 500) {
                    if (battery > 0) {
                        t = battery / 500;
                        if (t > time) {
                            battery -= K1 * time * 500;
                            cumulativDischarge += K1 * time * 500;
                        } else {
                            battery -= K1 * t * 500;
                            cumulativDischarge += K1 * t * 500;
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
                } else if (temp >= -500) {
                    if(battery > 1000){
                        continue;
                    }
                    battery += K2 * time * temp;
                    if(battery - 1000 > 0){
                        cumulativeCharge += K2 * time * Math.abs(temp) - (battery - 1000 );
                    }else {
                        cumulativeCharge += K2 * time *  Math.abs(temp);
                    }

                } else {
                    if(battery > 1000){
                        continue;
                    }
                    battery += K2 * time * 500;
                    if(battery - 1000 > 0){
                        cumulativeCharge += K2 * time * 500 - (battery - 1000 );
                    }else {
                        cumulativeCharge += K2 * time * 500;
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
        c.setCumulativDischarge(cumulativDischarge);
        c.setCumulativeCharge(cumulativeCharge);
        c.setBat(c.bat);
        return c;
    }
}
