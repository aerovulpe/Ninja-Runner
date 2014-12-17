package me.aerovulpe.ninjarunner.scenes;

import me.aerovulpe.ninjarunner.MainActivity;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;

public class SceneManager {

    private static MainActivity sMainActivity;

    public static void init(MainActivity mainActivity) {
        sMainActivity = mainActivity;
    }

    public static void setScene(Scene scene) {
        sMainActivity.getEngine().setScene(scene);
    }

    public static void loadTexture(Texture texture) {
        sMainActivity.getEngine().getTextureManager().loadTexture(texture);
    }

    public static void unloadTexture(Texture texture) {
        sMainActivity.getEngine().getTextureManager().unloadTexture(texture);
    }

    public static void loadFont(Font font) {
        sMainActivity.getEngine().getFontManager().loadFont(font);
    }

}





