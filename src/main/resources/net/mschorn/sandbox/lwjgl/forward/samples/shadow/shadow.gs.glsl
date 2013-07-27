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


layout(triangles_adjacency) in;
layout(triangle_strip, max_vertices=15) out;


void main() {	

	float shadowVolumeLength = 2;

	vec4 v0 = mvp.mv * gl_in[0].gl_Position;
	vec4 v2 = mvp.mv * gl_in[2].gl_Position;
	vec4 v4 = mvp.mv * gl_in[4].gl_Position;
	
	vec4 vectorVertex0Light = normalize(v0 - light.position); 
	vec4 vectorVertex2Light = normalize(v2 - light.position); 
	vec4 vectorVertex4Light = normalize(v4 - light.position); 
	
	vec3 vectorFace0Normal = normalize(cross((v4 - v0).xyz, (v2 - v0).xyz));
	vec3 vectorFace0Light = normalize(v4 + v2 + v0 - 3 * light.position).xyz; 
    float dotFace0Light = dot(vectorFace0Normal, vectorFace0Light);
   
 	vec4 movedVertex0 = mvp.p * (v0 + vectorVertex0Light * shadowVolumeLength); 
    vec4 movedVertex2 = mvp.p * (v2 + vectorVertex2Light * shadowVolumeLength); 
    vec4 movedVertex4 = mvp.p * (v4 + vectorVertex4Light * shadowVolumeLength); 

	
	if (dotFace0Light > 0.0) {
	
		vec4 v1 = mvp.mv * gl_in[1].gl_Position;
		vec4 v3 = mvp.mv * gl_in[3].gl_Position;
		vec4 v5 = mvp.mv * gl_in[5].gl_Position;
		
		vec4 vp0 = mvp.mvp * gl_in[0].gl_Position;
		vec4 vp2 = mvp.mvp * gl_in[2].gl_Position;
		vec4 vp4 = mvp.mvp * gl_in[4].gl_Position;
	
	    vec3 vectorFace1Normal = normalize(cross((v2 - v0).xyz, (v1 - v0).xyz));
    	vec3 vectorFace2Normal = normalize(cross((v4 - v2).xyz, (v3 - v2).xyz));
    	vec3 vectorFace3Normal = normalize(cross((v0 - v4).xyz, (v5 - v4).xyz));
	
	   	vec3 vectorFace1Light = normalize(((v2 + v1 + v0) - 3 * light.position).xyz);
    	vec3 vectorFace2Light = normalize(((v4 + v3 + v2) - 3 * light.position).xyz);
    	vec3 vectorFace3Light = normalize(((v5 + v4 + v0) - 3 * light.position).xyz); 
	
	  	float dotFace1Light = dot(vectorFace1Normal, vectorFace1Light);
    	float dotFace2Light = dot(vectorFace2Normal, vectorFace2Light);
    	float dotFace3Light = dot(vectorFace3Normal, vectorFace3Light);

	
		gl_Position = vp0;
  		EmitVertex();
  	
  		gl_Position = vp2;
  		EmitVertex();
  	
  		gl_Position = vp4;
  		EmitVertex();
  	
  		EndPrimitive(); 	
	
	
		if (dotFace1Light <= 0.0) {
			
			gl_Position = vp0;
			EmitVertex();
			
			gl_Position = movedVertex0;
			EmitVertex();
			
			gl_Position = vp2;
			EmitVertex();			
			
			gl_Position = movedVertex2;
			EmitVertex();						
			
			EndPrimitive();					
		
		}
		
		
		if (dotFace2Light <=  0.0) {			
			
			gl_Position = vp2;
			EmitVertex();
			
			gl_Position = movedVertex2;
			EmitVertex();	
			
			gl_Position = vp4;
			EmitVertex();	
			
			gl_Position = movedVertex4;
			EmitVertex();
			
			EndPrimitive();	
		
		}
		
		
		if (dotFace3Light <=  0.0) {
			
			gl_Position = vp4;
			EmitVertex();
			
			gl_Position = movedVertex4;
			EmitVertex();
			
			gl_Position = vp0;
			EmitVertex();
			
			gl_Position = movedVertex0;
			EmitVertex();		
			
			EndPrimitive();				
		
		}
			
		
	} else {
	
		gl_Position = movedVertex0;
  		EmitVertex();
  	
  		gl_Position = movedVertex2;
  		EmitVertex();
  	
  		gl_Position = movedVertex4;
  		EmitVertex();
  	
  		EndPrimitive(); 
  			
	}
	
	
}
