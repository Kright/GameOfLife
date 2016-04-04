package com.lgorsl.game

import java.nio.FloatBuffer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.{GL20, Texture}

/**
  * Created by lgor on 04.04.2016.
  */
class GameOfLife(val vertexData: FloatBuffer, val size: Int) extends CellularAutomaton{

  private var shader: ShaderProgram = null

  override def init(): Unit = {
    shader = Resources.getShader("gameOfLife")
  }

  override def render(source: Texture): Unit = {
    val gl = Gdx.gl20
    shader.begin()

    source.bind(0)
    gl.glUniform1i(shader.getAttributeLocation("uTexture"), 0)
    gl.glUniform2f(shader.getUniformLocation("uDelta"), 1f / size, 1f / size)

    val aPosition = shader.getAttributeLocation("aPosition")
    shader.enableVertexAttribute(aPosition)
    vertexData.position(0)
    gl.glVertexAttribPointer(aPosition, 2, GL20.GL_FLOAT, false, 8, vertexData)

    gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4)

    shader.disableVertexAttribute(aPosition)
    shader.end()
  }
}
