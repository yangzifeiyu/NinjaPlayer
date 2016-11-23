package com.mfusion.commons.data;

import com.mfusion.commons.entity.exception.IllegalScheduleException;
import com.mfusion.commons.entity.exception.PathAccessException;
import com.mfusion.commons.entity.exception.ScheduleNotFoundException;
import com.mfusion.commons.entity.exception.TemplateNotFoundException;
import com.mfusion.commons.entity.values.SchedulePlayType;
import com.mfusion.commons.entity.schedule.BlockEntity;
import com.mfusion.commons.entity.schedule.Schedule;
import com.mfusion.commons.tools.DateConverter;
import com.mfusion.commons.tools.FileOperator;
import com.mfusion.commons.tools.FileZipHelper;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.tools.XmlOperator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;


/**
 * Created by jimmy on 7/12/2016.
 *
 * A class that converts Schedule object into XML file and vice versa.
 */
public class XMLSchedule {

    private volatile static XMLSchedule singleton;

    public static XMLSchedule getInstance() {
        if (singleton == null) {
            synchronized (XMLSchedule.class) {
                if (singleton == null) {
                    singleton = new XMLSchedule();
                }
            }
        }
        return singleton;
    }
    private XmlOperator m_XMLHelper;
    /**
     *
     */
    public XMLSchedule()
    {
        //Assign a global value here
        this.m_XMLHelper=new XmlOperator();
    }

    /**
     * Default function to be called externally
     * As there is only one schedule xml for initial version, this is supposed
     * to be used by default unless otherwise required.
     * load schedule information from a default folder.
     * @return A schedule Object that is to be rendered on schedule designer
     * @throws PathAccessException
     * @throws ScheduleNotFoundException
     * @throws IllegalScheduleException
     */
    public Schedule LoadSchedule() throws Exception
    {
        return this.LoadSchedule(InternalKeyWords.DefaultScheduleName);
    }
    /**
     * Default function to be called externally
     * As there is only one schedule xml for initial version, this is supposed
     * to be used by default unless otherwise required.
     * load schedule information from a default folder.
     * @return A schedule Object that is to be rendered on schedule designer
     * @throws PathAccessException
     * @throws ScheduleNotFoundException
     * @throws IllegalScheduleException
     */
    public Schedule LoadSchedule(String scheduleName) throws Exception
    {
        return this.LoadSchedule(InternalKeyWords.DefaultScheduleXmlPath,scheduleName);
    }
    /**
     * load specified schedule information from a specified folder.
     * @param scheduleName schedule name
     * @param XMLFolder target xml folder
     * @return A schedule Object that is to be rendered on schedule designer
     * @throws PathAccessException
     * @throws ScheduleNotFoundException
     * @throws IllegalScheduleException
     */
    public Schedule LoadSchedule(String XMLFolder,String scheduleName) throws Exception
    {
        Schedule scheduleEntity=new Schedule();
        scheduleEntity.id=scheduleName;

        File scheduleFile=new File(XMLFolder,scheduleName+".xml");
        if(!scheduleFile.exists())
            return scheduleEntity;
        Document scheduleDocument=this.m_XMLHelper.getXmlDocument(scheduleFile);
        if(scheduleDocument==null)
            return scheduleEntity;

        Element rootElement=scheduleDocument.getDocumentElement();

        Element runElement=this.m_XMLHelper.getSubNode(rootElement, "Run");
        scheduleEntity.playType= SchedulePlayType.valueOf(Integer.valueOf(runElement.getAttribute("PlayBasedOn")));

        Element idlElement=this.m_XMLHelper.getSubNode(runElement, "IdlePBU");
        scheduleEntity.idleItem=idlElement.getAttribute("Id");

        ArrayList<Element> blockElementList=this.m_XMLHelper.getSubNodeList(runElement, "TimeLine");
        if(blockElementList==null)
            throw new IllegalScheduleException(scheduleName+" block is invalid");

        BlockEntity blockEntity=null;
        ArrayList<Element> blockItemElementList=null;
        Date currentDate=Calendar.getInstance().getTime();
        for (Element blockElement : blockElementList) {
            blockEntity=new BlockEntity();
            blockEntity.startDate= DateConverter.convertStrToDate(blockElement.getAttribute("StartDate"));
            blockEntity.endDate=DateConverter.convertStrToDate(blockElement.getAttribute("EndDate"));

            blockItemElementList=this.m_XMLHelper.getSubNodeList(blockElement, "PBU");
            for (Element item : blockItemElementList) {
                String templateId=item.getAttribute("Id");
                if(blockEntity.endDate!=null&&blockEntity.endDate.compareTo(currentDate)<0&&!XMLTemplate.getInstance().existTemplateWithResult(templateId)){
                    continue;
                }
                blockEntity.itemList.add(templateId);
            }

            if(blockEntity.itemList.size()==0)
                continue;
            blockEntity.startTime=DateConverter.convertStrToTime(blockElement.getAttribute("StartTime"));
            blockEntity.endTime=DateConverter.convertStrToTime(blockElement.getAttribute("EndTime"));

            String recurrence=blockElement.getAttribute("Recurrence");
            blockEntity.isRecurrence=recurrence.substring(0, 1).equals("1")?true:false;
            blockEntity.recurrence=recurrence.substring(1);

            scheduleEntity.blockList.add(blockEntity);
        }

        return scheduleEntity;
    }

