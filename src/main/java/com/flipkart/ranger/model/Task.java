package com.flipkart.ranger.model;

/**
 * Created by pankaj.bhardwaj on 19/08/15.
 */

public interface Task<T> {
    <R> R execute(ServiceNode<T> node);
}

