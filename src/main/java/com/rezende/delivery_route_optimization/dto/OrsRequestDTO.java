package com.rezende.delivery_route_optimization.dto;

import com.rezende.delivery_route_optimization.entities.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class OrsRequestDTO {

    private final List<List<Coordinates>> locations = new ArrayList<>();
    private List<String> metrics = new ArrayList<>();
    private String units;

    public OrsRequestDTO() { }

    private OrsRequestDTO(
            final List<List<Coordinates>> locations,
            final List<String> metrics,
            final String units
    ) {
        this.locations.addAll(locations);
        this.metrics.addAll(metrics);
        this.units = units;
    }

    public static OrsRequestDTO from(
            final List<List<Coordinates>> locations,
            final List<String> metrics,
            final String units
    ) {
        return new OrsRequestDTO(locations, metrics, units);
    }

    public List<List<Coordinates>> getLocations() {
        return locations;
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public String toString() {
        return "OrsRequestDTO{" +
                "locations=" + locations +
                ", metrics=" + metrics +
                ", units='" + units + '\'' +
                '}';
    }
}
