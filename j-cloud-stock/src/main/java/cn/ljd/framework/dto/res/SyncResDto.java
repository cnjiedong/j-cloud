package cn.ljd.framework.dto.res;

import lombok.Data;

@Data
public class SyncResDto {
    Integer insert = 0;
    Integer update = 0;
    Integer delete = 0;
}
