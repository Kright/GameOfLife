package com.lgorsl.game

import java.util.Random

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.{ApplicationAdapter, Gdx, Input, InputProcessor}

/**
  * keys:
  *
  * f1-f12: speed of simulation
  * space: pause
  *
  * 1,2 - changing algorithm (1 : Game of Life, 2: Ising model)
  *
  * left mouse click: draw point
  * right mouse click: draw many points
  * middle mouse click: move view
  * scroll: change scale
  *
  * -,+ : changing temperature (in Ising model only)
  *
  * Authors:
  * Igor Slobodskov
  * Shirkin Michael
  *
  * Created on 01.04.2016
  */
class Main extends ApplicationAdapter {

  val textureSize = 1024

  private val vertexData = Utils.makeBuffer(Array(-1f, 1f, 1f, 1f, -1f, -1f, 1f, -1f))
  private val frameBuffer = new Array[FrameBufferImpl](2)

  private var scale = 1f
  private val center = new Vector2(0f, 0f)
  private val screenSize = new Vector2()
  private val matrix = new Array[Float](9)

  private var running = true
  private var speed = 1.0
  private var progress = 0.0

  private val rnd = new Random()

  private def gl = Gdx.gl20

  private val automats = Array[CellularAutomaton](new GameOfLife(vertexData, textureSize), new IsingModel(vertexData, textureSize))
  private var currentAuto = automats(1)

  override def create() = {
    val InputP = new InputP()
    Gdx.input.setInputProcessor(InputP)

    frameBuffer(0) = new FrameBufferImpl(textureSize)
    frameBuffer(1) = new FrameBufferImpl(textureSize)
    frameBuffer(0).bind(0)

    clearFirstFrame(frameBuffer(0))

    automats.foreach(_.init())
  }

  override def resize(width: Int, height: Int): Unit = {
    screenSize.set(width, height)
    gl.glViewport(0, 0, width, height)
  }

  private def update(count: Int) = {
    for (i <- 1 to count) {
      frameBuffer(1).bind()
      gl.glViewport(0, 0, textureSize, textureSize)

      currentAuto.render(frameBuffer(0).texture)

      gl.glFlush()
      swapBuffers()
    }
  }

  override def render() = {
    val n = numberKeyPressed(Input.Keys.F1, 12)
    if (n != -1) {
      speed = Math.pow(2.5, n - 4) // f5 => speed == 1.0
      println(s"simulation speed = $speed")
    }

    val n2 = numberKeyPressed(Input.Keys.NUM_1, automats.size)
    if (n2 != -1) currentAuto = automats(n2)

    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) running ^= true

