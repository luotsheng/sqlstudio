package com.changhong.sqlstudio.common.tuple;

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

import static com.changhong.sqlstudio.common.utils.Comparators.anyeq;

/**
 * `Pair` 是一个泛型类，用于表示一对具有不同类型的元素。
 *
 * <p>此类的主要功能是存储和访问一对值，其中每个值可以是任意类型。通过 `first()` 和 `second()` 方法，可以分别获取该对值的第一个和第二个元素。
 *
 * <p>该类支持 `hashCode` 和 `equals` 方法，确保能够根据封装的 `Tuple` 对象进行比较和哈希操作。<p>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>该类使用 `Tuple` 内部类来存储元素，因此 `Tuple` 的功能和行为直接影响 `Pair` 的行为。</li>
 *     <li>比较和哈希操作依赖于封装的 `Tuple` 实例。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     Pair<String, Integer> pair = new Pair<>("Hello", 42);
 *     String first = pair.first(); // 获取 "Hello"
 *     Integer second = pair.second(); // 获取 42
 * </pre>
 *
 * @param <A> 第一个元素的类型
 * @param <B> 第二个元素的类型
 * @author Luo Tiansheng
 */
@SuppressWarnings("ClassCanBeRecord")
public class Pair<A, B>
{

        /**
         * 第一个元素
         */
        private final A first;

        /**
         * 第二个元素
         */
        private final B second;

        /**
         * 构造一个 `Pair` 实例，封装两个指定的元素。
         *
         * @param first  作为第一个元素的值
         * @param second 作为第二个元素的值
         */
        public Pair(A first, B second)
        {
                this.first = first;
                this.second = second;
        }

        public static <A, B> Pair<A, B> of(A first, B second)
        {
                return new Pair<>(first, second);
        }

        /**
         * 获取 `Pair` 中的第一个元素。
         *
         * @return `Pair` 中的第一个元素
         */
        public A first()
        {
                return first;
        }

        /**
         * 获取 `Pair` 中的第二个元素。
         *
         * @return `Pair` 中的第二个元素
         */
        public B second()
        {
                return second;
        }

        /**
         * 计算 `Pair` 对象的哈希值，基于封装的 `Tuple` 实例。
         *
         * @return `Pair` 的哈希值
         */
        @Override
        public int hashCode()
        {
                return super.hashCode();
        }

        /**
         * 判断当前 `Pair` 是否与指定的另一个 `Pair` 相等。
         * 通过比较两个 `Pair` 中封装的 `Tuple` 实例来判断。
         *
         * @param pair 要比较的 `Pair` 对象
         * @return 如果两个 `Pair` 相等，返回 `true`，否则返回 `false`
         */
        public boolean equals(Pair<A, B> pair)
        {
                return anyeq(first, pair.first) && anyeq(second, pair.second);
        }

}
