attribute vec2 aPosition;

void main(){
  gl_Position = vec4(aPosition.xy, 0.0, 1.0);
}

//[FRAGMENT]

uniform vec4 uColor;

void main(){
  gl_FragColor = uColor;
}