    /**
     * Default function to be used for saving a schedule XML into default xml location
     * @param sch
     * @return A schedule Object that is to be rendered on schedule designer
     * @throws PathAccessException
     * @throws IllegalScheduleException
     */
    public  boolean SaveSchedule(Schedule sch) throws Exception
    {
        return this.SaveSchedule(sch,InternalKeyWords.DefaultScheduleXmlPath);
    }

    /**
     * Save a schedule object into an XML file
     * @param sch Schedule object
     * @param XMLFolder target xml folder
     * @return A schedule Object that is to be rendered on schedule designer
     * @throws PathAccessException
     * @throws IllegalScheduleException
     */
    public boolean SaveSchedule(Schedule sch,String XMLFolder) throws Exception
    {
        if(sch==null||sch.idleItem==null||sch.idleItem==null||sch.idleItem.isEmpty())
            throw new IllegalScheduleException("No default template");

        if(sch.id==null||sch.id.isEmpty())
            sch.id=InternalKeyWords.DefaultScheduleName;

        Document scheduleDocument=this.writeScheduleDocument(sch);

        if(this.m_XMLHelper.saveXmlDocument(scheduleDocument, XMLFolder+sch.id+".xml"))
            return true;

        return false;
    }
    /**
     * Save a schedule object into an XML file and assign it to player
     * @param sch Schedule object
     * @return true|false
     * @throws PathAccessException
     * @throws ScheduleNotFoundException
     * @throws IllegalScheduleException
     */
    public  boolean SaveAndAssignSchedule(Schedule sch) throws Exception{
        return this.SaveAndAssignSchedule(sch,InternalKeyWords.DefaultScheduleXmlPath,InternalKeyWords.DefaultTemplateXmlPath);
    }

    /**
     * Save a schedule object into an XML file and assign it to player
     * @param sch Schedule object
     * @param ScheXMLFolder target schedule xml folder
     * @param TemplateXMLFolder target template xml folder
     * @return true|false
     * @throws PathAccessException
     * @throws ScheduleNotFoundException
     * @throws IllegalScheduleException
     */
    public  boolean SaveAndAssignSchedule(Schedule sch,String ScheXMLFolder,String TemplateXMLFolder) throws Exception{
        if(this.SaveSchedule(sch,ScheXMLFolder))
            return this.assignSchedule(sch.id,ScheXMLFolder,TemplateXMLFolder,InternalKeyWords.AssignedXmlFolder);
        return false;
    }

