#version 310 es

precision highp float;

uniform vec4 color;

out vec4 outColor;

void main() {
    if(color == vec4(0.0))
        outColor = vec4(0.05, 1.0, 0.8, 1.0);
    else
        outColor = color;
}
