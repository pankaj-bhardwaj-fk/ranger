package com.flipkart.ranger.finder;

import com.flipkart.ranger.model.ServiceNode;
import com.flipkart.ranger.model.ServiceNodeSelector;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Created by pankaj.bhardwaj on 03/08/15.
 */
public class LeastActiveServiceNodeSelector<T> implements ServiceNodeSelector<T> {
    private ThreadLocal<Map<ServiceNode<T>, Integer>> requestMapper= new ThreadLocal<>();
    private ThreadLocal<ServiceNode<T> > serviceNodeThreadLocal = new ThreadLocal();
    private static Integer softThresold = 50;
    private static Integer hardThresold = 70;
    @Override
    public ServiceNode<T> select(List<ServiceNode<T>> serviceNodes) {
        ServiceNode<T> nodes = null;
        Boolean found = true;
        while(found){
            nodes = serviceNodes.get(ThreadLocalRandom.current().nextInt(serviceNodes.size()));
            if(requestMapper.get().containsKey(nodes)){
                if(requestMapper.get().get(nodes) < softThresold){
                    found = false;
                    requestMapper.get().put(nodes, requestMapper.get().get(nodes)+1);
                    serviceNodeThreadLocal.set(nodes);
                }
            }
        }
        return nodes;
    }

    @Override
    public void ack() {
        requestMapper.get().put(serviceNodeThreadLocal.get(), requestMapper.get().get(serviceNodeThreadLocal.get())+1);
    }
}