    private Document writeScheduleDocument(Schedule scheduleEntity) throws Exception{
        try {
            Document scheduleDocument=this.m_XMLHelper.createXmlDocument("Display");
            if(scheduleDocument==null)
                return null;

            Element rootElement=scheduleDocument.getDocumentElement();

            Element runElement=scheduleDocument.createElement("Run");
            runElement.setAttribute("PlayBasedOn", String.valueOf(scheduleEntity.playType.value()));
            rootElement.appendChild(runElement);

            Element idlItemElement=scheduleDocument.createElement("IdlePBU");
            idlItemElement.setAttribute("Id", scheduleEntity.idleItem);
            XMLTemplate.getInstance().existTemplate(scheduleEntity.idleItem);
            runElement.appendChild(idlItemElement);

            Integer blockItemIndex=0;
            Element timelineElement=null;
            Element blockItemElement=null;
            Date currentDate= Calendar.getInstance().getTime();
            for (BlockEntity block : scheduleEntity.blockList) {
                timelineElement=scheduleDocument.createElement("TimeLine");
                timelineElement.setAttribute("StartDate", DateConverter.convertDateToStr(block.startDate));
                timelineElement.setAttribute("EndDate", DateConverter.convertDateToStr(block.endDate));
                timelineElement.setAttribute("StartTime", DateConverter.convertTimeToStrNoSecond(block.startTime));
                timelineElement.setAttribute("EndTime", DateConverter.convertTimeToStrNoSecond(block.endTime));
                timelineElement.setAttribute("Recurrence", (block.isRecurrence?"1":"0")+block.recurrence);

                blockItemIndex=0;
                for (String item : block.itemList) {
                    blockItemElement=scheduleDocument.createElement("PBU");
                    if(block.endDate==null||block.endDate.compareTo(currentDate)>=0)
                        XMLTemplate.getInstance().existTemplate(item);
                    blockItemElement.setAttribute("Id", item);
                    blockItemElement.setAttribute("Index", String.valueOf(blockItemIndex++));
                    timelineElement.appendChild(blockItemElement);
                }

                runElement.appendChild(timelineElement);
            }

            return scheduleDocument;
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
            LogOperator.WriteLogfortxt("XMLSchedule==>writeScheduleDocument:"+ex.getMessage());
            throw new IllegalScheduleException(ex.getMessage());
        }
    }

    public Boolean assignSchedule(String scheName) throws Exception{
        return this.assignSchedule(scheName,InternalKeyWords.DefaultScheduleXmlPath,InternalKeyWords.DefaultTemplateXmlPath,InternalKeyWords.AssignedXmlFolder);
    }

    private Boolean assignSchedule(String scheName,String ScheXMLFolder,String TemplateXMLFolder,String assignDestFolder) throws Exception{
        // TODO Auto-generated method stub
        try {
            HashSet<String> blockItemSet=null;
            Document scheduleDocument=null;
            if(scheName!=null&&!scheName.isEmpty()){
                scheduleDocument=this.m_XMLHelper.getXmlDocument(ScheXMLFolder+scheName+".xml");
                if(scheduleDocument!=null){
                    blockItemSet=this.getAllItemInSchedule(scheduleDocument);
                    if(blockItemSet==null||blockItemSet.isEmpty())
                        throw new IllegalScheduleException("No default template");
                }
            }
            if(scheduleDocument==null){
                scheduleDocument=this.m_XMLHelper.createXmlDocument();
            }

            //Add schedule and device cmmand
            Document assignDocument=this.buildAssignXml(scheduleDocument,this.createDeviceCmdNode(scheduleDocument));

            //save assign xml
            String outputFolder=InternalKeyWords.DefaultXmlTempPath+scheName+File.separator;
            FileOperator.deleteFile(outputFolder);

            this.m_XMLHelper.saveXmlDocument(assignDocument, outputFolder+InternalKeyWords.AssignedXmlName);

            //Copy template to assign folder
            if(blockItemSet!=null)
                for (String template : blockItemSet) {
                    if(!XMLTemplate.getInstance().existTemplate(TemplateXMLFolder,template)){
                        throw new TemplateNotFoundException(template);
                    }
                    FileOperator.copyFolder(TemplateXMLFolder+template, outputFolder+template);
                }

            //back current playing content
            String backupPath=InternalKeyWords.DefaultXmlTempPath+"MFusion.old.zip";
            FileZipHelper.compressionFolder(backupPath, assignDestFolder);
            try {

                //copy new playing content
                FileOperator.copyFolder(outputFolder, assignDestFolder);

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                //roll-back
                FileZipHelper.deCompressionFolder(new File(backupPath), assignDestFolder);
            }

            FileOperator.deleteFile(backupPath);
            return  DALSettings.getInstance().updateSetting(InternalKeyWords.Config_PlayingSchedule, scheName);

        } catch (Exception e) {
            // TODO: handle exception
            throw e;
        }
    }

    public Boolean assignDeviceSchedule() throws Exception{
        return this.assignDeviceSchedule(InternalKeyWords.AssignedXmlFolder);
    }

