#version 310 es

precision highp float;

in vec3 normal;
in vec3 lightDir;
in vec3 viewDir;
in float lightDistance;

uniform vec4 color;

out vec4 outColor;

float calcAttenuation() {
    float constant = 0.95;
    float linear = 0.05;
    float quadratic = 0.0;

    return 1.0 / (constant + linear * lightDistance + quadratic * pow(lightDistance, 2.0));
}

vec4 calculateLight() {
    vec4 baseColor = vec4(color);
    vec4 ambient = vec4(0.2, 0.2, 0.2, 1.0);
    vec4 specular = vec4(1.0, 1.0, 1.0, 1.0);

    vec3 ld = normalize(lightDir);

    float NDotL = max(0.0, dot(ld, normal));
    vec3 halfVector = normalize(lightDir + viewDir);
    float NDotH = max(0.0, dot(halfVector, normal));

    vec4 totalAmbient = baseColor * ambient;
    vec4 totalDiffuse = NDotL * baseColor;
    vec4 totalSpecular = specular * pow(NDotH, 12.0);

    //return totalAmbient + calcAttenuation() * (totalDiffuse + totalSpecular);
    return totalAmbient + totalDiffuse + totalSpecular;
}

void main() {
    outColor = calculateLight();
}