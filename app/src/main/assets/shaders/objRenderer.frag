#version 310 es

precision highp float;

in vec2 texCoords;

out vec4 outColor;

uniform sampler2D texture0;

void main() {
    outColor = texture(texture0, texCoords);
    //outColor = vec4(0.5, 0.35, 0.85, 1.0);
}