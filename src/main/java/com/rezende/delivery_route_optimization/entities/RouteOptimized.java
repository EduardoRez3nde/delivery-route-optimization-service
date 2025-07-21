package com.rezende.delivery_route_optimization.entities;

import java.util.ArrayList;
import java.util.List;

public class RouteOptimized {

    private final List<Address> addresses = new ArrayList<>();
    private Metric metric;

    public RouteOptimized() { }

    public RouteOptimized(final List<Address> addresses, final Metric metric) {
        this.addresses.addAll(addresses);
        this.metric = metric;
    }

    public static RouteOptimized from(final List<Address> addresses, final Metric metric) {
        return new RouteOptimized(addresses, metric);
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
