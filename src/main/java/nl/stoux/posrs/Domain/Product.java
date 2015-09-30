package nl.stoux.posrs.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.stoux.posrs.Domain.Abs.CrudModel;

import java.util.Set;

/**
 * Created by Leon Stam on 22-9-2015.
 */
@Getter @Setter
@AllArgsConstructor
public class Product implements CrudModel<Integer> {

    public static final String URL = "/products";

    final private Integer id;
    final private String name;

    private double price;
    private Category category;

    private Set<String> barcodes;

    public Product(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
