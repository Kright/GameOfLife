package com.lgorsl.game

import java.nio.FloatBuffer

import com.badlogic.gdx.{Gdx, Input}
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.{GL20, Texture}

/**
  * Created by lgor on 04.04.2016.
  */
class IsingModel(val vertexData: FloatBuffer, val size: Int) extends CellularAutomaton {

  private var shader: ShaderProgram = null

  private var betta = 1.76f // betta == 1 / temperature
  private val factor = 1.003f

  override def init(): Unit = {
    shader = Resources.getShader("ising")
  }

  override def render(source: Texture): Unit = {
    val gl = Gdx.gl20
    shader.begin()

    gl.glUniform1f(shader.getUniformLocation("uBetta"), betta)
    gl.glUniform4f(shader.getUniformLocation("uBonus"), math.random.toFloat, math.random.toFloat, math.random.toFloat, math.random.toFloat)
    gl.glUniform2f(shader.getUniformLocation("uDelta"), 1f / size, 1f / size)

    source.bind(0)
    gl.glUniform1i(shader.getUniformLocation("uTexture"), 0)

    val aPosition = shader.getAttributeLocation("aPosition")
    shader.enableVertexAttribute(aPosition)
    vertexData.position(0)
    gl.glVertexAttribPointer(aPosition, 2, GL20.GL_FLOAT, false, 8, vertexData)

    gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4)

    shader.disableVertexAttribute(aPosition)
    shader.end()
  }

  override def handleKeyboard(): Unit = {
    if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
      betta *= factor
      println(s"betta is $betta")
    }

    if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
      betta /= factor
      println(s"betta is $betta")
    }
  }
}
