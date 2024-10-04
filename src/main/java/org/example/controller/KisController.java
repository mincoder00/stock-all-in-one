package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.AccessTokenManager;
import org.example.config.KisConfig;
import org.example.model.Body;
import org.example.model.IndexData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple5;
import reactor.util.function.Tuples;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.jsoup.internal.StringUtil.isNumeric;

@Controller
public class KisController {
    @Autowired
    private AccessTokenManager accessTokenManager;

    @Autowired
    private KisConfig kisConfig;

    private final WebClient webClient;
    private String path;
    private String tr_id;

    public KisController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(KisConfig.REST_BASE_URL).build();
    }

    @GetMapping("/")
    public String index(Model model) {
        return "main";
    }

    @GetMapping("/indices")
    public String majorIndices(Model model) {

        List<Tuple2<String, String>> iscdsAndOtherVariable1 = Arrays.asList(
                Tuples.of("0001", "U"),
                Tuples.of("2001", "U"),
                Tuples.of("1001", "U"),
                Tuples.of("0002", "U"),
                Tuples.of("0003", "U"),
                Tuples.of("0004", "U"),
                Tuples.of("3003", "U"),
                Tuples.of("4001", "U"),
                Tuples.of("2008", "U"),
                Tuples.of("0163", "U"),
                Tuples.of("4600", "U"),
                Tuples.of("4411", "U"),
                Tuples.of("4421", "U"),
                Tuples.of("4422", "U"),
                Tuples.of("4427", "U"),
                Tuples.of("6028", "U"),
                Tuples.of("0005", "U"),
                Tuples.of("0008", "U"),
                Tuples.of("0009", "U"),
                Tuples.of("0021", "U"),
                Tuples.of("1009", "U"),
                Tuples.of("0025", "U"),
                Tuples.of("1043", "U"),
                Tuples.of("0021", "U"),
                Tuples.of("1003", "U")
        );
        List<Tuple5<String, String, String, String, String>> iscdsAndOtherVariable2 = Arrays.asList(
                Tuples.of(".DJI", "N", getYesterday(), getStringToday(), "D"),
                Tuples.of("COMP", "N", getYesterday(), getStringToday(), "D"),
                Tuples.of("SPX", "N", getYesterday(), getStringToday(), "D")
        );

        Flux<IndexData> korIndicesFlux = Flux.fromIterable(iscdsAndOtherVariable1)
                .concatMap(tuple2 -> getMajorIndex(tuple2.getT1(), tuple2.getT2()))
                .map(jsonData -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        return objectMapper.readValue(jsonData, IndexData.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
        Flux<IndexData> foreignIndicesFlux = Flux.fromIterable(iscdsAndOtherVariable2)
                .concatMap(tuple5 -> getOverseasIndex(tuple5.getT1(), tuple5.getT2(), tuple5.getT3(), tuple5.getT4(), tuple5.getT5()))
                .map(jsonData -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        return objectMapper.readValue(jsonData, IndexData.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
        List<IndexData> korIndicesList = korIndicesFlux.collectList().block();
        List<IndexData> foreignIndicesList = foreignIndicesFlux.collectList().block();
        model.addAttribute("indicesKor", korIndicesList);
        model.addAttribute("indicesFor", foreignIndicesList);

        model.addAttribute("jobDate", getJobDateTime());

        return "indices";
    }

    public String getStringToday() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return localDate.format(formatter);
    }

    public String getYesterday() {
        return LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public String getJobDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    public Mono<String> getMajorIndex(String iscd, String fid_cond_mrkt_div_code) {
        path = KisConfig.FHKUP03500100_PATH;
        tr_id = "FHKUP03500100";
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("fid_cond_mrkt_div_code", fid_cond_mrkt_div_code)
                        .queryParam("fid_input_iscd", iscd)
                        .queryParam("fid_input_date_1", getStringToday())
                        .queryParam("fid_input_date_2", getStringToday())
                        .queryParam("fid_period_div_code", "D")
                        .build())
                .header("content-type", "application/json")
                .header("authorization", "Bearer " + accessTokenManager.getAccessToken())
                .header("appkey", kisConfig.getAPPKEY())
                .header("appsecret", kisConfig.getAPPSECRET())
                .header("tr_id", tr_id)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(jsonData -> System.out.println("Received major index JSON: " + jsonData)); // 로그 추가

    }

    public Mono<String> getOverseasIndex(String iscd, String fid_cond_mrkt_div_code, String startDate, String endDate, String periodDivCode) {
        path = KisConfig.FHKST03030100_PATH;
        tr_id = "FHKST03030100";

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("fid_cond_mrkt_div_code", fid_cond_mrkt_div_code)
                        .queryParam("fid_input_date_1", startDate)
                        .queryParam("fid_input_date_2", endDate)
                        .queryParam("fid_input_iscd", iscd)
                        .queryParam("fid_period_div_code", periodDivCode)
                        .build())
                .header("content-type", "application/json")
                .header("authorization", "Bearer " + accessTokenManager.getAccessToken())
                .header("appkey", kisConfig.getAPPKEY())
                .header("appsecret", kisConfig.getAPPSECRET())
                .header("tr_id", tr_id)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(jsonData -> System.out.println("Received overseas index JSON: " + jsonData)); // 로그 추가
    }

    @GetMapping("/equities/{id}")
    public Mono<String> CurrentPrice(@PathVariable("id") String id, Model model) {
            if(isNumeric(id)) {
                String url = KisConfig.REST_BASE_URL + "/uapi/domestic-stock/v1/quotations/inquire-price?fid_cond_mrkt_div_code=J&fid_input_iscd=" + id;

                return webClient.get()
                        .uri(url)
                        .header("content-type", "application/json")
                        .header("authorization", "Bearer " + accessTokenManager.getAccessToken())
                        .header("appkey", kisConfig.getAPPKEY())
                        .header("appsecret", kisConfig.getAPPSECRET())
                        .header("tr_id", "FHKST01010100")
                        .retrieve()
                        .bodyToMono(Body.class)
                        .doOnSuccess(body -> {
                            model.addAttribute("equity", body.getOutput());
                            model.addAttribute("jobDate", getJobDateTime());
                        })
                        .doOnError(result -> System.out.println("*** error: " + result))
                        .thenReturn("equities");
            }
            else{
                List<Tuple5<String, String, String, String, String>> iscdsAndOtherVariable3 = Arrays.asList(
                        Tuples.of(id, "N", getYesterday(), getStringToday(), "D"));
                Flux<IndexData> foreignIndicesFlux = Flux.fromIterable(iscdsAndOtherVariable3)
                        .concatMap(tuple5 -> getOverseasIndex(tuple5.getT1(), tuple5.getT2(), tuple5.getT3(), tuple5.getT4(), tuple5.getT5()))
                        .map(jsonData -> {
                            ObjectMapper objectMapper = new ObjectMapper();
                            try {
                                return objectMapper.readValue(jsonData, IndexData.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        });
                List<IndexData> foreignIndicesList = foreignIndicesFlux.collectList().block();
                model.addAttribute("equity", foreignIndicesList);
                model.addAttribute("jobDate", getJobDateTime());
                return Mono.just("forEquities");
            }
    }
}