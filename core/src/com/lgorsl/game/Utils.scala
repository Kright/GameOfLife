package com.lgorsl.game

import java.nio.{ByteBuffer, ByteOrder}

/**
  * Created by lgor on 03.04.2016.
  */
object Utils {

  def makeBuffer(array: Array[Float]) = {
    val fb = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
    fb.put(array)
    fb.position(0)
    fb
  }

  def clamp(value: Float, min: Float, max: Float) = if (value < min) min else if (value > max) max else value
}
