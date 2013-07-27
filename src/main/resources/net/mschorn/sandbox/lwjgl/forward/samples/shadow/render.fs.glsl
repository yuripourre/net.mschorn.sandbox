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


layout(binding = 0) uniform sampler2D shadow;
layout(binding = 1) uniform sampler2D diffuse;
layout(binding = 2) uniform sampler2D specular;


in vec3 fl;
in vec3 fv;
in vec3 fn;
in vec2 fvt;


layout(location = 0) out vec4 color;


void main() {

	vec3 l = normalize(fl);
	vec3 n = normalize(fn);
	vec3 e = normalize(-fv);
	vec3 r = normalize(reflect(-l, fn));
	
	color = light.ambient;
	
	int v;
	for(int i = -2; i <= 2; i++){
		for(int j = -2; j <= 2; j++){
			vec4 s = texelFetch(shadow, ivec2(gl_FragCoord.x + j*2, gl_FragCoord.y + i*2), 0);
	    	if(s.x - s.y == 0)
	    		v += 1;
		}
	}
	
	if(v > 0) {
	  
		vec4 hcolor;
		hcolor = texture2D(diffuse, fvt) * light.diffuse * max(dot(n, l), 0.0);
		hcolor += texture2D(specular, fvt) * light.specular * pow(max(dot(r, e), 0.0), 5);		
	
		color += hcolor * (v / 25.0);
	
	}

}