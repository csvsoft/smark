package com.csvsoft.smark.ui;

import com.csvsoft.smark.entity.FieldMetaData;
import com.csvsoft.codegen.service.ISQLFactory;
import com.csvsoft.codegen.service.SQLDataProvider;
import com.csvsoft.smark.service.sqlsuggestor.*;
import com.csvsoft.smark.sevice.ICatalogueProvider;
import com.csvsoft.smark.ui.model.CataLogTreeData;
import com.csvsoft.smark.ui.model.TreeItemData;
import com.csvsoft.smark.util.TemplateUtils;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.SuggestionExtension;

import java.util.List;
import java.util.Map;

public class SQLSheet extends VerticalLayout implements View {

    private ISQLFactory sqlFactory;
    private Grid<List<Object>> dataGrid;
    private AceEditor ace;
    private VerticalSplitPanel vsp;
    HorizontalSplitPanel mainHSplitPanel;
    private Map<String, Object> varMap;

    public SQLSheet(ISQLFactory sqlFactory) {
        this.sqlFactory = sqlFactory;
        initUI();
    }

    public SQLSheet(ISQLFactory sqlFactory, Map<String, Object> varMap) {
        this.sqlFactory = sqlFactory;
        this.varMap = varMap;
        initUI();
    }

    public SQLSheet(ISQLFactory sqlFactory, String sql, Map<String, Object> varMap) {
        this(sqlFactory, varMap);
        vsp.setSecondComponent(buildDataGrid(sql));
        ace.setValue(sql);

    }

    public SQLSheet(ISQLFactory sqlFactory, String sql) {
        this(sqlFactory);
        vsp.setSecondComponent(buildDataGrid(sql));
        ace.setValue(sql);

    }

    private Tree<TreeItemData> buildDataCatalogTree() {
        Tree<TreeItemData> cataLogTree = new Tree<>();
        CataLogTreeData treeData = new CataLogTreeData(sqlFactory.getCataLogueProvider());
        TreeDataProvider treeDataProvider = new TreeDataProvider(treeData);
        cataLogTree.setDataProvider(treeDataProvider);
        return cataLogTree;


    }

    private String formatSQL(String origSQL) {
        String formatted = new BasicFormatterImpl().format(origSQL);
        return formatted;
    }

    public void refreshTables() {

        addCatalogTree();
    }

    private void addCatalogTree() {
        Tree<TreeItemData> catalogTree = buildDataCatalogTree();
        mainHSplitPanel.setFirstComponent(catalogTree);
        catalogTree.setItemCaptionGenerator(treeItemData -> {
            return treeItemData.getCaption();
        });
    }

    private void initUI() {

        mainHSplitPanel = new HorizontalSplitPanel();
        // catalog tree
        addCatalogTree();

        vsp = new VerticalSplitPanel();
        vsp.setSizeFull();
        vsp.setLocked(false);
        mainHSplitPanel.setSecondComponent(vsp);

        vsp.setFirstComponent(buildSQLEditorPanel());
        this.dataGrid = new Grid<>();
        this.dataGrid.setSizeFull();
        vsp.setSecondComponent(this.dataGrid);
        vsp.setSplitPosition(50, Unit.PERCENTAGE);


        mainHSplitPanel.setSizeFull();

        //this.setMargin(true);
        mainHSplitPanel.setSplitPosition(25, Unit.PERCENTAGE);
        this.addComponentsAndExpand(mainHSplitPanel);
        this.setMargin(true);
        this.setHeight("100%");

    }

    private VerticalLayout buildSQLEditorPanel() {
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);
        vl.setMargin(false);

        HorizontalLayout hl = new HorizontalLayout();

        Button btnRun = new Button();
        btnRun.setCaption("Run");
        btnRun.addClickListener((Button.ClickEvent evt) ->
                {
                    runSQL();
                }
        );
        Button formatRun = new Button();
        formatRun.setCaption("Format SQL");
        formatRun.addClickListener((Button.ClickEvent evt) ->
                {
                    ace.setValue(formatSQL(ace.getValue()));
                }
        );
        hl.setSpacing(true);
        hl.addComponent(btnRun);
        hl.addComponent(formatRun);
        vl.addComponent(hl);


        vl.addComponentsAndExpand(buildSQLEditor());

        return vl;
    }

    public void runSQL() {
        String sql = ace.getValue();
        if (StringUtils.isBlank(sql) || StringUtils.isBlank(sql.trim())) {
            return;
        }
        try {
            vsp.setSecondComponent(buildDataGrid(sql));
        } catch (Exception ex) {
            vsp.setSecondComponent(new Label(ex.getMessage()));
        }
    }

    private Grid buildDataGrid(String sql) {

        String realSQL = sql;
        if (sql.contains("$") && varMap != null) {
            realSQL = TemplateUtils.merge(sql, this.varMap);
        }

        this.dataGrid = new Grid<>();
        SQLDataProvider sqlDataProvider = sqlFactory.getSQLDataProvider();
        sqlDataProvider.setSQL(realSQL);

        List<FieldMetaData> metadataList = sqlDataProvider.getFieldMetaData();

        int i = 0;
        for (FieldMetaData fm : metadataList) {
            final int index = i;
            this.dataGrid.addColumn((List<Object> listObj) ->
            {
                Object o = listObj.get(index);
                return o == null ? "" : o.toString();
            }).setCaption(fm.getName());
            i++;
        }
        this.dataGrid.setDataProvider(sqlDataProvider);
        sqlDataProvider.refreshAll();
        dataGrid.setSizeFull();
        return this.dataGrid;
    }

    private AceEditor buildSQLEditor() {
        this.ace = new AceEditor();
        ace.setMode(AceMode.sql);
        //ace.setData("SELECT * from USERS");
        ace.setWidth("100%");
        ace.setShowPrintMargin(false);

        ace.addShortcutListener(new ShortcutListener("Ctrl + Enter", ShortcutAction.KeyCode.ENTER, new int[]{ShortcutAction.ModifierKey.CTRL}) {
            @Override
            public void handleAction(Object sender, Object target) {
                runSQL();
            }
        });
        ace.addShortcutListener(new ShortcutListener("Ctrl + Shift + F", ShortcutAction.KeyCode.F, new int[]{ShortcutAction.ModifierKey.CTRL, ShortcutAction.ModifierKey.SHIFT}) {
            @Override
            public void handleAction(Object sender, Object target) {

                ace.setValue(formatSQL(ace.getValue()));
            }
        });
        ace.setSizeFull();
        ace.setShowGutter(true);


        BaseSQLSuggestor suggestor = (this.varMap == null) ? new BaseSQLSuggestor() : new SQLVarSuggestor(varMap);
        ICatalogueProvider cp = sqlFactory.getCataLogueProvider();
        ISimpleSQLTokenizer t = new BaseSQLTokenizer();
        ISQLKeywordProivers k = new AnsiSQLKeywordProvider();
        suggestor.setCataLogProvider(cp);
        suggestor.setSqlTokenizer(t);
        suggestor.setKeyworkProvider(k);

        new SuggestionExtension(suggestor).extend(ace);

        return ace;
    }

    public AceEditor getEditor() {
        return ace;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
