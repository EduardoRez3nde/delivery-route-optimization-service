package com.rezende.delivery_route_optimization.dto;

import java.util.ArrayList;
import java.util.List;

public class RouteOptimizedResponseDTO {

    private final List<AddressDTO> routeOptimized = new ArrayList<>();
    private MetricDTO metric;

    public RouteOptimizedResponseDTO() { }

    private RouteOptimizedResponseDTO(final List<AddressDTO> address, final MetricDTO metric) {
        this.metric = metric;
        this.routeOptimized.addAll(address);
    }

    public static RouteOptimizedResponseDTO from(final List<AddressDTO> address, final MetricDTO metric) {
        return new RouteOptimizedResponseDTO(address, metric);
    }

    public List<AddressDTO> getRouteOptimized() {
        return routeOptimized;
    }

    public MetricDTO getMetric() {
        return metric;
    }

    public void setMetric(MetricDTO metric) {
        this.metric = metric;
    }
}
