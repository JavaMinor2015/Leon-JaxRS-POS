package nl.stoux.posrs.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.stoux.posrs.Domain.Abs.CrudModel;
import nl.stoux.posrs.Domain.Annotations.SubModel;

import java.util.Set;

/**
 * Created by Leon Stam on 22-9-2015.
 */
@Getter @Setter
@AllArgsConstructor
public class Product implements CrudModel<Integer> {

    public static final String URL = "/products";

    private final Integer id;
    private final String name;
    private double price;
    @SubModel private Category category;

    private Set<String> barcodes;

}
