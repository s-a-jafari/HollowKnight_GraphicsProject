package advanceProgramming.project.com.helper;

import advanceProgramming.project.com.model.BreakableWall;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class TiledMapHelper {
    private TiledMap tiledMap;

    public TiledMap loadMap(String path) {
        tiledMap = new TmxMapLoader().load(path);
        return tiledMap;
    }

    public Array<SolidBlock> getSolidRectangles() {
        Array<SolidBlock> solidBlocks = new Array<>();
        MapLayer layer = tiledMap.getLayers().get("logical");

        if (layer != null) {
            for (MapObject object : layer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    String name = object.getName();

                    if (name == null || (!name.equalsIgnoreCase("BreakableWall") &&
                        !name.equalsIgnoreCase("triggerBlock") &&
                        !name.equalsIgnoreCase("startBlock") &&
                        !name.equalsIgnoreCase("next") &&
                        !name.equalsIgnoreCase("voidheart") &&
                        !name.equalsIgnoreCase("bossCameraArena"))) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();

                        boolean isDeadly = false;
                        if (object.getProperties().containsKey("deadly")) {
                            isDeadly = object.getProperties().get("deadly", Boolean.class);
                        }

                        solidBlocks.add(new SolidBlock(rect.x, rect.y, rect.width, rect.height, isDeadly));
                    }
                }
            }
        }
        return solidBlocks;
    }

    // getting the spawn point from the map for "The Knight" :
    public Vector2 getSpawnPoint() {
        MapLayer layer = tiledMap.getLayers().get("logical");

        if (layer != null) {
            for (MapObject object : layer.getObjects()) {
                String name = object.getName();

                if (name != null && name.equalsIgnoreCase("SpawnPlayer")) {
                    Float x = object.getProperties().get("x", Float.class);
                    Float y = object.getProperties().get("y", Float.class);
                    if (x != null && y != null) return new Vector2(x, y);
                }
            }
        }
        return new Vector2(300, 1000);
    }

    // getting the spawn point from the map for enemies :
    public Array<Vector2> getEnemySpawns(String enemyName) {
        Array<Vector2> spawns = new Array<>();
        MapLayer layer = tiledMap.getLayers().get("logical");

        if (layer != null) {
            for (MapObject object : layer.getObjects()) {
                String name = object.getName();

                if (name != null && name.equalsIgnoreCase(enemyName)) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        spawns.add(new Vector2(rect.x, rect.y));
                    } else {
                        Float x = object.getProperties().get("x", Float.class);
                        Float y = object.getProperties().get("y", Float.class);
                        if (x != null && y != null) spawns.add(new Vector2(x, y));
                    }
                }
            }
        }
        return spawns;
    }

    public Array<BreakableWall> getBreakableWalls() {
        Array<BreakableWall> walls = new Array<>();
        MapLayer layer = tiledMap.getLayers().get("logical");

        if (layer != null) {
            for (MapObject object : layer.getObjects()) {
                String name = object.getName();
                if (name != null && name.equalsIgnoreCase("BreakableWall")) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        walls.add(new BreakableWall(rect.x, rect.y, rect.width, rect.height));
                    }
                }
            }
        }
        return walls;
    }

    public Array<Rectangle> getKindOfBlock(String kindOfBlock) {
        Array<Rectangle> walls = new Array<>();
        MapLayer layer = tiledMap.getLayers().get("logical");

        if (layer != null) {
            for (MapObject object : layer.getObjects()) {
                String name = object.getName();
                if (name != null && name.equalsIgnoreCase(kindOfBlock)) {
                    if (object instanceof RectangleMapObject) {
                        walls.add(((RectangleMapObject) object).getRectangle());
                    }
                }
            }
        }
        return walls;
    }
}
