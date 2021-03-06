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

package com.hazelcast.map.impl.query;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.util.executor.ManagedExecutorService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.hazelcast.query.PagingPredicateAccessor.getNearestAnchorEntry;
import static com.hazelcast.util.FutureUtil.RETHROW_EVERYTHING;
import static com.hazelcast.util.FutureUtil.returnWithDeadline;
import static com.hazelcast.util.SortingUtil.getSortedSubList;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Implementation of the {@link PartitionScanExecutor} which executes the partition scan in a parallel-fashion
 * delegating to the underlying executor.
 */
public class ParallelPartitionScanExecutor implements PartitionScanExecutor {

    protected static final int QUERY_EXECUTION_TIMEOUT_MINUTES = 5;

    private final PartitionScanRunner partitionScanRunner;
    private final ManagedExecutorService executor;
    private final int timeoutInMinutes;

    public ParallelPartitionScanExecutor(
            PartitionScanRunner partitionScanRunner, ManagedExecutorService executor, int timeoutInMinutes) {
        this.partitionScanRunner = partitionScanRunner;
        this.executor = executor;
        this.timeoutInMinutes = timeoutInMinutes;
    }

    public ParallelPartitionScanExecutor(
            PartitionScanRunner partitionScanRunner, ManagedExecutorService executor) {
        this.partitionScanRunner = partitionScanRunner;
        this.executor = executor;
        this.timeoutInMinutes = QUERY_EXECUTION_TIMEOUT_MINUTES;
    }

    @Override
    public List<QueryableEntry> execute(String mapName, Predicate predicate, Collection<Integer> partitions) {
        try {
            List<QueryableEntry> result = runUsingPartitionScanWithoutPaging(mapName, predicate, partitions);
            if (predicate instanceof PagingPredicate) {
                Map.Entry<Integer, Map.Entry> nearestAnchorEntry = getNearestAnchorEntry((PagingPredicate) predicate);
                result = getSortedSubList(result, (PagingPredicate) predicate, nearestAnchorEntry);
            }
            return result;
        } catch (ExecutionException ex) {
            throw new HazelcastException(ex);
        } catch (InterruptedException ex) {
            throw new HazelcastException(ex);
        }
    }

    protected List<QueryableEntry> runUsingPartitionScanWithoutPaging(
            String name, Predicate predicate, Collection<Integer> partitions)
            throws InterruptedException, ExecutionException {

        List<Future<Collection<QueryableEntry>>> futures = new ArrayList<Future<Collection<QueryableEntry>>>(partitions.size());

        for (Integer partitionId : partitions) {
            Future<Collection<QueryableEntry>> future = runPartitionScanForPartition(name, predicate, partitionId);
            futures.add(future);
        }

        Collection<Collection<QueryableEntry>> returnedResults = waitForResult(futures, timeoutInMinutes);
        List<QueryableEntry> result = new ArrayList<QueryableEntry>();
        for (Collection<QueryableEntry> returnedResult : returnedResults) {
            result.addAll(returnedResult);
        }
        return result;
    }

    protected Future<Collection<QueryableEntry>> runPartitionScanForPartition(String name, Predicate predicate, int partitionId) {
        QueryPartitionCallable task = new QueryPartitionCallable(name, predicate, partitionId);
        return executor.submit(task);
    }

    private static <T> Collection<Collection<T>> waitForResult(List<Future<Collection<T>>> lsFutures, int timeoutInMinutes) {
        return returnWithDeadline(lsFutures, timeoutInMinutes, MINUTES, RETHROW_EVERYTHING);
    }

    private final class QueryPartitionCallable implements Callable<Collection<QueryableEntry>> {
        protected final int partition;
        protected final String name;
        protected final Predicate predicate;

        private QueryPartitionCallable(String name, Predicate predicate, int partitionId) {
            this.name = name;
            this.predicate = predicate;
            this.partition = partitionId;
        }

        @Override
        public Collection<QueryableEntry> call() throws Exception {
            return partitionScanRunner.run(name, predicate, partition);
        }
    }

}
