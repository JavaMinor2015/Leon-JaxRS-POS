package nl.stoux.posrs.Restful.Services.Abs;

import nl.stoux.posrs.Domain.Abs.CrudModel;
import nl.stoux.posrs.Restful.Json.PaginationModel;
import nl.stoux.posrs.Util.Globals;
import nl.stoux.posrs.Util.Methods;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Leon Stam on 22-9-2015.
 */
@Produces("application/json")
public abstract class CrudService<Identifier extends Comparable<Identifier>, ObjectType extends CrudModel<Identifier>> extends BaseService<Identifier, ObjectType> {

    @GET
    public Response getAll(@DefaultValue("0") @QueryParam("index") final int index,
                           @DefaultValue("5") @QueryParam("limit") final int limit) {
        //Get the results
        List<Identifier> ids = new ArrayList<>(getMap().keySet());
        Collections.sort(ids);
        List<ObjectType> objects = ids.stream()
                .skip(index)
                .limit(limit)
                .map(identifier -> getMap().get(identifier))
                .collect(Collectors.toList());

        String baseURL = Methods.getModelURL(createClass());
        //Build the URLs
        String prevURL = null;
        if (index != 0) {
            int prevIndex = index - limit;
            if (prevIndex < 0) {
                prevIndex = 0;
            }
            prevURL = buildListURL(baseURL, prevIndex, limit);
        }

        String nextURL = null;
        int mapCount = getMap().size();
        if (index + limit < mapCount) {
            nextURL = buildListURL(baseURL, index + limit, limit);
        }

        return okResponse(new PaginationModel(prevURL, nextURL, objects));
    }

    private String buildListURL(String baseURL, int index, int limit) {
        return baseURL + "?index=" + index + "&limit=" + limit;
    }


    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") Identifier id) {
        return useItem(id, this::okResponse);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response updateOne(@PathParam("id") Identifier id, @FormParam("values") String json) {
        return updateFields(id, json);
    }

    @DELETE
    @Path("/id")
    public Response deleteOne(@PathParam("id") Identifier id) {
        return useItem(id, objectType -> {
            getMap().remove(id);
            return okResponse(Methods.asMap("result", "ok"));
        });
    }

    @POST
    public Response createOne(@FormParam("values") String json) {
        ObjectType o = Globals.getGson().fromJson(json, createClass());
        Identifier id = o.getId();

        Map<Identifier, ObjectType> map = getMap();
        if (map.containsKey(id)) {
            return badResponse(Response.Status.CONFLICT, "Already an item with this ID");
        } else {
            map.put(id, o);
            return okResponse(Methods.asMap("result", "ok", "id", id));
        }
    }

    public abstract Class<ObjectType> createClass();

}
