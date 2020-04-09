#version 300 es

in vec3 inPosition;
in vec3 inColor;

out vec3 color;

uniform mat4 matM;
uniform mat4 matV;
uniform mat4 matP;

void main() {
	gl_Position = matP * matV * matM * vec4(inPosition, 1.0);
	color = inColor;
}