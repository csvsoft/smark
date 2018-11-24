package com.csvsoft.smark.config;

import com.csvsoft.smark.exception.InvalidSmarkAppSpecException;

import java.io.Serializable;
import java.util.*;

import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SmarkAppSpec implements Serializable
{

    @XmlAttribute
    private String name;
    private String configOptions;
    private String codeOutRootDir;
    private String description;

    private long debugRunId = -1L;

    @XmlAttribute
    private String smarkAppSpecId;


    public SmarkAppSpec() {
        this.smarkAppSpecId = UUID.randomUUID().toString();
    }

    public String getSmarkAppSpecId() {
        return smarkAppSpecId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {

        return Objects.hash(smarkAppSpecId);
    }

    public long getDebugRunId() {
        return debugRunId;
    }

    public void setDebugRunId(long debugRunId) {
        this.debugRunId = debugRunId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SmarkAppSpec)) {
            return false;
        }
        SmarkAppSpec other = (SmarkAppSpec) obj;
        return this.smarkAppSpecId.equalsIgnoreCase(other.getSmarkAppSpecId());
    }

    public String getCodeOutRootDir() {
        return codeOutRootDir;
    }

    public void setCodeOutRootDir(String codeOutRootDir) {
        this.codeOutRootDir = codeOutRootDir;
    }

    @XmlAttribute
    private String packageName;

    @XmlAttribute
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getConfigOptions() {
        return configOptions;
    }

    public Properties getConfigProps() {
        return PropertiesBuilder.getProps(this.configOptions);
    }

    public void setConfigOptions(String configOptions) {
        this.configOptions = configOptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<SmarkTaskSpec> getSmarkTasks() {
        return smarkTasks;
    }

    public void setSmarkTasks(List<SmarkTaskSpec> smarkTasks) {
        this.smarkTasks = smarkTasks;
    }

    @XmlAttribute
    private String language = "scala";
    @XmlElementWrapper(name = "smark-tasks")
    @XmlElement(name = "smark-task")
    private List<SmarkTaskSpec> smarkTasks;

    public void addSmarkTask(SmarkTaskSpec taskSpec) {
        if (this.smarkTasks == null) {
            smarkTasks = new LinkedList<>();
        }
        smarkTasks.add(taskSpec);
    }

    public void removeSmarkTask(SmarkTaskSpec taskSpec) {
        if (this.smarkTasks != null && smarkTasks.contains(taskSpec)) {
            smarkTasks.remove(taskSpec);
        }

    }

    public SmarkTaskSpec getTaskSpecById(String taskSpecId) {
        if (this.smarkTasks == null) {
            return null;
        }
        for (SmarkTaskSpec taskSpec : this.smarkTasks) {
            if (taskSpec.getSmarkTaskId().equals(taskSpecId)) {
                return taskSpec;
            }
        }
        return null;

    }

    public SmarkTaskSpec getTaskSpecByName(String taskSpecName) {
        if (this.smarkTasks == null) {
            return null;
        }
        for (SmarkTaskSpec taskSpec : this.smarkTasks) {
            if (taskSpec.getName().equals(taskSpecName)) {
                return taskSpec;
            }
        }
        return null;

    }

    public boolean hasSmarkTaskWithName(String taskName) {
        if (this.smarkTasks == null) {
            return false;
        }
        for (SmarkTaskSpec taskSpec : this.smarkTasks) {
            if (taskSpec.getName().equals(taskName)) {
                return true;
            }
        }
        return false;

    }

    public boolean hasSmarkTaskWithClassNameName(String className) {
        if (this.smarkTasks == null) {
            return false;
        }
        for (SmarkTaskSpec taskSpec : this.smarkTasks) {
            if (taskSpec.getClassName().equals(className)) {
                return true;
            }
        }
        return false;

    }

    public void validate() throws InvalidSmarkAppSpecException{
        if(this.smarkTasks==null){
            return;
        }
        List<String> views = new LinkedList<>();
        List<String> taskClasses = new LinkedList<>();
        List<String> taskNames = new LinkedList<>();
        for (SmarkTaskSpec taskSpec : this.smarkTasks) {
            String name= taskSpec.getName();
            String className = taskSpec.getClassName();
            if(taskNames.contains(name)){
                throw new InvalidSmarkAppSpecException("Duplicate task names found:"+name);
            }else{
                taskNames.add(name);
            }
            if(taskClasses.contains(className)){
                throw new InvalidSmarkAppSpecException("Duplicate task class names found:"+className);
            }else{
                taskClasses.add(className);
            }
            if(taskSpec instanceof  HaveViews){
                HaveViews haveViews= (HaveViews)taskSpec;
                List<String> viewList = haveViews.getViewNames();
                for(String viewName:viewList){
                    if(views.contains(viewName)){
                        throw new InvalidSmarkAppSpecException("Duplicate view names found:"+viewName);
                    }else{
                        views.add(viewName);
                    }
                }
            }
        }
    }
    public boolean hasViewWithName(String viewName){
        for (SmarkTaskSpec taskSpec : this.smarkTasks) {
            if( ! (taskSpec instanceof  SmarkTaskSQLSpec)){
                continue;
            }
            SmarkTaskSQLSpec sqlSpec = (SmarkTaskSQLSpec) taskSpec;
            List<BaseSQLPair> sqlviewPairs = sqlSpec.getSqlviewPairs();
            if(sqlviewPairs == null ){
                continue;
            }
            for(BaseSQLPair p: sqlviewPairs){
                if(p instanceof SQLViewPair){
                    SQLViewPair sv = (SQLViewPair)p;
                    if(sv.getView().equals(viewName)){
                        return true;
                    }
                }
                }


        }
        return false;
    }


    public void adjustTasksOrder(){
        if(this.smarkTasks == null){
            return;
        }
        int i=0;
        for(SmarkTaskSpec task:smarkTasks){
            task.setOrder(i++);
            if(task instanceof SmarkTaskSQLSpec){
                SmarkTaskSQLSpec sqlTask =(SmarkTaskSQLSpec)task;
                sqlTask.adjustSQLOrder();
            }
        }

    }
    public void removeSQLViewById(String sqlViewPairId){
        for (SmarkTaskSpec taskSpec : this.smarkTasks) {

            if( ! (taskSpec instanceof  SmarkTaskSQLSpec)){
                continue;
            }
            SmarkTaskSQLSpec sqlSpec = (SmarkTaskSQLSpec) taskSpec;
            List<BaseSQLPair> sqlviewPairs = sqlSpec.getSqlviewPairs();
            if(sqlviewPairs == null ){
                continue;
            }
            BaseSQLPair removeSQLviewPair = null;
            for(BaseSQLPair p: sqlviewPairs){
                if(p.getSqlViewPairId().equals(sqlViewPairId)){
                    removeSQLviewPair=p;
                    break;
                }
            }
            if(removeSQLviewPair!=null){
                sqlviewPairs.remove(removeSQLviewPair);
            }

        }

    }

}
