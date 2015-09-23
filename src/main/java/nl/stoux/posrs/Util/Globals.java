package nl.stoux.posrs.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.stoux.posrs.Domain.Abs.CrudModel;
import nl.stoux.posrs.Domain.Category;
import nl.stoux.posrs.Domain.Product;
import nl.stoux.posrs.Restful.Json.ModelSerializer;

/**
 * Created by Leon Stam on 22-9-2015.
 */
public class Globals {

    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Product.class, new ModelSerializer())
            .registerTypeAdapter(Category.class, new ModelSerializer())
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

}
