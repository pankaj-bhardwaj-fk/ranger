package com.flipkart.ranger.model;

import com.flipkart.ranger.finder.ServiceFinder;

/**
 * Created by pankaj.bhardwaj on 19/08/15.
 */
public interface Task<T> {
    <R> R execute(ServiceNode<T> node);
}
