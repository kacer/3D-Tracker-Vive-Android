package cz.marw.threed_tracker_vive.rendering.glutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES31;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.RawRes;
import cz.marw.threed_tracker_vive.R;

public class GLTexture2D {

    public static int loadTexture(Context context, @RawRes int textureRawId) {
        final int[] textureID = new int[1];

        GLES31.glGenTextures(1, textureID, 0);
        if(textureID[0] > 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), textureRawId, options);

            GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, textureID[0]);
            GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_WRAP_S, GLES31.GL_CLAMP_TO_EDGE);
            GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_WRAP_T, GLES31.GL_CLAMP_TO_EDGE);
            GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MIN_FILTER, GLES31.GL_LINEAR);
            GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MAG_FILTER, GLES31.GL_LINEAR);

            GLUtils.texImage2D(GLES31.GL_TEXTURE_2D, 0, bitmap, 0);

            bitmap.recycle();
        } else {
            System.err.println("GLTexture2D: glGenTextures returns invalid texture ID " +
                    "for texture resource with ID: " + textureRawId);
        }

        return textureID[0];
    }

}
