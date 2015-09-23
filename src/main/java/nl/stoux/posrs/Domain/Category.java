package nl.stoux.posrs.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.stoux.posrs.Domain.Abs.CrudModel;

/**
 * Created by Leon Stam on 22-9-2015.
 */
@Getter @Setter
@AllArgsConstructor
public class Category implements CrudModel<Integer> {

    public static final String URL = "/categories";

    private final Integer id;
    private String category;



}