    if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
      running = false
      progress += speed
    }

    if (running) progress += speed

    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
      drawPoints(Gdx.input.getX, Gdx.input.getY, 0, 1)
    }

    if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) drawPoints(Gdx.input.getX, Gdx.input.getY, 10, 100)

    if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
      val sc = 2f / scale / screenSize.y
      center.add(sc * Gdx.input.getDeltaX, -sc * Gdx.input.getDeltaY)
    }

    val steps = Math.floor(progress)
    progress -= steps

    currentAuto.handleKeyboard()
    update(steps.toInt)

    renderToScreen(frameBuffer(0))
  }

  def swapBuffers() = {
    val f = frameBuffer(0)
    frameBuffer(0) = frameBuffer(1)
    frameBuffer(1) = f
  }

  /**
    * @return number of pressed key, or else -1
    */
  private def numberKeyPressed(firstCode: Int, count: Int): Int = {
    for (i <- 0 until count) {
      if (Gdx.input.isKeyJustPressed(firstCode + i)) return i
    }
    -1
  }

  private def clearFirstFrame(frameBuffer: FrameBufferImpl) = {
    frameBuffer.bind()
    gl.glViewport(0, 0, textureSize, textureSize)
    gl.glClearColor(1f, 1f, 1f, 1f)
    gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    gl.glFlush()
  }

  private def drawPoints(x: Float, y: Float, radius: Float, count: Int): Unit = {
    frameBuffer(0).bind()
    gl.glViewport(0, 0, textureSize, textureSize)

    renderPoints(generatePoints(
      -center.x + 2 * (-0.5f * screenSize.x / screenSize.y + x / screenSize.y) / scale,
      -center.y + 2 * (0.5f - y / screenSize.y) / scale,
      radius * 2 / screenSize.y / scale,
      math.max(1, (count / scale / scale).toInt)))
  }

  private def generatePoints(x: Float, y: Float, radius: Float, count: Int): Array[Float] = {
    val array = new Array[Float](count * 2)
    for (i <- 0 until count) {
      array(i * 2) = x + rnd.nextGaussian().toFloat * radius
      array(i * 2 + 1) = y + rnd.nextGaussian().toFloat * radius
    }
    array
  }

  private def renderToScreen(frameBuffer: FrameBufferImpl): Unit = {
    val shader = Resources.getShader("fromTexture")
    frameBuffer.bind(0)
    gl.glViewport(0, 0, screenSize.x.toInt, screenSize.y.toInt)

    shader.begin()

    frameBuffer.texture.bind(0)
    gl.glUniform1i(shader.getUniformLocation("uTexture"), 0)

    scale = Utils.clamp(scale, math.max(screenSize.x / screenSize.y, 1f), textureSize / 10)

    val scaleX = scale * screenSize.y / screenSize.x
    val scaleY = scale

    center.x = Utils.clamp(center.x, 1 / scaleX - 1, 1 - 1 / scaleX)
    center.y = Utils.clamp(center.y, 1 / scaleY - 1, 1 - 1 / scaleY)

    matrix(0) = scaleX
    matrix(4) = scaleY

    matrix(2) = center.x * scaleX
    matrix(5) = center.y * scaleY

    gl.glUniformMatrix3fv(shader.getUniformLocation("uMatrix"), 1, true, matrix, 0)

    val aPosition = shader.getAttributeLocation("aPosition")
    shader.enableVertexAttribute(aPosition)
    vertexData.position(0)
    gl.glVertexAttribPointer(aPosition, 2, GL20.GL_FLOAT, false, 8, vertexData)

    gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4)

    shader.disableVertexAttribute(aPosition)
    shader.end()
    gl.glFlush()
  }

  private def renderPoints(array: Array[Float]): Unit = {
    val shader = Resources.getShader("fill")
    val aPosition = shader.getAttributeLocation("aPosition")

    val coords = Utils.makeBuffer(array)

    shader.begin()
    shader.enableVertexAttribute(aPosition)

    gl.glUniform4f(shader.getUniformLocation("uColor"), 0f, 0f, 0f, 1f)

    coords.position(0)
    gl.glVertexAttribPointer(aPosition, 2, GL20.GL_FLOAT, false, 8, coords)

    gl.glDrawArrays(GL20.GL_POINTS, 0, array.size / 2)
    shader.end()

    gl.glFlush()
  }

  private class InputP extends InputProcessor {

    override def keyTyped(c: Char) = false

    override def mouseMoved(x: Int, y: Int) = false

    override def keyDown(k: Int) = false

    override def keyUp(k: Int) = false

    override def touchDown(x: Int, y: Int, p: Int, b: Int) = false

    override def touchUp(x: Int, y: Int, p: Int, b: Int) = false

    override def touchDragged(x: Int, y: Int, p: Int) = false

    override def scrolled(amount: Int): Boolean = {
      val dx = 2 * (-0.5f * screenSize.x / screenSize.y + Gdx.input.getX / screenSize.y) / scale
      val dy = 2 * (0.5f - Gdx.input.getY / screenSize.y) / scale
      val oldScale = scale
      scale *= Math.pow(1.05, -amount).toFloat
      scale = Math.min(scale, textureSize / 10)
      scale = Utils.clamp(scale, math.max(screenSize.x / screenSize.y, 1f), textureSize / 10)
      center.x -= dx * (1 - oldScale / scale)
      center.y -= dy * (1 - oldScale / scale)
      return true
    }
  }
}



