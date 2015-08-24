package com.flipkart.ranger.finder;

import com.flipkart.ranger.model.ServiceNode;
import com.flipkart.ranger.model.ServiceNodeSelector;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by pankaj.bhardwaj on 19/08/15.
 */

public class LeastActiveServiceNodeSelector<T> implements ServiceNodeSelector<T> {
    ThreadLocal<Map<String, Integer>> nodeCounterMap = new ThreadLocal<Map<String, Integer>>() {
        @Override
        protected Map<String, Integer> initialValue() {
            return Maps.newHashMap();
        }
    };
    private ThreadLocal<ServiceNode<T>> serviceNodeThreadLocal = new ThreadLocal();

    private static final ThreadLocal<Integer> count = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
    private static ThreadLocal<Double> mean = new ThreadLocal<Double>(){
        @Override
        protected Double initialValue() {
            return 0.0;
        }
    };
    private static ThreadLocal<Double> standard_deviation = new ThreadLocal<Double>(){
        @Override
        public Double initialValue(){
            return 0.0;
        }
    };
    private static ThreadLocal<Long> ThreadCount = new ThreadLocal<Long>(){
        @Override
        public Long initialValue(){
            return 0L;
        }
    };

    @Override
    public ServiceNode<T> select(List<ServiceNode<T>> serviceNodes) {
        ServiceNode<T> nodes = null;
        Boolean found = true;
        ThreadCount.set(ThreadCount.get() + 1);
        if(ThreadCount.get() % 10 ==0 && ThreadCount.get() != 0){
            int totalActiveNodes = 0, x_2=0, x=0;
            for(ServiceNode node : serviceNodes){
                if(nodeCounterMap.get().containsKey(node.getHost())){
                    totalActiveNodes++;
                    x = x + nodeCounterMap.get().get(node.getHost());
                    x_2 = x_2 + nodeCounterMap.get().get(node.getHost()) * nodeCounterMap.get().get(node.getHost());
                }
            }
            Double mu = (double) (x/totalActiveNodes);
            mean.set(mu);

            Double dev = Math.sqrt( (double)(x_2/totalActiveNodes) - mu * mu);
            standard_deviation.set(dev);
        }
        Integer counter = 0;
        while (found) {
            nodes = serviceNodes.get(ThreadLocalRandom.current().nextInt(serviceNodes.size()));
            if (nodeCounterMap.get().containsKey(nodes.getHost())) {
                if ((nodeCounterMap.get().get(nodes.getHost()) < mean.get()) || ((nodeCounterMap.get().get(nodes.getHost()) < (mean.get() + (2 * standard_deviation.get()))) && (count.get() < (((2.0 * serviceNodes.size()) / 3)))) || counter > 3) {
                    found = false;
                    Integer value = nodeCounterMap.get().get(nodes.getHost()) + 1;
                    nodeCounterMap.get().put(nodes.getHost(), value);
                    serviceNodeThreadLocal.set(nodes);
                    if (value + 1 >= mean.get()) {
                        count.set(count.get() + 1);
                    }
                } else {
                  counter ++;
                }
            }else{
                nodeCounterMap.get().put(nodes.getHost(), 1);
                serviceNodeThreadLocal.set(nodes);
                found = false;
            }
        }
        return nodes;
    }

    @Override
    public void ack() {
        Boolean flag = false;
        if (nodeCounterMap.get().get(serviceNodeThreadLocal.get().getHost()) >= mean.get()) {
            flag = true;
        }
        nodeCounterMap.get().put(serviceNodeThreadLocal.get().getHost(), nodeCounterMap.get().get(serviceNodeThreadLocal.get().getHost()) - 1);
        if (nodeCounterMap.get().get(serviceNodeThreadLocal.get().getHost()) < mean.get() && flag) {
            count.set(count.get() - 1);
        }

    }
}

