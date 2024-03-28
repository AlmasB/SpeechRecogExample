package com.almasb.example;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.KeepOnScreenComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.intelligence.speechrecog.SpeechRecognitionService;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
public class SpeechRecogApp extends GameApplication {

    private Map<String, Color> colors = Map.of(
            "yellow", Color.YELLOW,
            "green", Color.GREEN,
            "blue", Color.BLUE,
            "red", Color.RED
    );

    private Map<String, Entity> entities = new HashMap<>();

    private Map<String, Point2D> directions = Map.of(
            "up", new Point2D(0, -1),
            "down", new Point2D(0, 1),
            "left", new Point2D(-1, 0),
            "right", new Point2D(1, 0)
    );

    private Entity currentEntity;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.addEngineService(SpeechRecognitionService.class);
    }

    @Override
    protected void initGame() {
        getService(SpeechRecognitionService.class).addInputHandler((input, confidence) -> {
            handleInput(input);
        });

        getService(SpeechRecognitionService.class).start();

        colors.forEach((name, color) -> {
            var e = spawnEntity(color);
            entities.put(name, e);

            currentEntity = e;
        });
    }

    private void handleInput(String input) {
        var tokens = input.trim().toLowerCase().split(" +");

        for (String token : tokens) {
            if (colors.containsKey(token)) {
                currentEntity = entities.get(token);
            }

            if (directions.containsKey(token)) {
                var dir = directions.get(token);

                currentEntity.getComponent(ProjectileComponent.class)
                        .setDirection(dir);
            }
        }
    }

    private Entity spawnEntity(Color color) {
        return entityBuilder()
                .at(640, 360)
                .viewWithBBox(new Rectangle(40, 40, color))
                .with(new ProjectileComponent(new Point2D(1, 0), 250))
                .with(new KeepOnScreenComponent())
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}