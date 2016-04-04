
attribute vec2 aPosition;

varying vec2 vPos;

void main(){
  vPos = aPosition * 0.5 + vec2(0.5, 0.5);
  gl_Position = vec4(aPosition.xy, 0.0, 1.0);
}

//[FRAGMENT]

uniform sampler2D uTexture;

uniform vec4 uBonus;
uniform float uBetta;
uniform vec2 uDelta;

varying vec2 vPos;

float rand(vec2 co){
  //return fract(uBonus.x + sin(uBonus.y + dot(co.xy ,vec2(12.9898 + uBonus.z + co.x,78.233 + uBonus.w + co.y))) * 43758.5453);     //bullshit
  return fract(1234 * sin(1214 * co.x) * uBonus.x + 1212 * sin(761 * co.y) * uBonus.y + 987 * uBonus.z * sin(1543 * sqrt(co.x * co.y)) + uBonus.w);
}

void main(){
  vec2 right = vec2(uDelta.x, 0.0);
  vec2 down    = vec2(0.0, uDelta.y);

  float all =
         texture(uTexture, vPos + right).r +
         texture(uTexture, vPos + down).r +
         texture(uTexture, vPos - down).r +
         texture(uTexture, vPos - right).r;

  float me = texture(uTexture, vPos).r;

  float allSpin = all - 2.0;
  float mySpin = me - 0.5;

  float energy = allSpin * mySpin * uBetta;

  float p1 = exp(energy);
  float p2 = exp(-energy);

  float prob = p2 / (p1 + p2);
  prob *= 0.1;

  float res = me;
  if (prob > rand(vPos)){
    res = 1.0 - res;
  }

  gl_FragColor = vec4(res, res, res, 1.0);
}
