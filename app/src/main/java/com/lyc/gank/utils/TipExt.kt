package com.lyc.gank.utils

import android.app.Activity
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast
import com.lyc.gank.base.BaseFragment

/**
 * Created by Liu Yuchuan on 2018/2/18.
 */
fun BaseFragment.toast(str: String){
    Toast.makeText(activity(), str, Toast.LENGTH_SHORT).show()
}

fun BaseFragment.toast(@StringRes strRes: Int){
    Toast.makeText(activity(), strRes, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(str: String){
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(@StringRes strRes: Int){
    Toast.makeText(this, strRes, Toast.LENGTH_SHORT).show()
}


fun BaseFragment.longToast(str: String){
    Toast.makeText(activity(), str, Toast.LENGTH_LONG).show()
}

fun BaseFragment.longToast(@StringRes strRes: Int){
    Toast.makeText(activity(), strRes, Toast.LENGTH_LONG).show()
}

fun Activity.longToast(str: String){
    Toast.makeText(this, str, Toast.LENGTH_LONG).show()
}

fun Activity.longToast(@StringRes strRes: Int){
    Toast.makeText(this, strRes, Toast.LENGTH_LONG).show()
}


fun View.snackBar(str: String, actionText: String, action: View.OnClickListener){
    Snackbar.make(this, str, Snackbar.LENGTH_SHORT)
            .setAction(actionText, action)
            .show()
}

fun View.snackBar(@StringRes strRes: Int, @StringRes actionTextRes: Int, action: View.OnClickListener){
    Snackbar.make(this, strRes, Snackbar.LENGTH_SHORT)
            .setAction(actionTextRes, action)
            .show()
}

fun View.longSnackBar(str: String, actionText: String, action: View.OnClickListener){
    Snackbar.make(this, str, Snackbar.LENGTH_LONG)
            .setAction(actionText, action)
            .show()
}

fun View.longSnackBar(@StringRes strRes: Int, @StringRes actionTextRes: Int, action: View.OnClickListener){
    Snackbar.make(this, strRes, Snackbar.LENGTH_LONG)
            .setAction(actionTextRes, action)
            .show()
}

fun View.indefSnackBar(str: String, actionText: String, action: View.OnClickListener){
    Snackbar.make(this, str, Snackbar.LENGTH_INDEFINITE)
            .setAction(actionText, action)
            .show()
}

fun View.indefSnackBar(@StringRes strRes: Int, @StringRes actionTextRes: Int, action: View.OnClickListener){
    Snackbar.make(this, strRes, Snackbar.LENGTH_INDEFINITE)
            .setAction(actionTextRes, action)
            .show()
}