package com.tech.mediaserver.enums;

import lombok.Getter;

/**
 * @author chudichen
 * @date 2018/9/27
 */
@Getter
public enum ResultEnum {

    /** 成功 */
    SUCCESS(200),
    /** 下发失败 */
    ISSUE_FAIL(201),
    /** 失败 */
    FAIL(202),
	/** 存在需要覆盖 */
	EXIST(203);

    private Integer code;

    ResultEnum(Integer code) {
        this.code = code;
    }
}
