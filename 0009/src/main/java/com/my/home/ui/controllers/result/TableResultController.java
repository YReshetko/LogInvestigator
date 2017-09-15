package com.my.home.ui.controllers.result;

import com.my.home.plugin.model.TableResult;
import com.my.home.plugin.model.TableRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Table result from plugin to UI
 */
public class TableResultController
{
    @FXML
    private Label label;
    @FXML
    private VBox vbox;


    private TableView table;
    public void setTable(TableResult table)
    {
        if (this.table == null)
        {
            List<TableColumn> columns = new ArrayList<>();
            label.setText(table.getDescription());
            for (String columnHead : table.getHeader())
            {
                TableColumn column = new TableColumn(columnHead);

                columns.add(column);
                column.setCellValueFactory(new MapValueFactory<>(columnHead));
            }
            ObservableList<Map> allData = FXCollections.observableArrayList();
            for (TableRow row : table.getRows())
            {
                Map<String, String> dataRow = new HashMap<>();
                for (int i=0 ; i<row.getRow().size() ; i++)
                {
                    dataRow.put(table.getHeader().get(i), row.getRow().get(i).getValue());
                }
                allData.add(dataRow);
            }
            this.table = new TableView(allData);
            columns.forEach(column -> this.table.getColumns().add(column));
            this.table.setEditable(true);
            this.table.getSelectionModel().setCellSelectionEnabled(true);
            vbox.getChildren().add(this.table);
        }
    }
}
