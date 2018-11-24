package com.csvsoft.smark.ui;

import com.csvsoft.smark.entity.FieldMetaData;
import com.csvsoft.codegen.service.ISQLFactory;
import com.csvsoft.codegen.service.SQLDataProvider;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Grid;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class UIFactory {

    private static class ChartStreamSource implements StreamResource.StreamSource {

        private static final byte[] HTML = "<html><head><script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script><script type=\"text/javascript\">google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});google.setOnLoadCallback(drawChart);function drawChart() {var data = google.visualization.arrayToDataTable([['Age', 'Weight'],[ 8,      12],[ 4,      5.5],[ 11,     14],[ 4,      5],[ 3,      3.5],[ 6.5,    7]]);var options = {title: 'Age vs. Weight comparison',hAxis: {title: 'Age', minValue: 0, maxValue: 15},vAxis: {title: 'Weight', minValue: 0, maxValue: 15},legend: 'none'};var chart = new google.visualization.ScatterChart(document.getElementById('chart_div'));chart.draw(data, options);}</script></head><body><div id=\"chart_div\" style=\"width: 900px; height: 500px;\"></div></body></html>".getBytes();

        public InputStream getStream() {
            return new ByteArrayInputStream(HTML);
        }

    }

    public static StreamResource.StreamSource  buildSampleChart(){
        return new ChartStreamSource();

    }
    public static  Grid buildDataGrid(ISQLFactory sqlFactory,String sql){

        Grid<List<Object>> dataGrid = new Grid<>();
        SQLDataProvider sqlDataProvider = sqlFactory.getSQLDataProvider();
        sqlDataProvider.setSQL(sql);

        List<FieldMetaData> metadataList = sqlDataProvider.getFieldMetaData();

        int i=0;
        for(FieldMetaData fm:metadataList){
            final int index =i;
            dataGrid.addColumn((List<Object> listObj) ->
            {
                Object o = listObj.get(index);
                return o==null ? "":o.toString();
            }).setCaption(fm.getName());
            i++;
        }
        dataGrid.setDataProvider(sqlDataProvider);
        sqlDataProvider.refreshAll();
        dataGrid.setSizeFull();
        return dataGrid;
    }
}
