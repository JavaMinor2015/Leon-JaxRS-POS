package nl.stoux.posrs.Restful.Services;

import nl.stoux.posrs.Domain.Category;
import nl.stoux.posrs.Restful.Services.Abs.CrudService;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leon Stam on 22-9-2015.
 */
@Path(Category.URL)
@Produces("application/json")
public class CategoryService extends CrudService<Integer, Category> {

    private static HashMap<Integer, Category> categories = new HashMap<>();

    @Override
    protected Map<Integer, Category> getMap() {
        return CategoryService.categories;
    }

    @Override
    public Class<Category> createClass() {
        return Category.class;
    }
}
