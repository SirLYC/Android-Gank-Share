package com.lyc.gank.ui.post

import android.arch.lifecycle.ViewModel
import com.lyc.data.post.PostRepository
import com.lyc.data.resp.async
import com.lyc.gank.utils.NetworkStateReceiver
import com.lyc.gank.utils.NonNullSingleLiveEvent
import com.lyc.gank.utils.PostState
import com.lyc.gank.utils.loge
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Liu Yuchuan on 2018/2/23.
 */

class PostViewModel : ViewModel() {
    companion object {
        private const val TAG = "Post"
    }

    private val postRepository = PostRepository()

    val postState = NonNullSingleLiveEvent<PostState>(PostState.NoAction)

    private val compositeDisposable = CompositeDisposable()

    fun post(type: String, link: String, desc: String, who: String) {
        postState.value.post()?.let { nextState ->
            if (NetworkStateReceiver.isNetWorkConnected()) {
                postState.value = nextState
                doPost(type, link, desc, who)
            } else {
                postState.value = PostState.Error("没有网络连接")
            }
        }
    }

    private fun doPost(type: String, link: String, desc: String, who: String) {
        if (link.isEmpty()) {
            postState.value.error("链接不能为空")?.let(postState::setValue)
            return
        }
        if (desc.isEmpty()) {
            postState.value.error("描述不能为空")?.let(postState::setValue)
            return
        }
        if (desc.isEmpty()) {
            postState.value.error("分享人不能为空")?.let(postState::setValue)
            return
        }
        postRepository.postArticle(type, link, desc, who)
                .async()
                .subscribe({
                    if (it.error) {
                        postState.value.error(it.msg)?.let(postState::setValue)
                    } else {
                        postState.value.result()?.let(postState::setValue)
                    }
                }, {
                    loge(TAG, "$type\n$link\n$desc\n$who", it)
                    postState.value.error("未知错误")?.let(postState::setValue)
                })
                .also {
                    compositeDisposable.add(it)
                }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}