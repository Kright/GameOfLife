package com.lgorsl.game


import com.badlogic.gdx.graphics.Texture

/**
  * Created by lgor on 04.04.2016.
  */
trait CellularAutomaton {

  /**
   * shaders loading must be here
   */
  def init(): Unit

  def render(source: Texture): Unit

  def handleKeyboard(): Unit = {}
}


