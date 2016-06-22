package com.midsizemango.noteskotlin

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlin.properties.Delegates

/**
 * Created by Prasad S on 6/22/2016.
 */

class RecyclerListener(val mRecyclerView: RecyclerView)
{
    var mOnItemClickListener: OnItemClickListener by Delegates.notNull()
    var mOnItemLongClickListener: OnItemLongClickListener  by Delegates.notNull()
    var mAttachListener: RecyclerView.OnChildAttachStateChangeListener by Delegates.notNull()

    val mOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            if (true) {
                val holder = mRecyclerView.getChildViewHolder(view)
                mOnItemClickListener.onItemClicked(mRecyclerView, holder.adapterPosition, view)
            }
        }
    }

    val mOnLongClickListener: View.OnLongClickListener by lazy {
        View.OnLongClickListener { view ->
            if (true) {
                val holder = mRecyclerView.getChildViewHolder(view)
                val aux = mOnItemLongClickListener.onItemLongClicked(mRecyclerView, holder.adapterPosition, view)
                aux
            } else
                false
        }
    }

    init
    {
        mAttachListener = object: RecyclerView.OnChildAttachStateChangeListener
        {
            override fun onChildViewDetachedFromWindow(view: View?)
            {
                mOnClickListener
                mOnLongClickListener
                if (true)
                    view?.setOnClickListener(mOnClickListener)
                if (true)
                    view?.setOnLongClickListener(mOnLongClickListener)
            }

            override fun onChildViewAttachedToWindow(view: View?) { }
        }

        mRecyclerView.setTag(R.id.item_click_support, this)
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener)
    }

    fun  setOnItemClickListener(listener: OnItemClickListener) : RecyclerListener
    {
        mOnItemClickListener = listener
        return this
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) : RecyclerListener
    {
        mOnItemLongClickListener = listener
        return this
    }

    interface OnItemClickListener
    {
        fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View?);
    }

    interface OnItemLongClickListener
    {
        fun onItemLongClicked(recyclerView: RecyclerView, position: Int, v: View?) : Boolean
    }
}