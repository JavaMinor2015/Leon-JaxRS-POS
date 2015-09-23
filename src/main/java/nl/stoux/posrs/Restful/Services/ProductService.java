package nl.stoux.posrs.Restful.Services;

import nl.stoux.posrs.Domain.Product;
import nl.stoux.posrs.Restful.Services.Abs.CrudService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Leon Stam on 22-9-2015.
 */
@Path(Product.URL)
public class ProductService extends CrudService<Integer, Product> {

    private static HashMap<Integer, Product> productMap = new HashMap<>();
    static {
        HashSet<String> s = new HashSet<>();
        s.add("A1");
        s.add("A2");
        Product p = new Product(1, "A", 12.4, null, s);
        productMap.put(p.getId(), p);
    }

    @GET
    @Path("/barcode/{barcode}")
    public Response byBarcode(@PathParam("barcode") String barcode) {
        return useItem(
                barcode,
                (stream, search) -> stream.filter(product -> product.getBarcodes().contains(search)).findAny(),
                this::okResponse
        );
    }


    @Override
    public Map<Integer, Product> getMap() {
        return ProductService.productMap;
    }

    @Override
    public Class<Product> createClass() {
        return Product.class;
    }
}
