#version 300 es

in vec3 inPosition;
in vec3 inNormal;
in vec2 inTexCoords;

out vec2 texCoords;
// out vec3 normal;

uniform mat4 matM;
uniform mat4 matV;
uniform mat4 matP;

void main() {
    gl_Position = matP * matV * matM * vec4(inPosition, 1.0);
    texCoords = inTexCoords;
}