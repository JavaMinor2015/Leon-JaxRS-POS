package nl.stoux.posrs.Restful.Services.Abs;

import nl.stoux.posrs.Domain.Abs.CrudModel;
import nl.stoux.posrs.Util.Globals;
import nl.stoux.posrs.Util.Methods;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Created by Leon Stam on 22-9-2015.
 */
@Produces("application/json")
public abstract class CrudService<Identifier, ObjectType extends CrudModel<Identifier>> extends BaseService<Identifier, ObjectType> {

    @GET
    public Response getAll() {
        return okResponse(getMap().values());
    }

    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") Identifier id) {
        return useItem(id, this::okResponse);
    }

    @PUT
    @Path("/{id}")
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
        ObjectType o = Globals.gson.fromJson(json, createClass());
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
