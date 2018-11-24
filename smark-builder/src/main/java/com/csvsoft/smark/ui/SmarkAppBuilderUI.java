package com.csvsoft.smark.ui;

import com.csvsoft.codegen.service.ISQLFactory;
import com.csvsoft.codegen.service.RemoteSparkSQLFactory;
import com.csvsoft.smark.ApplicationContextProvider;
import com.csvsoft.smark.SmarkBuilderApplication;
import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.entities.UserCredential;
import com.csvsoft.smark.entities.UserRole;
import com.csvsoft.smark.exception.InvalidSmarkAppSpecException;
import com.csvsoft.smark.exception.SmarkAppNotReadyException;
import com.csvsoft.smark.exception.SmarkCompileFailureException;
import com.csvsoft.smark.exception.SmarkRunTimeException;
import com.csvsoft.smark.service.*;
import com.csvsoft.smark.ui.event.SmarkBuilderUIEventBus;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.contextmenu.MenuItem;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.*;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteTextField;
import eu.maxschuster.vaadin.autocompletetextfield.provider.CollectionSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.provider.MatchMode;
import eu.maxschuster.vaadin.autocompletetextfield.shared.ScrollBehavior;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@SpringUI(path="")
@Push
public class SmarkAppBuilderUI extends UI {

    @Autowired
    private GreeterService greeterService;

    private SmarkAppSpecService smarkAppSpecService;

    @Autowired
    public void setSmarkAppSpecService(SmarkAppSpecService smarkAppSpecService) {
        System.out.println("Injecting smarkAppSpecService");
        this.smarkAppSpecService = smarkAppSpecService;
    }

    final VerticalLayout mainLayout = new VerticalLayout();
    HorizontalSplitPanel mainSplit = new HorizontalSplitPanel();

    private Window popWindow;
    protected SmarkAppTreeGrid smackAppTree;
    protected SparkSession spark;

    private ErrorPopupWindow errorWindow;
    private ConfirmationPopup confirmationPopup;
    private MenuBar menuBar;
    private BuildAppThread buildAppThread;

    private SmarkAppMiniServerRepo miniServerRepo;

    private final SmarkBuilderUIEventBus smarkBuilderUIEventBus = new SmarkBuilderUIEventBus();


    //@Override

    protected void initX(VaadinRequest vaadinRequest) {
        if(greeterService == null){
            greeterService = ApplicationContextProvider.getContext().getBean(GreeterService.class);
        }
        setContent(new Label(greeterService.sayHi()));
    }
    //@PostConstruct
    @Override
    protected void init(VaadinRequest vaadinRequest) {

       // if(this.smarkAppSpecService == null){
       //     smarkAppSpecService = ApplicationContextProvider.getContext().getBean(FileBasedSmarkAppSpecService.class);
       // }

        miniServerRepo = new SmarkAppMiniServerRepo(this);
        setContent(buildMain());
        buildPopWindow();
        buildErrorPop();
        this.confirmationPopup = new ConfirmationPopup();
        spark = SparkSession.builder().config("spark.driver.host", "localhost").appName("smarkbuilder").master("local").getOrCreate();

        //buildAppThread = new BuildAppThread(this);
        //new Thread(buildAppThread).start();


        addDetachListener(new DetachListener() {
            @Override
            public void detach(DetachEvent event) {
                releaseResouce();
            }
        });
    }

    private void showAbout(){
        VerticalLayout vl = new VerticalLayout();
        Label label = new Label("Smark --smarter spark");
        Button button = new Button("OK");
        button.addClickListener(e->{
            popWindow.close();
        });
        vl.addComponent(label);
        this.popWindow.setCaption("About Smark Builder");
        this.popWindow.setHeight("200px");
        this.popWindow.setWidth("400px");
        this.popWindow.setModal(true);
        this.popWindow.setResizable(false);

        vl.addComponent(button);
        this.popWindow.setContent(vl);
        this.addWindow(popWindow);
    }

    private void releaseResouce() {
        if(buildAppThread!=null) {
            buildAppThread.setStopSignal(true);
        }
        if(miniServerRepo!=null){
            miniServerRepo.stopAllServers();
        }
        spark.close();
    }

    private void buildErrorPop() {
        this.errorWindow = new ErrorPopupWindow();
        int browserWindowWidth = Page.getCurrent().getBrowserWindowWidth();
        this.errorWindow.getWidth();
        float xPos = browserWindowWidth - this.errorWindow.getWidth();
        xPos = xPos > 0 ? xPos / 2 : 0;
        this.errorWindow.setPosition((int) xPos, 0);
    }


    public void setMainRight(Component comp) {
        mainSplit.setSecondComponent(comp);
    }

    public void setMainRightInTab(Component comp, String caption) {
        TabSheet tabsheet = new TabSheet();
        //tabsheet.setHeight("100%");

        tabsheet.addTab(comp, caption);
        comp.setHeightUndefined();
        setMainRight(tabsheet);
        tabsheet.setSizeFull();


    }

    public void showDeskTop() {
        setMainRight(new Label(""));
    }

    public void showDeskTop(String msg) {
        setMainRight(new Label(msg));
    }

