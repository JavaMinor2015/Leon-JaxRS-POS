package nl.stoux.posrs.Restful.Json;

import com.google.gson.*;
import nl.stoux.posrs.Domain.Abs.CrudModel;
import nl.stoux.posrs.Util.Methods;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * Created by Leon Stam on 23-9-2015.
 */
public class ModelSerializer implements JsonSerializer<CrudModel> {

    @Override
    public JsonElement serialize(CrudModel crudModel, Type type, JsonSerializationContext context) {
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

}
