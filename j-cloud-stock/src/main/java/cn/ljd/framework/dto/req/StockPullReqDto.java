package cn.ljd.framework.dto.req;

import lombok.Data;


@Data
public class StockPullReqDto {
    String codes;

    String quoteDate;

    String pullType;

    Boolean forceRefresh = false;
}
