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

package com.hazelcast.aggregation;

import com.hazelcast.aggregation.impl.BigDecimalAverageAggregator;
import com.hazelcast.aggregation.impl.BigDecimalSumAggregator;
import com.hazelcast.aggregation.impl.BigIntegerAverageAggregator;
import com.hazelcast.aggregation.impl.BigIntegerSumAggregator;
import com.hazelcast.aggregation.impl.CountAggregator;
import com.hazelcast.aggregation.impl.DistinctValuesAggregator;
import com.hazelcast.aggregation.impl.DoubleAverageAggregator;
import com.hazelcast.aggregation.impl.DoubleSumAggregator;
import com.hazelcast.aggregation.impl.FixedSumAggregator;
import com.hazelcast.aggregation.impl.FloatingPointSumAggregator;
import com.hazelcast.aggregation.impl.IntegerAverageAggregator;
import com.hazelcast.aggregation.impl.IntegerSumAggregator;
import com.hazelcast.aggregation.impl.LongAverageAggregator;
import com.hazelcast.aggregation.impl.LongSumAggregator;
import com.hazelcast.aggregation.impl.MaxAggregator;
import com.hazelcast.aggregation.impl.MinAggregator;
import com.hazelcast.aggregation.impl.NumberAverageAggregator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

/**
 * A utility class to create basic {@link com.hazelcast.aggregation.Aggregator} instances.
 *
 * Min/Max/Average aggregators are type specific, so an integerAvg() aggregator expects all elements to be integers.
 * There is no conversion executed while accumulating, so if there is any other type met an exception will be thrown.
 *
 * In order to operate on a generic Number type use the fixedPointSum(), floatingPointSum() and numberAvg() aggregators.
 * All of them will convert the given number to either Long or Double during the accumulation phase.
 * It will result in a lot of allocations since each number has to be converted, but it enables the user
 * to operate on the whole family of numbers. It is especially useful if the numbers given to the aggregators
 * may not be of one type only.
 *
 * The attributePath given in the factory method allows the aggregator to operate on the value extracted by navigating
 * to the given attributePath on each object that has been returned from a query.
 * The attribute path may be simple, e.g. "name", or nested "address.city".
 *
 * @since 3.8
 */
@SuppressWarnings({"checkstyle:methodcount", "checkstyle:classdataabstractioncoupling"})
public final class Aggregators {

    private Aggregators() {
    }

    public static <K, V> Aggregator<Long, K, V> count() {
        return new CountAggregator<K, V>();
    }

    public static <K, V> Aggregator<Long, K, V> count(String attributePath) {
        return new CountAggregator<K, V>(attributePath);
    }

    public static <R, K, V> Aggregator<Set<R>, K, V> distinct() {
        return new DistinctValuesAggregator<R, K, V>();
    }

    public static <R, K, V> Aggregator<Set<R>, K, V> distinct(String attributePath) {
        return new DistinctValuesAggregator<R, K, V>(attributePath);
    }

    //
    // average aggregators
    //
    public static <K, V> Aggregator<BigDecimal, K, V> bigDecimalAvg() {
        return new BigDecimalAverageAggregator<K, V>();
    }

    public static <K, V> Aggregator<BigDecimal, K, V> bigDecimalAvg(String attributePath) {
        return new BigDecimalAverageAggregator<K, V>(attributePath);
    }

    public static <K, V> Aggregator<BigDecimal, K, V> bigIntegerAvg() {
        return new BigIntegerAverageAggregator<K, V>();
    }

    public static <K, V> Aggregator<BigDecimal, K, V> bigIntegerAvg(String attributePath) {
        return new BigIntegerAverageAggregator<K, V>(attributePath);
    }

    public static <K, V> Aggregator<Double, K, V> doubleAvg() {
        return new DoubleAverageAggregator<K, V>();
    }

    public static <K, V> Aggregator<Double, K, V> doubleAvg(String attributePath) {
        return new DoubleAverageAggregator<K, V>(attributePath);
    }

    public static <K, V> Aggregator<Double, K, V> integerAvg() {
        return new IntegerAverageAggregator<K, V>();
    }

    public static <K, V> Aggregator<Double, K, V> integerAvg(String attributePath) {
        return new IntegerAverageAggregator<K, V>(attributePath);
    }

    public static <K, V> Aggregator<Double, K, V> longAvg() {
        return new LongAverageAggregator<K, V>();
    }

    public static <K, V> Aggregator<Double, K, V> longAvg(String attributePath) {
        return new LongAverageAggregator<K, V>(attributePath);
    }

    public static <K, V> Aggregator<Double, K, V> numberAvg() {
        return new NumberAverageAggregator<K, V>();
    }

    public static <K, V> Aggregator<Double, K, V> numberAvg(String attributePath) {
        return new NumberAverageAggregator<K, V>(attributePath);
    }

    //
    // max aggregators
    //
    public static <K, V> Aggregator<BigDecimal, K, V> bigDecimalMax() {
        return new MaxAggregator<BigDecimal, K, V>();
    }

    public static <K, V> Aggregator<BigDecimal, K, V> bigDecimalMax(String attributePath) {
        return new MaxAggregator<BigDecimal, K, V>(attributePath);
    }

    public static <K, V> Aggregator<BigInteger, K, V> bigIntegerMax() {
        return new MaxAggregator<BigInteger, K, V>();
    }

