package nl.stoux.posrs.Restful.Json;

import com.google.gson.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.stoux.posrs.Domain.Abs.CrudModel;
import nl.stoux.posrs.Util.ExceptionEater;
import nl.stoux.posrs.Util.Globals;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Leon Stam on 23-9-2015.
 */
public class ModelSerializer<Model extends CrudModel> implements JsonSerializer<Model>, JsonDeserializer<Model> {

    private Class<Model> modelClass;
    private Constructor<Model> clazzConstructor;

    private String modelURL;

    public ModelSerializer(Class<Model> clazz, String modelURL) {
        this.modelClass = clazz;
        this.modelURL = modelURL;

        clazzConstructor = findSmallestConstructor(clazz);
    }

    //Suppress warning for unchecked cast as the clazz cannot have a constructor that doesn't return a Model
    @SuppressWarnings("unchecked")
    private Constructor<Model> findSmallestConstructor(Class<Model> clazz) {
        //Find the smallest constructor
        Constructor<?> smallestConstructor = null;
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (smallestConstructor == null) {
                smallestConstructor = constructor;
                continue;
            }

            if (smallestConstructor.getParameterCount() > constructor.getParameterCount()) {
                smallestConstructor = constructor;
            }
        }
        assert(smallestConstructor != null);
        return (Constructor<Model>) smallestConstructor;
    }


    @Override
    public JsonElement serialize(Model crudModel, Type type, JsonSerializationContext context) {
        //Create the new Json object
        final JsonObject json = new JsonObject();
        json.addProperty("url", modelURL + "/" + crudModel.getId());

        Arrays.stream(crudModel.getClass().getDeclaredFields())
                .filter(f -> isUsable(f, true))
                .forEach(f -> ExceptionEater.eat(
                        () -> json.add(f.getName(), context.serialize(f.get(crudModel))))
                );

        return json;
    }

    @Override
    public Model deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject json = jsonElement.getAsJsonObject();
            final Model newInstance = createObject(json);

            json.entrySet().stream()
                    .map(this::findField)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(fieldValue -> {
                        if (isCrudModel(fieldValue)) {
                            assignCrudModel(newInstance, fieldValue, context);
                        } else {
                            fieldValue.setIn(newInstance, context);
                        }
                    });

            return newInstance;
        } catch (JsonParseException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonParseException(e.getMessage());
        }
    }

    /**
     * Create the object using the found constructor
     * @param json The JsonObject
     * @return The model
     * @throws JsonParseException If an element is missing that is required
     * @throws Exception Any other case
     */
    private Model createObject(JsonObject json) throws JsonParseException, Exception {
        //Find the paramaters
        Parameter[] parameters = clazzConstructor.getParameters();
        Object[] foundParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            if (!json.has(p.getName())) {
                throw new JsonParseException("Missing required element: " + p.getName());
            }

            foundParameters[i] = Globals.getGson().fromJson(json.get(p.getName()), p.getParameterizedType());
        }

        return clazzConstructor.newInstance(foundParameters);
    }


    /**
     * Check if a field is usable
     * @param field The field
     * @param finalAllowed Final fields are allowed
     * @return is usable
     */
    private boolean isUsable(Field field, boolean finalAllowed) {
        int modifier = field.getModifiers();
        if (Modifier.isStatic(modifier) || (!finalAllowed && Modifier.isFinal(modifier))) {
            return false;
        }

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        return true;
    }

    private boolean isCrudModel(FieldValue fieldValue) {
        return CrudModel.class.isAssignableFrom(fieldValue.getField().getType());
    }

    private void assignCrudModel(Model newInstance, FieldValue fieldValue, JsonDeserializationContext context) {
        //Find the ID type of the given
        Class<? extends CrudModel> fieldType = (Class<? extends CrudModel>) fieldValue.getField().getType();
        Field idField;
        try {
            idField = fieldType.getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            //Impossible, that means it's an invalid CrudModel
            e.printStackTrace();
            System.err.println("Invalid CrudModel: " + fieldType.getName() + " | Missing ID field");
            return;
        }
        Object id = context.deserialize(fieldValue.getValue(), idField.getType());

        //Get the Object with the given identifier
        //TODO: Use the repository
        //fieldValue.getField().set(newInstance, null);
    }

    /**
     * Find a field
     * @param entry The map entry (Key = Name of field, Value = new Field value)
     * @return The FieldValue combination
     */
    private Optional<FieldValue> findField(Map.Entry<String, JsonElement> entry) {
        try {
            Field f = modelClass.getDeclaredField(entry.getKey());
            if (!isUsable(f, false)) {
                return Optional.empty();
            }
            return Optional.of(new FieldValue(f, entry.getValue()));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }

    @Getter
    @AllArgsConstructor
    private class FieldValue {
        private Field field;
        private JsonElement value;

        public void setIn(Object o, JsonDeserializationContext context) {
            ExceptionEater.eat(() -> field.set(o, context.deserialize(value, field.getType())));
        }
    }

}
