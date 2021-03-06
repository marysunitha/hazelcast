package com.hazelcast.client.executor;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.executor.tasks.CancellationAwareTask;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;

@RunWith(HazelcastParallelClassRunner.class)
@Category({QuickTest.class, ParallelTest.class})
@Ignore
/*
 * This test is failing because of order problem between actual invoke and cancel.
 * For random and partition, the reason of broken order is also unknown to me (@sancar )
 * For submit to member, it is because we do not have order guarantee in the first place.
 * and when there is partition movement, we can not is partition id since tasks will not move with partitions
 */
public class ClientExecutorServiceCancelTest extends HazelcastTestSupport {

    public static final int SLEEP_TIME = 1000000;
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();
    private HazelcastInstance server1;
    private HazelcastInstance server2;

    @After
    public void tearDown() {
        hazelcastFactory.terminateAll();
    }

    @Before
    public void setup() {
        server1 = hazelcastFactory.newHazelcastInstance();
        server2 = hazelcastFactory.newHazelcastInstance();
    }

    private HazelcastInstance createClient(boolean smartRouting) {
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().setSmartRouting(smartRouting);
        return hazelcastFactory.newHazelcastClient(config);
    }

    @Test(expected = CancellationException.class)
    public void testCancel_submitRandom_withSmartRouting() throws ExecutionException, InterruptedException {
        testCancel_submitRandom(true);
    }

    @Test(expected = CancellationException.class)
    public void testCancel_submitRandom_withDummyRouting() throws ExecutionException, InterruptedException {
        testCancel_submitRandom(false);
    }

    private void testCancel_submitRandom(boolean smartRouting) throws ExecutionException, InterruptedException {
        HazelcastInstance client = createClient(smartRouting);

        IExecutorService executorService = client.getExecutorService(randomString());
        Future<Boolean> future = executorService.submit(new CancellationAwareTask(SLEEP_TIME));
        boolean cancelled = future.cancel(true);
        assertTrue(cancelled);
        future.get();
    }

    @Test(expected = CancellationException.class)
    @Ignore
    public void testCancel_submitToMember1_withSmartRouting() throws ExecutionException, InterruptedException {
        testCancel_submitToMember(true, server1.getCluster().getLocalMember());
    }

    @Test(expected = CancellationException.class)
    @Ignore
    public void testCancel_submitToMember2_withSmartRouting() throws ExecutionException, InterruptedException {
        testCancel_submitToMember(true, server2.getCluster().getLocalMember());
    }

    @Test(expected = CancellationException.class)
    @Ignore
    public void testCancel_submitToMember1_withDummyRouting() throws ExecutionException, InterruptedException {
        testCancel_submitToMember(false, server1.getCluster().getLocalMember());
    }

    @Test(expected = CancellationException.class)
    @Ignore
    public void testCancel_submitToMember2_withDummyRouting() throws ExecutionException, InterruptedException {
        testCancel_submitToMember(false, server2.getCluster().getLocalMember());
    }

    private void testCancel_submitToMember(boolean smartRouting, Member member) throws ExecutionException, InterruptedException {

        HazelcastInstance client = createClient(smartRouting);

        IExecutorService executorService = client.getExecutorService(randomString());
        Future<Boolean> future = executorService.submitToMember(new CancellationAwareTask(SLEEP_TIME), member);
        boolean cancelled = future.cancel(true);
        assertTrue(cancelled);
        future.get();
    }

    @Test(expected = CancellationException.class)
    public void testCancel_submitToKeyOwner_withSmartRouting() throws ExecutionException, InterruptedException {
        testCancel_submitToKeyOwner(true);
    }

    @Test(expected = CancellationException.class)
    public void testCancel_submitToKeyOwner_withDummyRouting() throws ExecutionException, InterruptedException {
        testCancel_submitToKeyOwner(false);
    }

    private void testCancel_submitToKeyOwner(boolean smartRouting) throws ExecutionException, InterruptedException {
        HazelcastInstance client = createClient(smartRouting);

        IExecutorService executorService = client.getExecutorService(randomString());
        Future<Boolean> future = executorService.submitToKeyOwner(new CancellationAwareTask(SLEEP_TIME), randomString());
        boolean cancelled = future.cancel(true);
        assertTrue(cancelled);
        future.get();
    }
}
