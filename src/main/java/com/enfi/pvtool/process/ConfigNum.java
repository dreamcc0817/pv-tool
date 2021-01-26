package com.enfi.pvtool.process;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
@Configuration
public class ConfigNum  {

    private int powerNum;
    private int batteryCapacity;

    public int getPowerNum() {
        return powerNum;
    }

    @Value("${powerNum}")
    public void setPowerNum(String powerNum) {
        this.powerNum = Integer.parseInt(powerNum);
    }

    public int getBatteryCapacity() {
        return batteryCapacity;
    }
    @Value("${batteryCapacity}")
    public void setBatteryCapacity(String batteryCapacity) {
        this.batteryCapacity = Integer.parseInt(batteryCapacity);
    }
}
