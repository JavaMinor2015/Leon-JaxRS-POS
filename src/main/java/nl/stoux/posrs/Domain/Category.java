package nl.stoux.posrs.Domain;

import lombok.*;
import nl.stoux.posrs.Domain.Abs.CrudModel;

/**
 * Created by Leon Stam on 22-9-2015.
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category implements CrudModel<Integer> {

    public static final String URL = "/categories";

    @Setter(AccessLevel.NONE) private Integer id;
    private String category;



}
