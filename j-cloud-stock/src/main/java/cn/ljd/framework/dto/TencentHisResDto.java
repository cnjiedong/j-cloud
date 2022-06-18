package cn.ljd.framework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class TencentHisResDto {
    String code;
    String msg;

    ResData data;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResData{
        private StockInfo stockInfo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StockInfo{
        private List<List<String>> qfqday;
    }




    /*@Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Qfqday{
        private List<List<String>> yzId;
        private Long lastAnimalId;
        private Integer sourceType;
    }*/
}
