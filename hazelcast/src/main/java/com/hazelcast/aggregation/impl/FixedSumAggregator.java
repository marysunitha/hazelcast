/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.aggregation.impl;

import com.hazelcast.aggregation.Aggregator;

import java.util.Map;

public class FixedSumAggregator<K, V> extends AbstractAggregator<Long, K, V> {

    private long sum;

    public FixedSumAggregator() {
        super();
    }

    public FixedSumAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulate(Map.Entry<K, V> entry) {
        Number extractedValue = (Number) extract(entry);
        sum += extractedValue.longValue();
    }

    @Override
    public void combine(Aggregator aggregator) {
        FixedSumAggregator longSumAggregator = (FixedSumAggregator) aggregator;
        this.sum += longSumAggregator.sum;
    }

    @Override
    public Long aggregate() {
        return sum;
    }

}
