package cn.ljd.framework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ResultDTO<T> implements Serializable {
    @JsonProperty(
            index = 10
    )
    private String code;
    @JsonProperty(
            index = 20
    )

    private String msg;
    @JsonProperty(
            index = 30
    )



    private T data;

    public static <T> ResultDTO ok() {
        ResultDTO dto = new ResultDTO();
        dto.code = "000000";
        dto.msg = "Success";
        return dto;
    }

    public static <T> ResultDTO ok(T data) {
        ResultDTO dto = new ResultDTO();
        dto.code = "000000";
        dto.msg = "Success";
        dto.data = data;
        return dto;
    }

    public static <T> ResultDTO ok(String code, String msg, T data) {
        ResultDTO dto = new ResultDTO();
        dto.code = code;
        dto.msg = msg;
        dto.data = data;
        return dto;
    }

    public static <T> ResultDTO error() {
        return error("500", "未知异常，请联系管理员");
    }

    public static <T> ResultDTO error(String msg) {
        return error("500", msg);
    }

    public static <T> ResultDTO error(String code, String msg) {
        ResultDTO dto = new ResultDTO();
        dto.code = code;
        dto.msg = msg;
        return dto;
    }
}
