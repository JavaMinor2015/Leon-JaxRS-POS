package nl.stoux.posrs.Restful.Services.Abs;

import nl.stoux.posrs.Restful.Mixins.ResponseMixin;
import nl.stoux.posrs.Util.Globals;
import nl.stoux.posrs.Util.Methods;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Leon Stam on 22-9-2015.
 */
public abstract class BaseService<Identifier, ObjectType> implements ResponseMixin {

    public static final String FIELD_NOT_FOUND = "NOT_FOUND";
    public static final String FIELD_FINAL = "NOT_MODIFIABLE";
    public static final String FIELD_UPDATED = "UPDATED";
    public static final String FIELD_FAILED = "UPDATE_FAILED";

    /** The default not found string */
    protected String notFoundString = "Not found: {s}";

    /**
     * Try to find an item by it's identifier and use it
     * If the item is not found it will return a BAD_REQUEST response
     * @param id ID of the item
     * @param action The action to execute if an item is found
     * @return A response
     */
    protected Response useItem(Identifier id, Function<ObjectType, Response> action) {
        id = modifyIdentifier(id);
        Map<Identifier, ObjectType> map = getMap();
        if (!map.containsKey(id)) {
            return badResponse(Response.Status.BAD_REQUEST, String.format(notFoundString, id));
        }

        ObjectType o = map.get(id);
        return action.apply(o);
    }

    /**
     * Try to find an item by using a custom find method and use it
     * If the item is not found it will return a BAD_REQUEST response
     * @param id ID of the item
     * @param findMethod Method used to find the object in the map
     * @param action The action to take
     * @return A response
     */
    protected <SearchType> Response useItem(SearchType searchFor,
                               BiFunction<Stream<ObjectType>, SearchType, Optional<ObjectType>> findMethod,
                               Function<ObjectType, Response> action) {

        Map<Identifier, ObjectType> map = getMap();
        Optional<ObjectType> optObject = findMethod.apply(map.values().parallelStream(), searchFor);
        if (!optObject.isPresent()) {
            return badResponse(Response.Status.BAD_REQUEST, String.format(notFoundString, searchFor));
        } else {
            return action.apply(optObject.get());
        }
    }

    /**
     * Function that allows modification of the identifier to generalize it as Key
     * @param base The identifier
     * @return The modified identifier
     */
    protected Identifier modifyIdentifier(Identifier base) {
        return base;
    }

    public abstract Map<Identifier, ObjectType> getMap();

    /**
     * Update fields of an object
     * @param id ID of the object
     * @param newValuesAsJson The values still as string
     * @return A response
     */
    protected Response updateFields(Identifier id, final String newValuesAsJson) {
        return useItem(id, objectType -> {
            HashMap<?, ?> newValues = Globals.getGson().fromJson(newValuesAsJson, HashMap.class);
            HashMap<String, String> updatedFields = updateFields(objectType, newValues);
            return Response.ok(Methods.asMap("result", "ok", "modified", updatedFields)).build();
        });
    }

    /**
     * Update fields of an object
     * @param objectType The object
     * @param newValues Map with new values
     * @return Fields that have been updated
     */
    private HashMap<String, String> updateFields(ObjectType objectType, HashMap<?, ?> newValues) {
        HashMap<String, String> updatedFields = new HashMap<>();

        Class<?> clazz = objectType.getClass();
        newValues.forEach((keyObject, value) -> {
            String key = keyObject.toString();
            try {
                Field f = clazz.getDeclaredField(key);
                if (f == null || Modifier.isFinal(f.getModifiers())) {
                    updatedFields.put(key, (f == null ? FIELD_NOT_FOUND : FIELD_FINAL));
                    return;
                }

                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }

                f.set(objectType, value);
                updatedFields.put(key, FIELD_UPDATED);
            } catch (Exception e) {
                System.out.println("Woops.. " + e.getMessage());
                e.printStackTrace();
                updatedFields.put(key, FIELD_FAILED);
            }
        });

        return updatedFields;
    }

}
