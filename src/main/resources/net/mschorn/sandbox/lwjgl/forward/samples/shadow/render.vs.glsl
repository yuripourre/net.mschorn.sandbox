/*
 * Copyright 2013, Michael Schorn (me@mschorn.net). All rights reserved.
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


layout(location = 0) in vec4 v;
layout(location = 1) in vec2 vt;
layout(location = 2) in vec3 vn;


out vec3 fl;
out vec3 fv;
out vec3 fn;
out vec2 fvt;


void main() {

	fl = normalize(vec3(light.position - mvp.mv * v));
	fv = vec3(mvp.mv * v);
	fn = normalize(mvp.nm * vn);

	fvt = vt;

	gl_Position = mvp.mvp * v;

}
