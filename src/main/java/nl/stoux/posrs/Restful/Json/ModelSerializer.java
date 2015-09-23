package nl.stoux.posrs.Restful.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import nl.stoux.posrs.Domain.Abs.CrudModel;

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
        json.addProperty("url", getURL(crudModel) + "/" + crudModel.getId());
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

    /**
     * Get the URL for a CrudModel
     * @param crudModel The model
     * @return The URL
     */
    private String getURL(CrudModel crudModel) {
        try {
            return (String) crudModel.getClass().getDeclaredField("URL").get(null);
        } catch (Exception e) {
            System.out.println("Failed to get URL: " + e.getMessage());
            return "";
        }
    }


}
