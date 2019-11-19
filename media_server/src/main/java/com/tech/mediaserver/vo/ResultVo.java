package com.tech.mediaserver.vo;

import lombok.Data;

/**
 * @author chudichen
 * @date 2018/9/27
 */
@Data
public class ResultVo {
    private Integer code;
    private String message;

    public ResultVo(Integer code) {
        this.code = code;
    }

    public ResultVo(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
