
uniform mat3 uMatrix;

attribute vec2 aPosition;

varying vec2 vPos;

void main(){
  vec2 pos = (uMatrix * vec3(aPosition.xy, 1.0)).xy;
  vPos = aPosition * 0.5 + vec2(0.5, 0.5);
  gl_Position = vec4(pos.xy, 0.0, 1.0);
}

//[FRAGMENT]

uniform sampler2D uTexture;

varying vec2 vPos;

void main(){
  gl_FragColor = texture(uTexture, vPos);
}