    public Boolean assignDeviceSchedule(String assignDestFolder) throws Exception{
        // TODO Auto-generated method stub
        try {
            HashSet<String> blockItemSet=null;
            Document assignDocument=this.m_XMLHelper.getXmlDocument(assignDestFolder+InternalKeyWords.AssignedXmlName);
            if(assignDocument==null)
                this.m_XMLHelper.createXmlDocument();

            //Add schedule and device cmmand
            assignDocument=this.insertDeviceToAssignXml(assignDocument);

            //save assign xml
            String outputFolder=InternalKeyWords.DefaultXmlTempPath+InternalKeyWords.AssignedXmlName;
            FileOperator.deleteFile(outputFolder);

            this.m_XMLHelper.saveXmlDocument(assignDocument,outputFolder);

            FileOperator.copyFile(outputFolder,assignDestFolder+InternalKeyWords.AssignedXmlName);

            return  true;

        } catch (Exception e) {
            // TODO: handle exception
            throw e;
        }
    }

    private Element createDeviceCmdNode(Document document) {
        // TODO Auto-generated method stub
        try {

            Element deviceElement=document.createElement("Device");
            Element powerElement=document.createElement("Power");
            deviceElement.appendChild(powerElement);

            String shutDownTime=DALSettings.getInstance().getSettingByKey(InternalKeyWords.Config_ShutDownTime);
            if(shutDownTime!=null&&!shutDownTime.isEmpty())
                powerElement.appendChild(this.getDeviceCmdElement(document,"Shutdown",shutDownTime));

            String wakeUpTime=DALSettings.getInstance().getSettingByKey(InternalKeyWords.Config_WakeUpTime);
            if(wakeUpTime!=null&&!wakeUpTime.isEmpty())
                powerElement.appendChild(this.getDeviceCmdElement(document,"WakeUp",wakeUpTime));

            return deviceElement;
        } catch (Exception e) {

            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    private Element getDeviceCmdElement(Document document,String cmdType,String cmdValue){
        Element timelineElement=document.createElement("TimeLine");
        timelineElement.setAttribute("RunTime", cmdValue);
        timelineElement.setAttribute("StartDate", DateConverter.convertCurrentDateToStr());
        timelineElement.setAttribute("EndDate", "0001,01,01");

        Element cmdElement=document.createElement("Command");
        cmdElement.setAttribute("name", cmdType);
        cmdElement.setAttribute("target", InternalKeyWords.PlayerOriginal);
        timelineElement.appendChild(cmdElement);

        return timelineElement;
    }

    private HashSet<String> getAllItemInSchedule(Document scheduleDocument){
        try {

            HashSet<String> itemSet=new HashSet<String>();

            Element idlElement=this.m_XMLHelper.getSubNode(scheduleDocument.getDocumentElement(), "IdlePBU");
            if(idlElement==null)
                return null;

            itemSet.add(idlElement.getAttribute("Id"));

            Element displayRunElement = this.m_XMLHelper.getSubNode(scheduleDocument.getDocumentElement(),"Run");
            if(displayRunElement==null)
                return null;

            ArrayList<Element> blockElementList=this.m_XMLHelper.getSubNodeList(displayRunElement, "TimeLine");
            if(blockElementList==null||blockElementList.size()==0)
                return null;

            Date currentDate=Calendar.getInstance().getTime(),endDate;
            ArrayList<Element> blockItemElementList;
            for (Element blockElement : blockElementList) {

                endDate = DateConverter.convertStrToDate(blockElement.getAttribute("EndDate"));
                blockItemElementList = this.m_XMLHelper.getSubNodeList(blockElement, "PBU");
                for (Element item : blockItemElementList) {
                    String templateId = item.getAttribute("Id");
                    if (endDate != null && endDate.compareTo(currentDate) < 0 && !XMLTemplate.getInstance().existTemplateWithResult(templateId)) {
                        continue;
                    }
                    itemSet.add(templateId);
                }
            }
            return itemSet;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    private Document buildAssignXml(Document scheduleDocument,Element deviceElement){
        try {

            Element displayElement=scheduleDocument.getDocumentElement();
            if(displayElement!=null)
                scheduleDocument.removeChild(displayElement);

            Element rootElement=scheduleDocument.createElement("MfusionPlayer");
            rootElement.setAttribute("Company", "Mfusion");
            rootElement.setAttribute("XMLVersion", "1.0");
            rootElement.setAttribute("ServerType", "Lite");
            rootElement.setAttribute("PlayerName", "Local");
            rootElement.setAttribute("UpdateTime", DateConverter.convertCurrentFullTimeToStr());
            scheduleDocument.appendChild(rootElement);

            Element dataElement=scheduleDocument.createElement("Data");
            rootElement.appendChild(dataElement);

            Element contentElement=scheduleDocument.createElement("Contents");
            dataElement.appendChild(contentElement);

            if(deviceElement!=null)
                contentElement.appendChild(deviceElement);

            if(displayElement!=null)
                contentElement.appendChild(displayElement);

            return scheduleDocument;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    private Document insertDeviceToAssignXml(Document assignDocument){
        try {
            Element rootElement=null;
            if(assignDocument==null){
                assignDocument=m_XMLHelper.createXmlDocument();
                rootElement=assignDocument.createElement("MfusionPlayer");
                rootElement.setAttribute("Company", "Mfusion");
                rootElement.setAttribute("XMLVersion", "1.0");
                rootElement.setAttribute("ServerType", "Lite");
                rootElement.setAttribute("PlayerName", "Local");
                rootElement.setAttribute("UpdateTime", DateConverter.convertCurrentFullTimeToStr());
                assignDocument.appendChild(rootElement);
            }else
                rootElement=assignDocument.getDocumentElement();

            Element dataElement= m_XMLHelper.getSubNode(assignDocument.getDocumentElement(),"Data");
            if(dataElement==null){
                dataElement=assignDocument.createElement("Data");
                rootElement.appendChild(dataElement);
            }

            Element contentElement= m_XMLHelper.getSubNode(assignDocument.getDocumentElement(),"Contents");
            if(contentElement==null){
                contentElement=assignDocument.createElement("Contents");
                dataElement.appendChild(contentElement);
            }

            Element oldDeviceElement= m_XMLHelper.getSubNode(assignDocument.getDocumentElement(),"Device");
            if(oldDeviceElement!=null)
                contentElement.removeChild(oldDeviceElement);

            Element deviceElement=this.createDeviceCmdNode(assignDocument);
            if(deviceElement!=null)
                contentElement.appendChild(deviceElement);

            return assignDocument;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public Boolean refreshTemplateInSchedule(String oldTemplateName,String newTemplateName){
        return refreshTemplateInSchedule(InternalKeyWords.DefaultScheduleXmlPath,InternalKeyWords.DefaultScheduleName,oldTemplateName,newTemplateName);
    }

    public Boolean refreshTemplateInSchedule(String XMLFolder,String scheduleName, String oldTemplateName,String newTemplateName){

        File scheduleFile = new File(XMLFolder, scheduleName + ".xml");
        if (!scheduleFile.exists())
            return true;

        Document scheduleDocument = this.m_XMLHelper.getXmlDocument(scheduleFile);
        if (scheduleDocument == null)
            return true;

        try {
            FileOperator.copyFile(scheduleFile.getPath(),InternalKeyWords.DefaultXmlTempPath+scheduleName + ".xml");

            Element rootElement = scheduleDocument.getDocumentElement();

            Element runElement = this.m_XMLHelper.getSubNode(rootElement, "Run");

            Element idlElement = this.m_XMLHelper.getSubNode(runElement, "IdlePBU");
            if (idlElement.getAttribute("Id").equals(oldTemplateName))
                idlElement.setAttribute("Id", newTemplateName);

            ArrayList<Element> blockElementList = this.m_XMLHelper.getSubNodeList(runElement, "TimeLine");
            if (blockElementList == null)
                return true;

            ArrayList<Element> blockItemElementList = null;
            for (Element blockElement : blockElementList) {
                blockItemElementList = this.m_XMLHelper.getSubNodeList(blockElement, "PBU");
                for (Element item : blockItemElementList) {
                    if (item.getAttribute("Id").equals(oldTemplateName))
                        item.setAttribute("Id", newTemplateName);
                }
            }

            return this.m_XMLHelper.saveXmlDocument(scheduleDocument, scheduleFile.getPath());
        }catch (Exception ex){
            FileOperator.copyFile(InternalKeyWords.DefaultXmlTempPath+scheduleName + ".xml",scheduleFile.getPath());
            ex.printStackTrace();
            LogOperator.WriteLogfortxt("XMLSchedule==>refreshTemplateInSchedule:"+ex.getMessage());
            return false;
        }
    }
}
