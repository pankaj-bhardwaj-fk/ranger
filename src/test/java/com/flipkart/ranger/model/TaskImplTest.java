package com.flipkart.ranger.model;

import java.util.List;

/**
 * Created by pankaj.bhardwaj on 19/08/15.
 */
public class TaskImplTest<T> implements Task<T> {
    @Override
    public void initialize(List<Object> objectList) {

    }

    @Override
    public <R> R execute(ServiceNode<T> node) {
        return null;
    }
}
