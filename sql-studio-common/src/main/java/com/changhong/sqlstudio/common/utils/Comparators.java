package com.changhong.sqlstudio.common.utils;

import java.util.Objects;

public class Comparators {

    /**
     * #brief: 比较模式常量 - 等于
     *
     * <p>用于表示比较模式为等于（Equal）的位掩码常量。
     * 适用于 {@code anycmp} 方法中的模式参数。
     */
    public static final int COMPARE_MODE_EQ = 0x000001;

    /**
     * #brief: 比较模式常量 - 小于等于
     *
     * <p>用于表示比较模式为小于等于（Less than or Equal）的位掩码常量。
     * 适用于 {@code anycmp} 方法中的模式参数。
     */
    public static final int COMPARE_MODE_LT = 0x000002;

    /**
     * #brief: 比较模式常量 - 大于
     *
     * <p>用于表示比较模式为大于（Greater than）的位掩码常量。
     * 适用于 {@code anycmp} 方法中的模式参数。
     */
    public static final int COMPARE_MODE_GT = 0x000004;

    /**
     * #brief: 比较模式常量 - 小于等于
     *
     * <p>用于表示比较模式为小于等于（Less than or Equal）的位掩码常量。
     * 该常量是 {@code COMPARE_MODE_LT} 和 {@code COMPARE_MODE_EQ} 的组合。
     * 适用于 {@code anycmp} 方法中的模式参数。
     */
    public static final int COMPARE_MODE_LE = COMPARE_MODE_LT | COMPARE_MODE_EQ;

    /**
     * #brief: 比较模式常量 - 大于等于
     *
     * <p>用于表示比较模式为大于等于（Greater than or Equal）的位掩码常量。
     * 该常量是 {@code COMPARE_MODE_GT} 和 {@code COMPARE_MODE_EQ} 的组合。
     * 适用于 {@code anycmp} 方法中的模式参数。
     */
    public static final int COMPARE_MODE_GE = COMPARE_MODE_GT | COMPARE_MODE_EQ;

    /**
     * #brief：对两个对象做 equals 操作比较<p>
     *
     * 如果两个对象相同则返回 {@code true}, 如果两个对象不相同则
     * 返回 {@code false}。
     *
     * @see Object#equals(Object)
     */
    public static boolean anyeq(Object x, Object y) {
        return Objects.equals(x, y);
    }

    /**
     * #brief：对两个对象做 not equals 操作比较<p>
     *
     * 如果两个对象相同则返回 {@code false}, 如果两个对象不相同则
     * 返回 {@code true}。
     *
     * @see #anyeq(Object, Object)
     */
    public static boolean anyne(Object x, Object y) {
        return !anyeq(x, y);
    }

    /**
     * 判断给定对象是否与任意其他对象相等。
     *
     * <p>该方法用于检查目标对象是否在提供的对象列表中。内部使用
     * {@link #anyeq(Object, Object)} 方法比较对象的相等性。<p>
     *
     * <h2>功能特点</h2>
     * <ul>
     *     <li>支持任意数量的比较对象。</li>
     *     <li>适用于基础类型和复杂对象的相等性判断。</li>
     * </ul>
     *
     * <h2>使用示例</h2>
     * <pre>
     *     boolean result = anyclude("test", "a", "b", "test", "c"); // 返回 true
     * </pre>
     *
     * @param a    目标对象
     * @param anys 要比较的一组对象
     * @return 如果目标对象与提供的任意一个对象相等，则返回 true；否则返回 false
     */
    public static boolean checkin(Object a, Object... anys) {
        for (Object b : anys) {
            if (anyeq(a, b))
                return true;
        }
        return false;
    }

    /**
     * #brief: 通用对象比较（默认等于模式）
     *
     * <p>比较两个可比较对象是否满足默认比较模式（等于）。
     * 此方法为简化调用，默认使用 {@code COMPARE_MODE_EQ} 模式。
     *
     * @param a 第一个比较对象
     * @param b 第二个比较对象
     * @return 当满足比较模式时返回 true
     */
    public static <T extends Comparable<T>> boolean anycmp(T a, T b) {
        return anycmp(a, b, COMPARE_MODE_EQ);
    }

    /**
     * #brief: 通用对象多模式比较
     *
     * <p>通过位掩码模式实现灵活的对象比较逻辑，支持多种比较模式的组合。
     * 支持的比较模式需通过 {@code COMPARE_MODE_*} 系列常量进行组合，
     * 需确保传入有效的模式组合参数。
     *
     * @param a 第一个比较对象
     * @param b 第二个比较对象
     * @param mode 位掩码组合的比较模式（使用 COMPARE_MODE_* 常量）
     * @return 当满足任一激活的比较模式时返回 true
     * @throws IllegalArgumentException 当模式参数不合法时抛出
     */
    public static <T extends Comparable<T>> boolean anycmp(T a, T b, int mode) {
        if ((mode & COMPARE_MODE_EQ) != 0) {
            if ((mode & COMPARE_MODE_LT) != 0)
                if (a.compareTo(b) <= 0)
                    return true;

            if ((mode & COMPARE_MODE_GT) != 0)
                if (a.compareTo(b) > 0)
                    return true;

            return a.compareTo(b) == 0;
        }

        if ((mode & COMPARE_MODE_LT) != 0)
            return a.compareTo(b) < 0;


        if ((mode & COMPARE_MODE_GT) != 0)
            return a.compareTo(b) > 0;

        throw new IllegalArgumentException("比较模式不正确");
    }

}
