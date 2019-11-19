package com.tech.mediaserver.enums;

import lombok.Getter;
import org.apache.commons.codec.binary.StringUtils;

/**
 * @author chudichen
 * @date 2018/10/13
 */
@Getter
public enum  StateEnum {
    /** 节目正常 */
    SUCCESS(100, "节目正常"),
    /** 节目中断 */
    PROCESSING(101, "节目中断"),
    /** 未下发 */
    DEFAULT(105, "未下发"),
    /** 已下发 */
    ISSUE(106, "已下发");

    private Integer code;

    private String desc;

    StateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static StateEnum getByCode(Integer code) {
        for (StateEnum stateEnum : StateEnum.values()) {
            if (stateEnum.getCode().equals(code)) {
                return stateEnum;
            }
        }
        return StateEnum.DEFAULT;
    }

    public static StateEnum getByDesc(String desc) {
        for (StateEnum stateEnum : StateEnum.values()) {
            if (StringUtils.equals(stateEnum.getDesc(), desc)) {
                return stateEnum;
            }
        }
        return StateEnum.DEFAULT;
    }
}
