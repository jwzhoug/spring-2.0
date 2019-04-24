package com.jwzhoug.fremework.aop.config;

import lombok.Data;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-24
 */
@Data
public class GAopConfig {

    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