    public static <K, V> Aggregator<BigInteger, K, V> bigIntegerMax(String attributePath) {
        return new MaxAggregator<BigInteger, K, V>(attributePath);
    }

    public static <K, V> Aggregator<Double, K, V> doubleMax() {
        return new MaxAggregator<Double, K, V>();
    }

    public static <K, V> Aggregator<Double, K, V> doubleMax(String attributePath) {
        return new MaxAggregator<Double, K, V>(attributePath);
    }

    public static <K, V> Aggregator<Integer, K, V> integerMax() {
        return new MaxAggregator<Integer, K, V>();
    }

    public static <K, V> Aggregator<Integer, K, V> integerMax(String attributePath) {
        return new MaxAggregator<Integer, K, V>(attributePath);
    }

    public static <K, V> Aggregator<Long, K, V> longMax() {
        return new MaxAggregator<Long, K, V>();
    }

    public static <K, V> Aggregator<Long, K, V> longMax(String attributePath) {
        return new MaxAggregator<Long, K, V>(attributePath);
    }

    public static <R extends Comparable, K, V> Aggregator<R, K, V> comparableMax() {
        return new MaxAggregator<R, K, V>();
    }

    public static <R extends Comparable, K, V> Aggregator<R, K, V> comparableMax(String attributePath) {
        return new MaxAggregator<R, K, V>(attributePath);
    }

    //
    // min aggregators
    //
    public static <K, V> Aggregator<BigDecimal, K, V> bigDecimalMin() {
        return new MinAggregator<BigDecimal, K, V>();
    }

    public static <K, V> Aggregator<BigDecimal, K, V> bigDecimalMin(String attributePath) {
        return new MinAggregator<BigDecimal, K, V>(attributePath);
    }

    public static <K, V> Aggregator<BigInteger, K, V> bigIntegerMin() {
        return new MinAggregator<BigInteger, K, V>();
    }

    public static <K, V> Aggregator<BigInteger, K, V> bigIntegerMin(String attributePath) {
        return new MinAggregator<BigInteger, K, V>(attributePath);
    }

    public static <K, V> Aggregator<Double, K, V> doubleMin() {
        return new MinAggregator<Double, K, V>();
    }

    public static <K, V> Aggregator<Double, K, V> doubleMin(String attributePath) {
        return new MinAggregator<Double, K, V>(attributePath);
    }

    public static <K, V> Aggregator<Integer, K, V> integerMin() {
        return new MinAggregator<Integer, K, V>();
    }

    public static <K, V> Aggregator<Integer, K, V> integerMin(String attributePath) {
        return new MinAggregator<Integer, K, V>(attributePath);
    }

    public static <K, V> Aggregator<Long, K, V> longMin() {
        return new MinAggregator<Long, K, V>();
    }

    public static <K, V> Aggregator<Long, K, V> longMin(String attributePath) {
        return new MinAggregator<Long, K, V>(attributePath);
    }

    public static <R extends Comparable, K, V> Aggregator<R, K, V> comparableMin() {
        return new MinAggregator<R, K, V>();
    }

    public static <R extends Comparable, K, V> Aggregator<R, K, V> comparableMin(String attributePath) {
        return new MinAggregator<R, K, V>(attributePath);
    }

    //
    // sum aggregators
    //
    public static <K, V> Aggregator<BigDecimal, K, V> bigDecimalSum() {
        return new BigDecimalSumAggregator<K, V>();
    }

    public static <K, V> Aggregator<BigDecimal, K, V> bigDecimalSum(String attributePath) {
        return new BigDecimalSumAggregator<K, V>(attributePath);
    }

    public static <K, V> Aggregator<BigInteger, K, V> bigIntegerSum() {
        return new BigIntegerSumAggregator<K, V>();
    }

    public static <K, V> Aggregator<BigInteger, K, V> bigIntegerSum(String attributePath) {
        return new BigIntegerSumAggregator<K, V>(attributePath);
    }

    public static <K, V> Aggregator<Double, K, V> doubleSum() {
        return new DoubleSumAggregator<K, V>();
    }

    public static <K, V> Aggregator<Double, K, V> doubleSum(String attributePath) {
        return new DoubleSumAggregator<K, V>(attributePath);
    }

    public static <K, V> Aggregator<Long, K, V> integerSum() {
        return new IntegerSumAggregator<K, V>();
    }

    public static <K, V> Aggregator<Long, K, V> integerSum(String attributePath) {
        return new IntegerSumAggregator<K, V>(attributePath);
    }

    public static <K, V> Aggregator<Long, K, V> longSum() {
        return new LongSumAggregator<K, V>();
    }

    public static <K, V> Aggregator<Long, K, V> longSum(String attributePath) {
        return new LongSumAggregator<K, V>(attributePath);
    }

    public static <K, V> Aggregator<Long, K, V> fixedPointSum() {
        return new FixedSumAggregator<K, V>();
    }

    public static <K, V> Aggregator<Long, K, V> fixedPointSum(String attributePath) {
        return new FixedSumAggregator<K, V>(attributePath);
    }

    public static <K, V> Aggregator<Double, K, V> floatingPointSum() {
        return new FloatingPointSumAggregator<K, V>();
    }

    public static <K, V> Aggregator<Double, K, V> floatingPointSum(String attributePath) {
        return new FloatingPointSumAggregator<K, V>(attributePath);
    }
}
