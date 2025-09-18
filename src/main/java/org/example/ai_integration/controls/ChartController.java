package org.example.ai_integration.controls;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

/**
 * Controller for the charts in the user quiz analytics page.
 * This class is connected to {@code chart-view.fxml}.
 */

public class ChartController {
    /**
     * The chart that displays the user analytics.
     */
    @FXML
    private PieChart performanceChart;
    /**
     * Initalises the format for the chart and the data displayed within the chart.
     * Loads elements of {@code chart-view.fxml} into the application.
     */
    @FXML
    public void initialize() {
        // Dummy data: 7 correct, 3 incorrect
        PieChart.Data correct = new PieChart.Data("Correct", 7);
        PieChart.Data incorrect = new PieChart.Data("Incorrect", 3);

        performanceChart.setData(FXCollections.observableArrayList(correct, incorrect));

        // Optional: styling
        performanceChart.setTitle("Quiz Results");
        performanceChart.setClockwise(true);
        performanceChart.setLabelsVisible(true);
        performanceChart.setLegendVisible(true);
    }
}
