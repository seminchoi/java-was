package codesquad.container;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Container {
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String FILE_SEPARATOR = File.separator;
    
    private final Map<String, Object> container = new HashMap<>();
    private final List<String> targets = new ArrayList<>();
    private final List<Class<?>> componentsToInitialize = new ArrayList<>();

    public Container(ContainerConfigurer containerConfigurer) {
        targets.addAll(containerConfigurer.getTargets());
    }

    public void init() {
        for (String targetPackage : targets) {
            try {
                scanTarget(targetPackage);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Failed to scan package: " + targetPackage, e);
            }
        }

        for (Class<?> component : componentsToInitialize) {
            createInstance(component, new ArrayList<>());
        }
    }

    private void scanTarget(String target) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            classLoader.loadClass(target);
            processClass(target);
            return;
        } catch (ClassNotFoundException ignore) {
        }

        String path = target.replace(PACKAGE_SEPARATOR, FILE_SEPARATOR);
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                scanDirectory(new File(resource.getFile()), target);
            } else if (resource.getProtocol().equals("jar")) {
                scanJar(resource, target);
            }
        }
    }

    private void scanDirectory(File directory, String packageName) throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(file, packageName + "." + file.getName());
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    processClass(className);
                }
            }
        }
    }

    private void scanJar(URL resource, String packageName) throws IOException, ClassNotFoundException {
        String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith(packageName.replace(PACKAGE_SEPARATOR, FILE_SEPARATOR)) && entryName.endsWith(".class")) {
                    String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                    processClass(className);
                }
            }
        }
    }

    private void processClass(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        if (clazz.isAnnotationPresent(Component.class)) {
            componentsToInitialize.add(clazz);
        }
    }

    private Object createInstance(Class<?> component, List<String> processingClasses) {
        String camelName = toCamelCase(component.getSimpleName());
        if (container.containsKey(camelName)) {
            return container.get(camelName);
        }

        if (processingClasses.contains(component.getName())) {
            throw new RuntimeException("Circular dependency detected for class: " + component.getName());
        }

        processingClasses.add(component.getName());

        Constructor<?>[] constructors = component.getDeclaredConstructors();
        if (constructors.length != 1) {
            throw new RuntimeException("Component must have exactly one constructor: " + component.getName());
        }
        Constructor<?> constructor = constructors[0];

        Parameter[] parameterTypes = constructor.getParameters();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Object dependency = getDependency(parameterTypes[i], processingClasses);
            parameters[i] = dependency;
        }

        try {
            constructor.setAccessible(true);
            Object instance = constructor.newInstance(parameters);
            String key = toCamelCase(component.getSimpleName());
            container.put(key, instance);

            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to instantiate component: " + component.getName(), e);
        }
    }

    private Object getDependency(Parameter parameter, List<String> processingClasses) {
        Class<?> parameterType = parameter.getType();
        String key = toCamelCase(parameterType.getSimpleName());
        if (container.containsKey(key)) {
            return container.get(key);
        }

        String parameterName = parameter.getName();
        for (Class<?> componentToInitialize : componentsToInitialize) {
            String camelCase = toCamelCase(componentToInitialize.getSimpleName());
            if (parameterName.equals(camelCase)) {
                return createInstance(componentToInitialize, processingClasses);
            }
        }

        List<Class<?>> matchedComponent = componentsToInitialize.stream()
                .filter(parameterType::isAssignableFrom)
                .toList();

        if (!matchedComponent.isEmpty()) {
            if (matchedComponent.size() > 1) {
                throw new RuntimeException("Multiple matching components found for type: " + parameterType.getName());
            }
            Class<?> matchedClass = matchedComponent.get(0);
            return createInstance(matchedClass, processingClasses);
        } else {
            throw new RuntimeException("No matching component found for type: " + parameterType.getName());
        }
    }

    private String toCamelCase(String name) {
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    public Object getComponent(String name) {
        return container.get(name);
    }
}
