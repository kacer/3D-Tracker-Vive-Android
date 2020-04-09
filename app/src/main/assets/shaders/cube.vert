#version 300 es

in vec3 inPosition;
in vec3 inNormal;

uniform mat4 matM;
uniform mat4 matV;
uniform mat4 matP;
uniform vec4 lightPosition;

out vec3 normal;
out vec3 lightDir;
out vec3 viewDir;
out float lightDistance;

void main() {
    vec4 pos = matM * vec4(inPosition, 1.0);
    vec4 posMV = matV * pos;
    gl_Position = matP * posMV;
    normal = inverse(transpose(mat3(matV * matM))) * inNormal;
    normal = normalize(normal);
    lightDir = (lightPosition - pos).xyz;
    lightDistance = length(lightDir);
    viewDir = -posMV.xyz;
}