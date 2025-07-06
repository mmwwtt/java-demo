package com.lititi.exams.commons2.enumeration;

/**
 * 表示是否的状态
 * 
 * @company 杭州利提提
 * @author LuoJiang
 * @version WhetherFlag.java, v 0.1 2019年2月16日 下午2:15:58
 */
public enum WhetherFlag {

    NO(0, "否"), YES(1, "是");

    private final int code;
    private final String comment;

    WhetherFlag(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    public static WhetherFlag getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (WhetherFlag whetherFlag : WhetherFlag.values()) {
            if (code.equals(whetherFlag.getCode())) {
                return whetherFlag;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getComment() {
        return comment;
    }

    public static WhetherFlag getWhetherFlagByBoolean(Boolean flag) {
        if (flag != null && flag) {
            return WhetherFlag.YES;
        } else {
            return WhetherFlag.NO;
        }
    }

    public static Boolean getBooleanByWhetherFlagCode(Integer whetherFlagCode) {
        WhetherFlag whetherFlag = WhetherFlag.getByCode(whetherFlagCode);
        if (WhetherFlag.YES.equals(whetherFlag)) {
            return true;
        }
        return false;
    }

}
