package com.raed.drawingview

import android.util.Log
import com.kunkunnapps.stickermodule.DrawingAction
import kotlin.collections.ArrayList

class ActionStack {
    companion object {
        private const val TAG = "ActionStack"
        private val mMaxSize = Runtime.getRuntime().maxMemory() / 4
    }
    private var mCurrentSize: Long = 0
    private val mUndoStack: ArrayList<DrawingAction> = ArrayList()
    private val mRedoStack: ArrayList<DrawingAction> = ArrayList()

    fun addAction(action: DrawingAction) {
        Log.d(TAG, "Add getAction: $action")
        if (mRedoStack.size > 0) { //Clear the redo stack
            for (s in mRedoStack) mCurrentSize -= s.getSize()
            mRedoStack.clear()
        }
        addActionToStack(mUndoStack, action)
    }

    fun addActionToRedoStack(action: DrawingAction) {
        Log.d(TAG, "Add getAction to redo stack: $action")
        addActionToStack(mRedoStack, action)
    }

    fun addActionToUndoStack(action: DrawingAction) {
        Log.d(TAG, "Add getAction to undo stack: $action")
        addActionToStack(mUndoStack, action)
    }

    fun previous(): DrawingAction {
        return freeLastItem(mUndoStack)
    }

    operator fun next(): DrawingAction {
        return freeLastItem(mRedoStack)
    }

    val isRedoStackEmpty: Boolean
        get() = mRedoStack.size == 0

    val isUndoStackEmpty: Boolean
        get() = mUndoStack.size == 0

    private fun freeItem() {
        //I do not know weather it is necessary to do this or not, but please do not change it.
        if (mUndoStack.size >= mRedoStack.size) mCurrentSize -= mUndoStack.removeAt(0)
            .getSize() else mCurrentSize -= mRedoStack.removeAt(0).getSize()
    }

    private fun addActionToStack(
        stack: MutableList<DrawingAction>,
        action: DrawingAction
    ) {
        Log.d(TAG, "MaxSize = $mMaxSize")
        Log.d(TAG, "Before:CurSize = $mCurrentSize")
        Log.d(
            TAG,
            "Dr+mCSi = " + (mCurrentSize + action.getSize())
        )
        if (action.getSize() > mMaxSize) {
            //I hope this won't happen :)
            mUndoStack.clear()
            mRedoStack.clear()
            mCurrentSize = 0
            return
        }
        while (mCurrentSize + action.getSize() > mMaxSize) {
            freeItem()
        }
        stack.add(action)
        mCurrentSize += action.getSize()
        Log.d(TAG, "After:CurSize = $mCurrentSize")
    }

    private fun freeLastItem(list: ArrayList<DrawingAction>): DrawingAction {
        mCurrentSize -= list[list.size - 1].getSize()
        return list.removeAt(list.size - 1)
    }


}