package com.flipkart.ranger.finder;

import com.flipkart.ranger.model.ServiceNode;
import com.flipkart.ranger.model.ServiceNodeSelector;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by pankaj.bhardwaj on 19/08/15.
 */
public class LeastActiveServiceLinear<T> implements ServiceNodeSelector<T>  {
    ThreadLocal<Map<String, Integer>> mapThreadLocal = new ThreadLocal<Map<String, Integer>>() {
        @Override
        protected Map<String, Integer> initialValue() {
            return Maps.newHashMap();
        }
    };
    ThreadLocal<ServiceNode<T> > serviceNodeThreadLocal = new ThreadLocal<ServiceNode<T>>() {};

    @Override
    public ServiceNode<T> select(List<ServiceNode<T>> serviceNodes) {
        ServiceNode<T> node = null;
        Integer minConection = -1;

        for(ServiceNode<T> iterServiceNode : serviceNodes){

                if (!mapThreadLocal.get().containsKey(iterServiceNode.getHost())) {
                    node = iterServiceNode;
                    mapThreadLocal.get().put(iterServiceNode.getHost(), 1);
                    serviceNodeThreadLocal.set(node);
                    return node;
                } else {
                    if (mapThreadLocal.get().get(iterServiceNode.getHost()) < minConection || (minConection == -1)) {
                        minConection = (Integer) mapThreadLocal.get().get(iterServiceNode.getHost());
                        node = iterServiceNode;
                    }
                }

        }
        mapThreadLocal.get().put(node.getHost(), (Integer) mapThreadLocal.get().get(node.getHost())+1);
        serviceNodeThreadLocal.set(node);
        return node;
    }

    @Override
    public void ack() {
        mapThreadLocal.get().put(serviceNodeThreadLocal.get().getHost(), mapThreadLocal.get().get(serviceNodeThreadLocal.get().getHost()) - 1);
    }
}
