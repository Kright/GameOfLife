

attribute vec2 aPosition;

varying vec2 vPos;

void main(){
  vPos = aPosition * 0.5 + vec2(0.5, 0.5);
  gl_Position = vec4(aPosition.xy, 0.0, 1.0);
}

//[FRAGMENT]

uniform sampler2D uTexture;
uniform vec2 uDelta;

varying vec2 vPos;

void main(){
  vec2 right = vec2(uDelta.x, 0.0);
  vec2 up    = vec2(0.0, uDelta.y);

  float all =
         texture(uTexture, vPos + right + up).r +
         texture(uTexture, vPos + right).r +
         texture(uTexture, vPos + right - up).r +

         texture(uTexture, vPos + up).r +
         texture(uTexture, vPos - up).r +

         texture(uTexture, vPos - right + up).r +
         texture(uTexture, vPos - right).r +
         texture(uTexture, vPos - right - up).r;

  float me = texture(uTexture, vPos).r;
  float res = me;

  if (me > 0.5){ // if white
    if ( abs(all-5.0) < 0.5 ) res = 0.0; //new cell
  } else{  // if black
    if (all > 6.0 || all < 5.0)
      res = 1.0; //die
  }

  gl_FragColor = vec4(res, res, res, 1.0);
}
