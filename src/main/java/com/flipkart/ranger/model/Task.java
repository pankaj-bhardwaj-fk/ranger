package com.flipkart.ranger.model;

import java.util.List;

/**
 * Created by pankaj.bhardwaj on 19/08/15.
 */

public interface Task<T> {

    void initialize(List<Object> objectList);
    <R> R execute(ServiceNode<T> node);
}

