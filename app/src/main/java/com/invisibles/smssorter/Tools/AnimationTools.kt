package com.invisibles.smssorter.Tools

import android.animation.ValueAnimator
import android.view.View

class AnimationTools {

    companion object{

        fun simpleTransition(start: Int, end: Int, obj: View, duration: Long = 400, width: Boolean = false, height: Boolean = false): ValueAnimator {
            val anim = ValueAnimator.ofInt(start, end)
            anim.addUpdateListener {
                val value = it.animatedValue as Int
                val layoutParams = obj.layoutParams

                if (width) layoutParams.width = value
                else if (height) layoutParams.height = value

                obj.layoutParams = layoutParams
            }
            anim.duration = duration
            return anim
        }
    }



}