package com.lyc.gank.utils

import android.support.v7.util.ListUpdateCallback

/**
 * Created by Liu Yuchuan on 2018/2/16.
 */
class ObservableList<E>(private val realList: MutableList<E>): AbstractMutableList<E>() {
    override val size: Int
        get() = realList.size

    private val callbacks = mutableListOf<ListUpdateCallback>()

    fun addCallback(callback: ListUpdateCallback) = callbacks.add(callback)
    fun removeCallback(callback: ListUpdateCallback) = callbacks.remove(callback)


    override fun get(index: Int) = realList[index]

    override fun add(index: Int, element: E) {
        realList.add(index, element)
        callbacks.forEach { it.onInserted(index, 1) }
    }

    override fun removeAt(index: Int): E {
        val result = realList.removeAt(index)
        callbacks.forEach { it.onRemoved(index, 1) }
        return result
    }

    override fun set(index: Int, element: E): E {
        val result = realList.set(index, element)
        callbacks.forEach { it.onChanged(index, 1, element) }
        return result
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        val result = realList.addAll(index, elements)
        if(result){
            callbacks.forEach { it.onInserted(index, elements.size) }
        }
        return result
    }

    override fun addAll(elements: Collection<E>): Boolean{
        val addIndex = realList.size
        val result = realList.addAll(elements)
        if(result){
            callbacks.forEach { it.onInserted(addIndex, elements.size) }
        }
        return result
    }

    override fun removeRange(fromIndex: Int, toIndex: Int) {
        var index = fromIndex
        while(index < toIndex)
            realList.removeAt(index++)

        callbacks.forEach { it.onRemoved(fromIndex, toIndex - fromIndex) }
    }

    override fun clear() {
        val oldSize = realList.size
        realList.clear()
        callbacks.forEach { it.onRemoved(0, oldSize) }
    }
}