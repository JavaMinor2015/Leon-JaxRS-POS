package nl.stoux.posrs.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import nl.stoux.posrs.Domain.Abs.CrudModel;
import nl.stoux.posrs.Domain.Category;
import nl.stoux.posrs.Domain.Product;
import nl.stoux.posrs.Restful.Json.ModelSerializer;
import nl.stoux.posrs.Restful.Services.Abs.CrudService;
import nl.stoux.posrs.Restful.Services.CategoryService;
import nl.stoux.posrs.Restful.Services.ProductService;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Leon Stam on 22-9-2015.
 */
public class Globals {

    @Getter
    private static Gson gson;
    static {
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping();
        for (Class<? extends CrudModel> model : Arrays.asList(Category.class, Product.class)) {
            builder.registerTypeAdapter(model, new ModelSerializer<>(model));
        }
        gson = builder.create();
    }

    private static final HashMap<Class<? extends CrudModel>, Class<? extends CrudService>> classMap = new HashMap<>();
    static {
        classMap.put(Product.class, ProductService.class);
        classMap.put(Category.class, CategoryService.class);
    }

    public static Class<? extends CrudService> getService(Class<? extends CrudModel> modelClass) {
        return classMap.get(modelClass);
    }
}
