package nl.stoux.posrs.Restful.Json;

import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Created by Leon Stam on 23-9-2015.
 */
@AllArgsConstructor
public class PaginationModel {

    private String prev;
    private String next;
    private List results;

}