    private Component buildMain() {

        // Have a horizontal split panel

        mainSplit.setFirstComponent(buildSmarkAppTree());
        mainSplit.setSecondComponent(new Label(""));
        //mainSplit.setSizeUndefined();
        mainSplit.setSizeFull();


// Set the position of the splitter as percentage
        mainSplit.setSplitPosition(25, Sizeable.UNITS_PERCENTAGE);


        // Top menu and search Box
        HorizontalLayout menuLayout = new HorizontalLayout();
        menuLayout.setWidth("100%");
        menuLayout.addComponent(buildMenuBar());
       // Component searchBox = buildSearchBox();

        //menuLayout.addComponent(searchBox);
        //menuLayout.setComponentAlignment(searchBox, Alignment.TOP_RIGHT);

        mainLayout.addComponent(menuLayout);

        mainLayout.addComponentsAndExpand(mainSplit);
        mainSplit.setHeight("100%");

        mainLayout.setHeight("100%");
        mainLayout.setSpacing(false);
        mainLayout.setMargin(false);

        return mainLayout;

    }

    private SmarkAppTreeGrid buildSmarkAppTree() {
        try {
            this.smackAppTree = new SmarkAppTreeGrid(this);
            List<SmarkAppSpec> smarkAppSpecs = this.smarkAppSpecService.listSmarkAppSpecs(this.getUserCredential(), null);
            smarkAppSpecs.forEach(spec -> smackAppTree.addSmarkAppSpec(spec));
            smarkAppSpecs.forEach(appSpec ->miniServerRepo.addServer(appSpec));
        } catch (Exception ex) {
            ex.printStackTrace();
            this.showError(ex.getMessage());
        }
        return this.smackAppTree;
    }

    private Component buildSmarkTree() {
        // An initial planet tree
        Tree<String> tree = new Tree<>();
        TreeData<String> treeData = new TreeData<>();

// Couple of childless root items
        treeData.addItem(null, "Mercury");
        treeData.addItem(null, "Venus");

// Items with hierarchy
        treeData.addItem(null, "Earth");
        treeData.addItem("Earth", "The Moon");

        TreeDataProvider inMemoryDataProvider = new TreeDataProvider<>(treeData);
        tree.setDataProvider(inMemoryDataProvider);
        tree.expand("Earth"); // Expand programmatically

        // Create a context menu for 'someComponent'
        ContextMenu menu = new ContextMenu(tree, true);
        // Basic menu item
        final MenuItem basic = menu.addItem("Basic Item", e -> {
            Notification.show("Action!");
        });
        basic.setIcon(FontAwesome.AUTOMOBILE);

// Checkable item
        final MenuItem checkable = menu.addItem("Checkable", e -> {
            Notification.show("checked: " + e.isChecked());
        });
        checkable.setIcon(FontAwesome.ANCHOR);
        checkable.setCheckable(true);
        checkable.setChecked(true);

// Disabled item
        final MenuItem disabled = menu.addItem("Disabled", e -> {
            Notification.show("disabled");
        });
        disabled.setEnabled(false);
        disabled.setIcon(FontAwesome.AMBULANCE);

        return tree;

    }


    private Component buildSearchBox() {
        Collection<String> theJavas = Arrays.asList(new String[]{
                "Java",
                "JavaScript",
                "Join Java",
                "JavaFX Script"
        });

        AutocompleteSuggestionProvider suggestionProvider = new CollectionSuggestionProvider(theJavas, MatchMode.CONTAINS, true, Locale.US);

        AutocompleteTextField field = new AutocompleteTextField();

// ===============================
// Available configuration options
// ===============================
        //(true); // Client side should cache suggestions
        field.setDelay(150); // Delay before sending a query to the server
        field.setItemAsHtml(false); // Suggestions contain html formating. If true, make sure that the html is save to use!
        field.setMinChars(3); // The required value length to trigger a query
        field.setScrollBehavior(ScrollBehavior.NONE); // The method that should be used to compensate scrolling of the page
        field.setSuggestionLimit(0); // The max amount of suggestions send to the client. If the limit is >= 0 no limit is applied

        field.setSuggestionProvider(suggestionProvider);
        field.addSelectListener(e -> {
            String text = "Text changed to: " + e.getSuggestion();
            Notification.show(text, Notification.Type.TRAY_NOTIFICATION);
        });
        field.addValueChangeListener(e -> {
            String text = "Value changed to: " + e.getValue();
            Notification notification = new Notification(
                    text, Notification.Type.TRAY_NOTIFICATION);
            notification.setPosition(Position.BOTTOM_LEFT);
            notification.show(Page.getCurrent());
        });
        return field;

    }

    public Component buildMenuBar() {

        MenuBar.Command cmdAbout = new MenuBar.Command() {
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                    showAbout();
            }
        };

