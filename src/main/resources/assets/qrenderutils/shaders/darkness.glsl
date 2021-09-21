#version 120

varying vec2 frag_texture;

uniform sampler2D source;
uniform float threshold = 0;

void main()
{
    vec3 col = vec3(texture2D(source, frag_texture));

    gl_FragColor = vec4(col, 1);
}