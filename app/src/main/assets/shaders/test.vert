#version 300 es

uniform mat4 matMVP;

in vec3 position;

void main() {
    gl_Position = matMVP * vec4(position, 1.0);
}
