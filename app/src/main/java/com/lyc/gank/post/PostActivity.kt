package com.lyc.gank.post

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import com.lyc.data.types
import com.lyc.gank.R
import com.lyc.gank.utils.PostState
import com.lyc.gank.utils.provideViewModel
import com.lyc.gank.utils.textPaste
import com.lyc.gank.utils.toast
import kotlinx.android.synthetic.main.activity_post.*

class PostActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var postViewModel: PostViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        lv_post.setLabels(types)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fab_post.setOnClickListener(this)

        postViewModel = provideViewModel()

        postViewModel.postState.let {
            when (it.value) {
                is PostState.Posting -> fab_post.isEnabled = false
                is PostState.Success -> onPostSuccess()
                else -> fab_post.isEnabled = true
            }

            postViewModel.postState.observe(this, Observer {
                when (it) {
                    is PostState.Posting -> fab_post.isEnabled = false
                    is PostState.Success -> onPostSuccess()
                    else -> fab_post.isEnabled = true
                }
            })
        }
    }

    private fun onPostSuccess() {
        finish()
        toast(R.string.tip_post_success)
    }

    //setText when resume
    override fun onResume() {
        super.onResume()
        if (et_post_link.text.isEmpty()) {
            val link = textPaste() ?: return
            if (Patterns.WEB_URL.matcher(link).matches()) {
                et_post_link.setText(link)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> false
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_post -> {
                post()
            }
        }
    }

    private fun post() {
        postViewModel.post(lv_post.selectedLabel, et_post_link.text.toString().trim(),
                et_post_desc.text.toString().trim(), et_post_who.text.toString().trim())
    }
}