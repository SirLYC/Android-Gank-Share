package com.lyc.data.post

import com.lyc.data.BuildConfig
import com.lyc.data.api.Network
import com.lyc.data.resp.unwrap

/**
 * Created by Liu Yuchuan on 2018/2/23.
 */
class PostRepository {
    fun postArticle(type: String, link: String, desc: String, who: String)
            = Network.gankIoApi
            .add2gank(link, desc, type, "${BuildConfig.DEBUG}", who)
            .unwrap()
}
