package com.lyc.gank.utils

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.NonNull

/**
 * Created by hgj on 16/01/2018.
 */
open class NonNullLiveData<T>(initValue: T) : MutableLiveData<T>() {

    init {
        value = initValue
    }

    override fun getValue(): T {
        return super.getValue()!! // nonnull
    }

    override fun setValue(@NonNull value: T) {
        super.setValue(value!!) // nonnull
    }

    override fun postValue(@NonNull value: T) {
        super.postValue(value!!) // nonnull
    }
}
