package com.flipkart.ranger.finder;

import com.flipkart.ranger.model.ServiceNode;
import com.flipkart.ranger.model.ServiceNodeSelector;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by pankaj.bhardwaj on 19/08/15.
 */

public class LeastActiveServiceNodeSelector<T> implements ServiceNodeSelector<T> {
    private ThreadLocal<Map<ServiceNode<T>, Integer>> requestMapper= new ThreadLocal<>();
    private ThreadLocal<ServiceNode<T> > serviceNodeThreadLocal = new ThreadLocal();
    private static final ThreadLocal<Integer> count =  new ThreadLocal<Integer>() {
                @Override protected Integer initialValue() {
                    return 0;
                }
            };

    private static Integer softThresold = 50;
    private static Integer hardThresold = 70;
    private static Integer sigmaLoad = 100;
    @Override
    public ServiceNode<T> select(List<ServiceNode<T>> serviceNodes) {
        ServiceNode<T> nodes = null;
        Boolean found = true;
        while(found){
            nodes = serviceNodes.get(ThreadLocalRandom.current().nextInt(serviceNodes.size()));
            if(requestMapper.get().containsKey(nodes)){
                if(requestMapper.get().get(nodes) < softThresold || requestMapper.get().get(nodes) < hardThresold && count.get() < sigmaLoad){
                    found = false;
                    Integer value = requestMapper.get().get(nodes)+1;
                    requestMapper.get().put(nodes, value);
                    serviceNodeThreadLocal.set(nodes);
                    if(value +1 >= softThresold){
                        count.set(count.get()+1);
                    }
                }
            }
        }
        return nodes;
    }

    @Override
    public void ack() {
        Boolean flag = false;
        if(requestMapper.get().get(serviceNodeThreadLocal.get()) >= softThresold){
            flag = true;
        }
        requestMapper.get().put(serviceNodeThreadLocal.get(), requestMapper.get().get(serviceNodeThreadLocal.get())+1);
        if(requestMapper.get().get(serviceNodeThreadLocal.get()) < softThresold && flag){
            count.set(count.get() - 1);
        }

    }
}

