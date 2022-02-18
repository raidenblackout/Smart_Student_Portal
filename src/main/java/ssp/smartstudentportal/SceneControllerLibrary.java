package ssp.smartstudentportal;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.util.Callback;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class SceneControllerLibrary {
    private static final Map<Class<?>, Callback<Class<?>, Object>> classCallbackHashMap = new HashMap<>();

    public static Object constructController(Class<?> controllerClass) {
        if (classCallbackHashMap.containsKey(controllerClass)) {
            return loadControllerWithSavedMethod(controllerClass);
        } else {
            return loadControllerWithDefaultConstructor(controllerClass);
        }
    }

    private static Object loadControllerWithSavedMethod(Class<?> controller) {
        try {
            return (classCallbackHashMap.get(controller)).call(controller);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object loadControllerWithDefaultConstructor(Class<?> controller) {
        try {
            return controller.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void addControllerMethod(Class<?> controller, Callback<Class<?>, Object> method) {
        classCallbackHashMap.put(controller, method);
    }

    public static Parent load(String location) throws IOException {
        FXMLLoader loader = getLoader(location);
        return loader.load();
    }

    public static FXMLLoader getLoader(String location) {
        return new FXMLLoader(
                SceneControllerLibrary.class.getResource(location),
                null,
                new JavaFXBuilderFactory(),
                controllerClass -> constructController(controllerClass));
    }

    public void removeControllerMethod(Class<?> controller) {
        classCallbackHashMap.remove(controller);
    }
}
