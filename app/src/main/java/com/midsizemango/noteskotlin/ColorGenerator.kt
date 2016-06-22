package com.midsizemango.noteskotlin

import java.util.Arrays
import java.util.Random

/**
 * Created by Prasad S on 1/10/2016.
 */

class ColorGenerator private constructor(private val mColors: List<Int>) {
    private val mRandom: Random

    init {
        mRandom = Random(System.currentTimeMillis())
    }

    val randomColor: Int
        get() = mColors[mRandom.nextInt(mColors.size)]

    companion object {

        var MATERIAL: ColorGenerator

        init {
            MATERIAL = create(Arrays.asList(
                    0xff00bcd4.toInt(),
                    0xff009688.toInt(),
                    0xff795548.toInt(),
                    0xff455a64.toInt(),
                    0xffe57b72.toInt(),
                    0xff8d2345.toInt(),
                    0xff607d8b.toInt(),
                    0xffff7043.toInt(),
                    0xffff4081.toInt(),
                    0xffe91e63.toInt(),
                    0xffffeb3b.toInt(),
                    0xfff5be25.toInt(),
                    0xff359ff2.toInt(),
                    0xff5677fc.toInt(),
                    0xd44e6cef.toInt(),
                    0xff263238.toInt(),
                    0xff009688.toInt(),
                    0xff029ae4.toInt(),
                    0xff009e29.toInt(),
                    0xff33b679.toInt()))
        }

        fun create(colorList: List<Int>): ColorGenerator {
            return ColorGenerator(colorList)
        }
    }
}