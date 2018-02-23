package com.lyc.gank.ui.post

import android.arch.lifecycle.ViewModel
import com.lyc.data.post.PostRepository

/**
 * Created by Liu Yuchuan on 2018/2/23.
 */
class PostViewModel : ViewModel() {
    val postRepository = PostRepository()
}