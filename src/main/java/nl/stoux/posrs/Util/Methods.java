package nl.stoux.posrs.Util;

import nl.stoux.posrs.Domain.Abs.CrudModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leon Stam on 22-9-2015.
 */
public class Methods {

    /**
     * Map arguments
     * @param args Key -> Value pairs
     * @return the map
     */
    public static Map<Object, Object> asMap(Object... args) {
        HashMap<Object, Object> map = new HashMap<>();
        for (int i = 0; i + 1< args.length; i = i + 2) {
            map.put(args[i], args[i + 1]);
        }
        return map;
    }

    /**
     * Get the base URL of a CrudModel
     * @param model The model's class
     * @return The base URL
     */
    public static String getModelURL(Class<? extends CrudModel> model) {
        try {
            return (String) model.getDeclaredField("URL").get(null);
        } catch (Exception e) {
            System.out.println("Failed to get URL: " + e.getMessage());
            return "";
        }
    }

}
