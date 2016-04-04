package com.lgorsl.game

import java.util.regex.Pattern

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram

/**
  * Created by lgor on 03.04.2016.
  *
  * *.glsl files placed in android/assets folder
  */
object Resources {

  private val loadedShaders = new scala.collection.mutable.HashMap[String, ShaderProgram]()

  def getShader(name: String): ShaderProgram = {
    if (!loadedShaders.contains(name)){
      val codeParts = Gdx.files.internal(name + ".glsl").readString().split(Pattern.quote("[FRAGMENT]"))
      assert(codeParts.length == 2)
      val shader = new ShaderProgram(codeParts(0), codeParts(1))
      assert(shader.isCompiled, shader.getLog)
      loadedShaders.put(name, shader)
    }

    loadedShaders(name)
  }

}
