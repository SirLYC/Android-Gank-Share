package com.lyc.gank.utils

import android.support.annotation.CheckResult

/**
 * Created by hgj on 04/01/2018.
 *
 * RefreshState state transition diagram:
 *
 *                        +------------------------+
 *                        |      Empty (init)      |
 *                        +------------------------+
 *                             |
 *                             | search
 *                             v
 *                        +------------------------+  result(true)   +--------------+
 *                        |                        | --------------> |              |
 *    +------------------ |  RefreshingFromEmpty   |                 | RefreshEmpty |
 *    |                   |                        |  search        |              |
 *    |                   |                        | <-------------- |              |
 *    |                   +------------------------+                 +--------------+
 *    |                        |
 *    |                        | result(false)
 *    |                        v
 *    |                   +------------------------+
 *    |                   |        NotEmpty        |
 *    |                   +------------------------+
 *    |                        |              ^
 *    | error                  | search      | result(*)
 *    v                        v              |
 *  +--------+  error     +------------------------+
 *  |        | <--------- |                        |
 *  | Error  |            | RefreshingFromNotEmpty |
 *  |        |  search   |                        |
 *  |        | ---------> |                        |
 *  +--------+            +------------------------+
 *
 */
sealed class RefreshState {

    @CheckResult open fun refresh(): RefreshState? = null
    @CheckResult open fun result(isEmpty: Boolean): RefreshState? = null
    @CheckResult open fun error(msg: String): RefreshState? = null

    /**
     * init state: empty
     *
     * next state: [RefreshingFromEmpty]
     */
    object Empty : RefreshState() {
        override fun refresh() = RefreshingFromEmpty
    }

    /**
     * refreshing (abstract)
     *
     * previous state: [Empty], [RefreshEmpty], [NotEmpty], [Error]
     * next state: [RefreshEmpty], [NotEmpty], [Error]
     *
     * subclasses implement [result] method
     */
    abstract class Refreshing : RefreshState() {
        abstract override fun result(isEmpty: Boolean): RefreshState?
        override fun error(msg: String) = Error(msg)
    }

    /**
     * refreshing from empty data
     *
     * previous state: [Empty], [RefreshEmpty]
     * next state: [RefreshEmpty], [NotEmpty], [Error]
     */
    object RefreshingFromEmpty : Refreshing() {
        override fun result(isEmpty: Boolean) = if (isEmpty) RefreshEmpty else NotEmpty
    }

    /**
     * refreshing from not empty data
     *
     * previous state: [NotEmpty], [Error]
     * next state: [NotEmpty], [Error]
     */
    object RefreshingFromNotEmpty : Refreshing() {
        override fun result(isEmpty: Boolean) = NotEmpty
    }

    /**
     * empty data after refreshing
     *
     * previous state: [RefreshingFromEmpty]
     * next state: [RefreshingFromEmpty]
     */
    object RefreshEmpty : RefreshState() {
        override fun refresh() = RefreshingFromEmpty
    }

    /**
     * not empty data after refreshing
     *
     * previous state: [RefreshingFromEmpty], [RefreshingFromNotEmpty]
     * next state: [RefreshingFromNotEmpty]
     */
    object NotEmpty : RefreshState() {
        override fun refresh() = RefreshingFromNotEmpty
    }

    /**
     * error occurs after refreshing
     *
     * previous state: [NotEmpty], [Error]
     * next state: [RefreshingFromEmpty], [RefreshingFromNotEmpty]
     */
    class Error(val msg: String) : RefreshState() {
        override fun refresh() = RefreshingFromNotEmpty
    }
}
