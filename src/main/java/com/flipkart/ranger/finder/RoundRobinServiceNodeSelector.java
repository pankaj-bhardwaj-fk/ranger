/**
 * Copyright 2015 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.ranger.finder;

import com.flipkart.ranger.model.ServiceNode;
import com.flipkart.ranger.model.ServiceNodeSelector;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinServiceNodeSelector<T> implements ServiceNodeSelector<T> {
    private static final ThreadLocal<Integer> index =
            new ThreadLocal<Integer>() {
                @Override protected Integer initialValue() {
                    return 0;
                }
            };

    @Override
    public ServiceNode<T> select(List<ServiceNode<T>> serviceNodes) {
        index.set((index.get() + 1) % serviceNodes.size());
        return serviceNodes.get(index.get());
    }

    @Override
    public void ack() {

    }
}
