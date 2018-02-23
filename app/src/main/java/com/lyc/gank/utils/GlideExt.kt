package com.lyc.gank.utils

import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.lyc.gank.R

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
fun<T> RequestBuilder<T>.gankOption() =
        this.apply(RequestOptions()
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading))