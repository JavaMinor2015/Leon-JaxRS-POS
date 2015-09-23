package nl.stoux.posrs.Restful.Json;

import com.google.gson.*;
import lombok.AllArgsConstructor;
import nl.stoux.posrs.Domain.Abs.CrudModel;
import nl.stoux.posrs.Restful.Services.Abs.CrudService;
import nl.stoux.posrs.Util.Globals;
import nl.stoux.posrs.Util.Methods;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Leon Stam on 23-9-2015.
 */
@AllArgsConstructor
public class ModelSerializer<Model extends CrudModel> implements JsonSerializer<Model>, JsonDeserializer<Model> {

    private Class<Model> clazz;

    @Override
    public JsonElement serialize(Model crudModel, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("url", Methods.getModelURL(crudModel.getClass()) + "/" + crudModel.getId());
        for (Field field : crudModel.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                json.add(field.getName(), context.serialize(field.get(crudModel)));
            } catch (Exception e) {
                //YOLO
            }
        }
        return json;
    }

    @Override
    public Model deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            JsonObject json = jsonElement.getAsJsonObject();
            Model newInstance = clazz.newInstance();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                Field f = clazz.getDeclaredField(entry.getKey());
                if (f == null || !isUsable(f)) {
                    continue;
                }

                if (CrudModel.class.isAssignableFrom(f.getType())) {
                    //It's a CrudModel class
                    JsonElement value = entry.getValue();
                    if (value.isJsonPrimitive()) {
                        //ID of the object is given
                        JsonPrimitive otherElem = value.getAsJsonPrimitive();
                        int id = otherElem.getAsInt(); //TODO: Currently just assuming it's an int

                        //Find the service
                        Class<? extends CrudService> service = Globals.getService((Class<? extends CrudModel>) f.getType());
                        boolean isFound = false;
                        for (Field field : service.getDeclaredFields()) {
                            if (Modifier.isStatic(field.getModifiers())) {
                                if (!field.isAccessible()) {
                                    field.setAccessible(true);
                                }

                                if (Map.class.isAssignableFrom(field.getType())) {
                                    Map map = (Map) field.get(null);
                                    if (map.containsKey(id)) {
                                        f.set(newInstance, map.get(id));
                                        isFound = true;
                                    }
                                }
                            }
                        }

                        if (!isFound) {
                            throw new JsonParseException("Couldn't find " + f.getName() + " with id " + id);
                        }
                    } else {
                        f.set(newInstance, null);
                    }
                } else {
                    f.set(newInstance, jsonDeserializationContext.deserialize(entry.getValue(), f.getType()));
                }
            }

            return newInstance;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonParseException(e.getMessage());
        }
    }

    /**
     * Check if a field is usable
     * @param field The field
     * @return is usable
     */
    private boolean isUsable(Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            return false;
        }

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        return true;
    }
}
