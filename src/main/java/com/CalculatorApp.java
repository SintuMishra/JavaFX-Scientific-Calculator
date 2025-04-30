package com.calculator;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class CalculatorApp extends Application {

    private TextField inputField = new TextField();
    private TextArea historyArea = new TextArea();
    private VBox historyBox = new VBox();
    private boolean historyVisible = true;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Scientific Calculator");

        inputField.setPrefHeight(40);
        inputField.setStyle("-fx-font-size: 18px;");
        inputField.setEditable(false);

        GridPane buttonGrid = createButtonGrid();

        historyArea.setEditable(false);
        historyArea.setPrefWidth(250);
        historyArea.setStyle("-fx-font-size: 14px;");

        Button toggleHistoryBtn = new Button("▶ History");
        toggleHistoryBtn.setOnAction(e -> toggleHistory());

        historyBox.getChildren().addAll(toggleHistoryBtn, historyArea);
        historyBox.setPadding(new Insets(10));
        historyBox.setVisible(true);

        HBox mainLayout = new HBox(10, buttonGrid, historyBox);
        VBox layout = new VBox(10, createMenuBar(), inputField, mainLayout);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 850, 400);

        // Keyboard input support
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case ENTER -> evaluate();
                case BACK_SPACE -> {
                    String current = inputField.getText();
                    if (!current.isEmpty())
                        inputField.setText(current.substring(0, current.length() - 1));
                }
                case ESCAPE -> inputField.clear();
                default -> {
                    String text = event.getText();
                    if ("0123456789+-*/().^".contains(text))
                        inputField.appendText(text);
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu viewMenu = new Menu("View");

        CheckMenuItem toggleHistory = new CheckMenuItem("Show History");
        toggleHistory.setSelected(true);
        toggleHistory.setOnAction(e -> toggleHistory());

        viewMenu.getItems().add(toggleHistory);
        menuBar.getMenus().add(viewMenu);
        return menuBar;
    }

    private GridPane createButtonGrid() {
        String[][] buttons = {
            {"7", "8", "9", "/", "sin"},
            {"4", "5", "6", "*", "cos"},
            {"1", "2", "3", "-", "sqrt"},
            {"0", "C", "=", "+", "log"},
            {"(", ")", "^", "tan", "CLR"}
        };

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER_LEFT);

        for (int row = 0; row < buttons.length; row++) {
            for (int col = 0; col < buttons[row].length; col++) {
                String label = buttons[row][col];
                Button btn = new Button(label);
                btn.setPrefSize(60, 50);
                btn.setStyle("-fx-font-size: 16px;");
                btn.setOnAction(e -> handleInput(label));
                grid.add(btn, col, row);
            }
        }
        return grid;
    }

    private void handleInput(String label) {
        switch (label) {
            case "=" -> evaluate();
            case "C" -> {
                String text = inputField.getText();
                if (!text.isEmpty())
                    inputField.setText(text.substring(0, text.length() - 1));
            }
            case "CLR" -> {
                inputField.clear();
                historyArea.clear();
            }
            case "sin", "cos", "tan", "log", "sqrt" -> inputField.appendText(label + "(");
            default -> inputField.appendText(label);
        }
    }

    private void evaluate() {
        try {
            String expr = inputField.getText();
            Expression expression = new ExpressionBuilder(expr).build();
            double result = expression.evaluate();
            inputField.setText(String.valueOf(result));
            historyArea.appendText(expr + " = " + result + "\n");
        } catch (Exception e) {
            inputField.setText("Error");
        }
    }

    private void toggleHistory() {
        historyVisible = !historyVisible;
        historyBox.setVisible(historyVisible);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
// mvn clean package