package nl.stoux.posrs.Domain;

import lombok.*;
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

}
