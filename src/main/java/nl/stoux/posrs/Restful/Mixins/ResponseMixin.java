package nl.stoux.posrs.Restful.Mixins;

import nl.stoux.posrs.Domain.Abs.CrudModel;
import nl.stoux.posrs.Util.Globals;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Leon Stam on 22-9-2015.
 */
public interface ResponseMixin {

    default Response okResponse(Object toJsonObject) {
        return okResponse(Globals.gson.toJson(toJsonObject));
    }

    default Response okResponse(String json) {
        return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
    }

    default Response badResponse(Response.Status code, String error) {
        return Response.status(code).entity(error).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
