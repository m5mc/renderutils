#version 120

varying vec2 frag_texture;

uniform sampler2D source;

uniform float sigma = 4;
uniform int blurSize = 9;

uniform vec2 blurVec;
uniform vec2 texOffset;

const float pi = 3.14159265;

void main()
{
    float numBlurPixelsPerSide = float(blurSize / 2); 

    vec3 incrementalGaussian;
    incrementalGaussian.x = 1.0 / (sqrt(2.0 * pi) * sigma);
    incrementalGaussian.y = exp(-0.5 / (sigma * sigma));
    incrementalGaussian.z = incrementalGaussian.y * incrementalGaussian.y;

    vec4 avgValue = vec4(0.0, 0.0, 0.0, 0.0);
    float coefficientSum = 0.0;

    avgValue += texture2D(source, frag_texture.st) * incrementalGaussian.x;
    coefficientSum += incrementalGaussian.x;
    incrementalGaussian.xy *= incrementalGaussian.yz;

    for (float i = 1.0; i <= numBlurPixelsPerSide; i++) { 
        avgValue += texture2D(source, frag_texture.st - i * texOffset * 
                                blurVec) * incrementalGaussian.x;         
        avgValue += texture2D(source, frag_texture.st + i * texOffset * 
                                blurVec) * incrementalGaussian.x;         
        coefficientSum += 2.0 * incrementalGaussian.x;
        incrementalGaussian.xy *= incrementalGaussian.yz;
    }

    gl_FragColor = avgValue / coefficientSum;
}