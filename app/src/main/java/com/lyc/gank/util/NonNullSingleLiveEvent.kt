package com.lyc.gank.util

import android.support.annotation.NonNull

/**
 * Created by hgj on 20/12/2017.
 */
class NonNullSingleLiveEvent<T>(initValue: T) : SingleLiveEvent<T>() {

    init {
        value = initValue
    }

    override fun setValue(@NonNull value: T?) {
        super.setValue(value!!) // nonnull
    }

    override fun postValue(value: T) {
        super.postValue(value!!) // nonnull
    }

    override fun getValue(): T {
        return super.getValue()!! // nonnull
    }
}
