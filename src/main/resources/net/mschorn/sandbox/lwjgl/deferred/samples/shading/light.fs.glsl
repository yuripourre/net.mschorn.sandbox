/*
 * Copyright 2012, Michael Schorn (me@mschorn.net). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice, this list of
 *      conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials
 *      provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */


#version 420 core


layout(std140, binding = 0) uniform MVP {

    mat4 p;
    mat4 mv;
    mat4 mvp;
    mat3 nm;

} mvp;


layout(std140, binding = 1) uniform Light {

	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	vec4 position;

} light;


layout(binding = 0) uniform sampler2D tv;
layout(binding = 1) uniform sampler2D tvn;
layout(binding = 2) uniform sampler2D tdiffuse;
layout(binding = 3) uniform sampler2D tspecular;


in vec2 fvt;


layout(location = 0) out vec4 color;


void main() {

	vec4 v = vec4(texture2D(tv, fvt).rgb, 1.0);
	vec4 n = vec4(texture2D(tvn, fvt).rgb, 0.0);

	vec3 l = normalize(vec3(light.position - v));
	vec3 e = normalize(-v);
	vec3 r = normalize(reflect(-l, n));

	float d = length(vec3(light.position - v));

	if(v.z < 0) {

		color = light.ambient;
		color += texture2D(tdiffuse, fvt) * light.diffuse * max(dot(n, l), 0.0);
		color += texture2D(tspecular, fvt) * light.specular * pow(max(dot(r, e), 0.0), 5);

	} else {

		color = vec4(0, 0, 0, 0);

	}

}