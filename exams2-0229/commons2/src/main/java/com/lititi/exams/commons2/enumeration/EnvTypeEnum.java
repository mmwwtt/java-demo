package com.lititi.exams.commons2.enumeration;

import org.apache.commons.lang3.StringUtils;

public enum EnvTypeEnum {
    PRODUCTION(0, "production", "生产环境", "qss888.cn"), DEV(1, "dev", "测试环境", "testproductpage.forceplanet.com");

    private final int code;
    private final String envStr;
    private final String comment;
    private final String domain;

    EnvTypeEnum(int code, String envStr, String comment, String domain) {
        this.code = code;
        this.envStr = envStr;
        this.comment = comment;
        this.domain = domain;
    }

    public int getCode() {
        return code;
    }

    public String getEnvStr() {
        return envStr;
    }

    public String getComment() {
        return comment;
    }

    public String getDomain() {
        return domain;
    }

    public static EnvTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (EnvTypeEnum flag : EnvTypeEnum.values()) {
            if (flag.code == code) {
                return flag;
            }
        }
        return null;
    }

    public static EnvTypeEnum getByEnvStr(String envStr) {
        if (StringUtils.isBlank(envStr)) {
            return null;
        }
        for (EnvTypeEnum flag : EnvTypeEnum.values()) {
            if (flag.envStr.equals(envStr)) {
                return flag;
            }
        }
        return null;
    }


    public static boolean checkEnvBelongToDevOrProduction(String envStr) {
        return EnvTypeEnum.DEV.getEnvStr().equals(envStr) || EnvTypeEnum.PRODUCTION.getEnvStr().equals(envStr);
    }

}
