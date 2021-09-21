#version 120

attribute vec3 gl_Vertex;
attribute vec2 gl_MultiTexCoord0;

varying vec2 frag_texture;

void main()
{
    gl_Position = vec4(gl_Vertex, 1.0);
    frag_texture = gl_MultiTexCoord0;
}