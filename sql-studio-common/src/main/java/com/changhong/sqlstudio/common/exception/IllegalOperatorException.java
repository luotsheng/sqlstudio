package com.changhong.sqlstudio.common.exception;

/* -------------------------------------------------------------------------------- *\
|*                                                                                  *|
|*    Copyright (C) 2019-2024 RedGogh All rights reserved.                          *|
|*                                                                                  *|
|*    Licensed under the Apache License, Version 2.0 (the "License");               *|
|*    you may not use this file except in compliance with the License.              *|
|*    You may obtain a copy of the License at                                       *|
|*                                                                                  *|
|*        http://www.apache.org/licenses/LICENSE-2.0                                *|
|*                                                                                  *|
|*    Unless required by applicable law or agreed to in writing, software           *|
|*    distributed under the License is distributed on an "AS IS" BASIS,             *|
|*    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.      *|
|*    See the License for the specific language governing permissions and           *|
|*    limitations under the License.                                                *|
|*                                                                                  *|
\* -------------------------------------------------------------------------------- */

/**
 * 自定义异常，表示非法操作异常。
 * <p>
 * 该异常用于表示在程序执行过程中遇到不合法的操作时抛出，例如
 * 用户尝试执行不被允许的操作或违反了操作的规则。
 * </p>
 */
public class IllegalOperatorException extends SystemRuntimeException {

    /**
     * 默认构造函数
     */
    public IllegalOperatorException() {
    }

    /**
     * 使用指定的异常作为原因构造一个新的 IllegalOperatorException。
     *
     * @param e 异常原因
     */
    public IllegalOperatorException(Throwable e) {
        super(e);
    }

    /**
     * 使用指定的格式化信息和参数构造一个新的 IllegalOperatorException。
     *
     * @param fmt  格式化字符串
     * @param args 格式化参数
     */
    public IllegalOperatorException(String fmt, Object... args) {
        super(fmt, args);
    }

    /**
     * 使用指定的格式化信息、异常原因和参数构造一个新的 IllegalOperatorException。
     *
     * @param fmt  格式化字符串
     * @param e    异常原因
     * @param args 格式化参数
     */
    public IllegalOperatorException(String fmt, Throwable e, Object... args) {
        super(fmt, e, args);
    }

}

