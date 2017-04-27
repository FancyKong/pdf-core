package com.cafa.pdf.core.commom.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 注册申请类别枚举
 * @author Cherish
 * @version 1.0
 * @date 2017/4/26 8:22
 */
@Getter
@AllArgsConstructor
public enum CheckEnum {
    AUTHOR(1, "著作者"),
    CUSTOMER(0, "会员"),
    UNKNOWN(-1, "未知");

    private Integer num;
    private String desc;

    public static String getDesc(Integer num) {
        CheckEnum e;
        switch (num) {
            case 1:
                e = AUTHOR;break;
            case 0:
                e = CUSTOMER;break;
            default:
                e = UNKNOWN;break;
        }
        return e.getDesc();
    }
}
