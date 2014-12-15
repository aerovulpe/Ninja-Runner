package com.aerisvulpe.shinobi.scenes;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;

import com.aerisvulpe.shinobi.Main;

/**
 * SceneManager class handles the switching of the scenes and loading scene resources.
 * @author Ritesh Bhattarai
 */

public class SceneManager {
       
        private static Main core;
       
        public static void init(Main base)
        {
                core = base;
        }
       
        public static void setScene(Scene scene)
        {
                core.getEngine().setScene(scene);
        }
       
        public static void loadTexture(Texture texture)
        {
                core.getEngine().getTextureManager().loadTexture(texture);
        }
        
        public static void unloadTexture(Texture texture)
        {
        	core.getEngine().getTextureManager().unloadTexture(texture);
        }
       
        public static void loadFont(Font font)
        {
                core.getEngine().getFontManager().loadFont(font);
        }
        
}





