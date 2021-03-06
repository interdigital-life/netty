/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.netty.test.udt.bench.xfer;

import com.google.caliper.Param;
import io.netty.test.udt.bench.BenchXfer;
import io.netty.test.udt.util.CaliperRunner;
import io.netty.test.udt.util.TrafficControl;

import java.util.List;

/**
 * perform two way native TCP socket send/recv
 */
public class TcpNative extends BenchXfer {

    @Param
    private volatile int latency;
    @Param
    private volatile int message;
    @Param
    private volatile int duration;

    protected static List<String> latencyValues() {
        return BenchXfer.latencyList();
    }

    protected static List<String> messageValues() {
        return BenchXfer.messageList();
    }

    protected static List<String> durationValues() {
        return BenchXfer.durationList();
    }

    public static void main(final String[] args) throws Exception {
        CaliperRunner.execute(TcpNative.class);
    }

    public void timeRun(final int reps) throws Exception {
        log.info("init");

        TrafficControl.delay(latency);

        TrafficControl.delay(0);

        log.info("done");
    }
}
