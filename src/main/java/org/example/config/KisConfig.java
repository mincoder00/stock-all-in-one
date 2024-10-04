package org.example.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KisConfig {
    public static final String REST_BASE_URL = "https://openapi.koreainvestment.com:9443";
    public static final String WS_BASE_URL = "ws://ops.koreainvestment.com:21000";

    @Value("${kis.appkey}")
    private String APPKEY;

    @Value("${kis.appsecret}")
    private String APPSECRET;
    public static final String FHKUP03500100_PATH = "/uapi/domestic-stock/v1/quotations/inquire-daily-indexchartprice";
    public static final String FHKST03030100_PATH = "/uapi/overseas-price/v1/quotations/inquire-daily-chartprice";
}
