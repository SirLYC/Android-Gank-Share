package com.lyc.gank.utils

import android.support.annotation.CheckResult
import com.lyc.gank.utils.LoadState.Empty.loadMore

/**
 * Created by hgj on 04/01/2018.
 *
 * LoadState state transition diagram:
 *
 *                                    +----------------+
 *                                    |  Empty (init)  |
 *                                    +----------------+
 *               result(true)            |         | result(false)
 *             +-------------------------+         |
 *             v                                   v
 *  +---------------+  result(true)   +----------------+            +-------------+
 *  |    HasMore    | <-------------- |     NoMore     |            |    Error    |
 *  +---------------+                 +----------------+            +-------------+
 *     |       ^                         ^                             ^       |
 *     |       | result(true)            | result(false)         error |       |
 *     |       |                         |                             |       |
 *     |       |                      +----------------+               |       |
 *     |       +--------------------- |                | --------------+       |
 *     |                              |    Loading     |                       |
 *     |         loadMore             |                |         loadMore      |
 *     +----------------------------> |                | <---------------------+
 *                                    +----------------+
 *
 */
sealed class LoadState {

    @CheckResult open fun loadMore(): LoadState? = null
    @CheckResult open fun result(hasMore: Boolean): LoadState? = null
    @CheckResult open fun error(msg: String): LoadState? = null

    /**
     * init state: empty stub, [loadMore] action is disabled
     *
     * next state: [HasMore] [NoMore]
     */
    object Empty : LoadState() {
        override fun result(hasMore: Boolean) = if (hasMore) HasMore else NoMore
    }

    /**
     * has more data to load after loading
     *
     * previous state: [Empty], [Loading]
     * next state: [Loading]
     */
    object HasMore : LoadState() {
        override fun loadMore() = Loading
    }

    /**
     * no more data to load after loading
     *
     * previous state: [Empty], [Loading]
     * no next state: [HasMore]
     */
    object NoMore : LoadState() {
        override fun result(hasMore: Boolean) = if (hasMore) HasMore else null
    }

    /**
     * loading
     *
     * previous state: [HasMore], [Error]
     * next state: [HasMore], [NoMore], [Error]
     */
    object Loading : LoadState() {
        override fun result(hasMore: Boolean) = if (hasMore) HasMore else NoMore
        override fun error(msg: String) = Error(msg)
    }

    /**
     * error occurs after loading
     *
     * previous state: [Loading]
     * next state: [Loading]
     */
    class Error(val msg: String) : LoadState() {
        override fun loadMore() = Loading
    }
}
