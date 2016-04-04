package com.lgorsl.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.{GL20, Texture}

/**
  * Frame buffer
  * I will render to it
  *
  * Created by lgor on 03.04.2016.
  */
class FrameBufferImpl(size: Int) {

  val (texture, id) = {
    val fb = makeFrameBuffer()
    val t = makeTexture(size)
    Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D, t.getTextureObjectHandle, 0)

    val status = Gdx.gl20.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER)
    assert(status == GL20.GL_FRAMEBUFFER_COMPLETE, s"wrong status is $status")

    (t, fb)
  }

  private def makeFrameBuffer() = {
    val frameBuffer = Gdx.gl20.glGenFramebuffer()
    Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, frameBuffer)
    frameBuffer
  }

  private def makeTexture(size: Int) = {
    val t = new Texture(size, size, Format.RGB888)
    t.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
    t.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    t
  }

  def bind(bufferId: Int) = {
    Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, bufferId)
  }

  def bind(): Unit = bind(id)
}
