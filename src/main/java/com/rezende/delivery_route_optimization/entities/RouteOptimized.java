package com.rezende.delivery_route_optimization.entities;

import java.util.ArrayList;
import java.util.List;

public class RouteOptimized {

    private final List<Address> addresses = new ArrayList<>();
    private Metric metric;

    public RouteOptimized() { }

    public RouteOptimized(final Metric metric, final List<Address> addresses) {
        this.metric = metric;
        this.addresses.addAll(addresses);
    }

    public static RouteOptimized from(final Metric metric, final List<Address> addresses) {
        return new RouteOptimized(metric, addresses);
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    @Override
    public String toString() {
        return "RouteOptimized{" +
                "addresses=" + addresses +
                ", metric=" + metric +
                '}';
    }
}
