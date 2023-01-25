#type vertex
#version 330 core

layout (location = 0) in vec3 aPos;

out vec2 TexCoords;

void main()
{
	gl_Position = vec4(aPos, 1.0);
	TexCoords = aPos.xy;
}

#type fragment
#version 330 core

in vec2 TexCoords;

out vec4 FragColor;

uniform vec2 Center;
uniform float Scale;
uniform int Iterations;

vec2 ComplexMul(vec2 a, vec2 b)
{
	return vec2(a.x * b.x - a.y * b.y, a.x * b.y + a.y * b.x);
}

void main()
{
	vec2 c = (TexCoords - Center) / Scale;
	vec2 z = vec2(0.0, 0.0);

	int i;
	for (i = 0; i < Iterations; i++)
	{
		z = ComplexMul(z, z) + c;
		if (length(z) > 2.0)
		{
			break;
		}
	}

	if (i == Iterations)
	{
		FragColor = vec4(0.0, 0.0, 0.0, 1.0);
	}
	else
	{
		FragColor = vec4(vec3(float(i) / float(Iterations)), 1.0);
	}
}