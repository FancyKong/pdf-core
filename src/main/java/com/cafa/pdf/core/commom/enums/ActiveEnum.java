package com.cafa.pdf.core.commom.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否激活枚举
 * @author Cherish
 * @version 1.0
 * @date 2017/4/14 15:23
 */
@Getter
@AllArgsConstructor
public enum ActiveEnum {
    ACTIVE(1, "激活"),
    NONE(0, "冻结"),
    UNKNOWN(-1, "未知");

    private Integer num;
    private String desc;

    public static String getDesc(Integer num) {
        ActiveEnum e;
        switch (num) {
            case 1:
                e = ACTIVE;break;
            case 0:
                e = NONE;break;
            default:
                e = UNKNOWN;break;
        }
        return e.getDesc();
    }
}
