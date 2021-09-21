#version 120

varying vec2 frag_texture;

uniform sampler2D source;
uniform float threshold = 0.4;
uniform float intensity = 1.2;

void main()
{
    vec3 col = vec3(texture2D(source, frag_texture));
    
    float lum = dot(vec3(0.336, 0.555, 0.107), pow(col, vec3(2.2)));
    float rel = max(0, lum - threshold) / (1 - threshold);

    gl_FragColor = vec4(col * rel * intensity, 1);
}