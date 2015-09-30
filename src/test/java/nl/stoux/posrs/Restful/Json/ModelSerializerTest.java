package nl.stoux.posrs.Restful.Json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nl.stoux.posrs.Domain.Product;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by Leon Stam on 30-9-2015.
 */
public class ModelSerializerTest {

    private Gson gson;
    private ModelSerializer<Product> productModelSerializer;

    @Before
    public void setup() {
        productModelSerializer = new ModelSerializer<>(Product.class, Product.URL);
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(Product.class, productModelSerializer)
                .create();
    }

    @Test
    public void serializeWithoutSubModel() throws Exception {
        //Arrange
        Product p = new Product(1, "Huh", 12.0, null, new HashSet<>(Arrays.asList("1", "2")));

        //Act
        JsonObject o = gson.toJsonTree(p).getAsJsonObject();
        System.out.println(gson.toJson(p));

        //Assert
        //=> Contains correct values
        for (String s : Arrays.asList("url", "id", "name", "price", "barcodes")) {
            assertTrue(o.has(s));
        }
        assertTrue(!o.has("category"));

        //=> Check correct values
        assertEquals(1, o.get("id").getAsInt());
        assertEquals("/products/1", o.get("url").getAsString());
        assertEquals("Huh", o.get("name").getAsString());
        assertEquals(12.0, o.get("price").getAsDouble(), 0);

        JsonArray barcodes = o.get("barcodes").getAsJsonArray();
        assertEquals(2, barcodes.size());
        assertEquals("1", barcodes.get(0).getAsString());
        assertEquals("2", barcodes.get(1).getAsString());
    }

    @Ignore("No repository available yet to test with submodels")
    @Test
    public void serializeWithSubModel() throws Exception {

    }

    @Test
    public void deserializeWithoutSubModel() throws Exception {
        //Arrange
        String json = gson.toJson(
                new Product(1, "Huh", 12.0, null, new HashSet<>(Arrays.asList("1", "2")))
        );

        //Act
        Product p = gson.fromJson(json, Product.class);

        //Assert
        assertEquals(Integer.valueOf(1), p.getId());
        assertEquals("Huh", p.getName());
        assertEquals(12.0, p.getPrice(), 0.0001);
        assertNull(p.getCategory());

        assertThat(p.getBarcodes(), hasItem("1"));
        assertThat(p.getBarcodes(), hasItem("2"));
    }

    @Ignore("No repository available yet to test with submodels")
    @Test
    public void deserializeWithSubModel() throws Exception {

    }
}