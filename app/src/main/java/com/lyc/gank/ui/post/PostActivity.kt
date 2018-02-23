package com.lyc.gank.ui.post

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import com.lyc.data.types
import com.lyc.gank.R
import com.lyc.gank.utils.textPaste
import kotlinx.android.synthetic.main.activity_post.*

class PostActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        lv_post.setLabels(types)

        actionBar.setDisplayShowHomeEnabled(true)
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
                //todo : send
            }
        }
    }
}