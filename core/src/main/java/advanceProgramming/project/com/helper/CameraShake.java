package advanceProgramming.project.com.helper;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class CameraShake {
    private static float intensity = 0;
    private static float duration = 0;
    private static float timer = 0;

    public static void shake(float shakeIntensity, float shakeDuration) {
        intensity = shakeIntensity;
        duration = shakeDuration;
        timer = 0;
    }

    public static void update(float delta, OrthographicCamera camera) {
        if (timer < duration) {
            timer += delta;
            float currentPower = intensity * (1f - (timer / duration));
            float x = (MathUtils.random() - 0.5f) * 2 * currentPower;
            float y = (MathUtils.random() - 0.5f) * 2 * currentPower;
            camera.translate(x, y);
        }
    }
}
