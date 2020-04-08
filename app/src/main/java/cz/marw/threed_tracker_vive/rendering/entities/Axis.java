package cz.marw.threed_tracker_vive.rendering.entities;

import android.content.Context;
import android.opengl.GLES31;

import java.util.ArrayList;
import java.util.List;

import cz.marw.threed_tracker_vive.rendering.glutils.GLBuffers;
import cz.marw.threed_tracker_vive.rendering.glutils.ToIntArray;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4;

public class Axis extends Entity {
	
	public Axis(Context context) {
		super(context, "axis");
	}

	@Override
	protected GLBuffers createGeometry(Context context) {
		float[] vertices = {
				// Vertex	Color
				0, 0, 0,	1, 0, 0,
				1, 0, 0,	1, 0, 0,
				0, 0, 0,	0, 1, 0,
				0, 1, 0,	0, 1, 0,
				0, 0, 0,	0, 0, 1,
				0, 0, 1, 	0, 0, 1
		};

		List<Integer> indices = new ArrayList<>();
		for(int i = 0; i < 6; i += 2) {
			indices.add(i);
			indices.add(i + 1);
		}

		GLBuffers.Attributes[] attributes = new GLBuffers.Attributes[] {
				new GLBuffers.Attributes("inPosition", 3),
				new GLBuffers.Attributes("inColor", 3)
		};

		return new GLBuffers(vertices, ToIntArray.convert(indices), attributes);
	}

	@Override
	public void draw(Mat4 view, Mat4 projection) {
		GLES31.glUseProgram(shaderProgram);
		GLES31.glUniformMatrix4fv(locMatM, 1, false, getModelMat().floatArray(), 0);
		GLES31.glUniformMatrix4fv(locMatV, 1, false, view.floatArray(), 0);
		GLES31.glUniformMatrix4fv(locMatP, 1, false, projection.floatArray(), 0);
		glBuffers.draw(GLES31.GL_LINES, shaderProgram);
		GLES31.glUseProgram(0);
	}
	
}
