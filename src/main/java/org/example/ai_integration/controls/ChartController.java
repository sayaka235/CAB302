package org.example.ai_integration.controls;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

public class ChartController {

    @FXML
    private PieChart performanceChart;

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