        MenuBar.Command newAppSpec = new MenuBar.Command() {
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                SmarkAppForm form = new SmarkAppForm(smarkAppSpecService.getNewSmarkAppSpec(getUserCredential()), SmarkAppBuilderUI.this);
                final Window window = popWindow;
                window.setCaption("Create New Smark App");
                window.setContent(form);
                window.setModal(true);
                //window.setSizeFull();
                addWindow(window);

            }
        };
        //SmarkAppForm

        //SparkSchemaEditor
        MenuBar.Command testSchemaEditor = new MenuBar.Command() {
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                String schemaText= "#Field Name,Data Type,Nullable, Date Format \n firstName,Strin\n";
                SparkSchemaEditor editor = new SparkSchemaEditor(schemaText);
                final Window window = popWindow;
                window.setCaption("Create New Smark App");
                window.setContent(editor);
                window.setModal(true);
                //window.setSizeFull();
                addWindow(window);

            }
        };
        menuBar = new MenuBar();

        // Smark App
        MenuBar.MenuItem smarkApps = menuBar.addItem("SmarkApps", null, null);
        MenuBar.MenuItem newSmarkApp = smarkApps.addItem("New", null, newAppSpec);

        if(getUserCredential().isReadOnly()){
            newSmarkApp.setEnabled(false);
        }

        MenuBar.MenuItem openSmarkApp = smarkApps.addItem("Open", null, null);

        // Run App
        MenuBar.MenuItem runApp = menuBar.addItem("View", null, null);
        MenuBar.MenuItem run = runApp.addItem("View Audit Logs", null, null);
        MenuBar.MenuItem test_schema_editor = runApp.addItem("Test Schema Editor", null, testSchemaEditor);

        // Help
        MenuBar.MenuItem help = menuBar.addItem("Help", null, null);
        MenuBar.MenuItem functionHelp = help.addItem("About", null, cmdAbout);



        return menuBar;

    }

    public UserCredential getUserCredential() {
        UserCredential userCredential = this.smarkAppSpecService.getDefaultUserCredential();
        return userCredential;
    }


    private void buildPopWindow() {
        final Window window = new Window("Window");
        window.setWidth(600.0f, Unit.PIXELS);
        window.setHeight(500.0f, Unit.PIXELS);
        window.setModal(true);
        this.popWindow = window;
    }


    public SmarkAppTreeGrid getSmackAppTree() {
        return smackAppTree;
    }

    public SparkSession getSparkSession() {
        return spark;
    }

    public SmarkAppSpecService getSmarkAppSpecService() {
        return smarkAppSpecService;
    }

    public Window getPopWindow() {
        return this.popWindow;
    }

    public void showError(String errorMsg) {
        if (this.errorWindow == null) {
            System.out.print(errorMsg);
            return;
        }
        this.errorWindow.setErrorMessage(errorMsg);

        if (!errorWindow.isAttached()) {
            this.addWindow(this.errorWindow);
        }
    }

    public void showConfirm2(String caption, String msg, Consumer<Boolean> confirmationConsumer) {
        this.confirmationPopup.setCaption(caption);
        this.confirmationPopup.setMessage(msg);
        this.confirmationPopup.setResultConsumer(confirmationConsumer);
        if (!confirmationPopup.isAttached()) {
            this.addWindow(this.confirmationPopup);
        }


    }

    public void showConfirm(String caption, String msg, Consumer<Boolean> confirmationConsumer) {
        ConfirmDialog.show(this, caption, msg,
                "Yes", "No", new ConfirmDialog.Listener() {
                    public void onClose(ConfirmDialog dialog) {
                        confirmationConsumer.accept(dialog.isConfirmed());
                    }
                });
    }

    public boolean saveAppSpec(SmarkAppSpec appSpec) {
        try {
            this.smarkAppSpecService.saveSmarkAppSpec(this.getUserCredential(), appSpec);
            addAppSpecTobuild(appSpec);
            this.showDeskTop();
            Notification.show("Saved successfully.");
            return true;
        } catch (InvalidSmarkAppSpecException ex) {
            this.showError(ex.getMessage());
            return false;
        }
    }


    public void showSimpleProgressBar() {
        ProgressBar progressBar = new ProgressBar();
    }

    public ISQLFactory getSparkSQLFactory(SmarkAppSpec appSpec,String runTo){
        SmarkAppMiniServer miniServer = this.miniServerRepo.getServer(appSpec);
        try {
            return new RemoteSparkSQLFactory(miniServer, appSpec, runTo);
        }catch(SmarkRunTimeException ex){
           if( ex.getCause()!=null){
              Throwable cause = ex.getCause();
              if(cause instanceof SmarkCompileFailureException){
                  this.showError(appSpec.getName() +" compilation failed, please fix the codes before making further changes.");
              }else if(cause instanceof SmarkAppNotReadyException){
                  this.showError(appSpec.getName() +" is still compiling/running, not ready for changing:"+ cause.getMessage());
              }else{
                  this.showError(appSpec.getName()+" got runtime error:"+ ExceptionUtils.getStackTrace(cause));
              }
           }
            throw ex;
        }

    }

    public void addAppSpecTobuild(SmarkAppSpec appSpec){
        this.miniServerRepo.addServer(appSpec);
    }

    public static SmarkBuilderUIEventBus getBuilderUIEventbus() {
        return ((SmarkAppBuilderUI) getCurrent()).smarkBuilderUIEventBus;
    }


}
