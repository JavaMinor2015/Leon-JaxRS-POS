package nl.stoux.posrs.Domain;

import lombok.*;
import nl.stoux.posrs.Domain.Abs.CrudModel;

import java.util.Set;

/**
 * Created by Leon Stam on 22-9-2015.
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product implements CrudModel<Integer> {

    public static final String URL = "/products";

    @Setter(AccessLevel.NONE) private Integer id;
    @Setter(AccessLevel.NONE) private String name;

    private double price;
    private Category category;

    private Set<String> barcodes;

}
