package com.rezende.delivery_route_optimization.dto;

import java.util.ArrayList;
import java.util.List;

public class OrsResponseDTO {

    private final List<List<Double>> durations = new ArrayList<>();
    private final List<List<Double>> distances = new ArrayList<>();

    public OrsResponseDTO() { }

    public List<List<Double>> getDurations() {
        return durations;
    }

    public List<List<Double>> getDistances() {
        return distances;
    }

    @Override
    public String toString() {
        return "OrsResponseDTO{" +
                "durations=" + durations +
                ", distances=" + distances +
                '}';
    }
}
