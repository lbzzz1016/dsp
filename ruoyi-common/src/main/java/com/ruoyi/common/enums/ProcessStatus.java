package com.ruoyi.common.enums;

/**
 * 流程状态
 *
 * @author LBZ
 */
public enum ProcessStatus {
    INAPPROVAL("0", "审批中"), AGREE("1", "已同意"), RETURN("2", "退回"), REJECT("3", "驳回");

    private final String code;
    private final String info;

    ProcessStatus(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
