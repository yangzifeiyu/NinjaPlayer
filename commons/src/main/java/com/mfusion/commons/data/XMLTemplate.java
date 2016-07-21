package com.mfusion.commons.data;

import android.content.res.AssetManager;
import android.graphics.Color;

import com.mfusion.commons.entity.exception.IllegalTemplateException;
import com.mfusion.commons.entity.exception.PathAccessException;
import com.mfusion.commons.entity.exception.TemplateNotFoundException;
import com.mfusion.commons.entity.values.ComponentType;
import com.mfusion.commons.entity.values.ResourceSourceType;
import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.tools.FileOperator;
import com.mfusion.commons.tools.FileZipHelper;
import com.mfusion.commons.tools.ImageHelper;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.tools.XmlOperator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by guoyu on 7/12/2016.
 */
public class XMLTemplate {
    private volatile static XMLTemplate singleton;

    public static XMLTemplate getInstance() {
        if (singleton == null) {
            synchronized (XMLTemplate.class) {
                if (singleton == null) {
                    singleton = new XMLTemplate();
                }
            }
        }
        return singleton;
    }

    private XmlOperator m_XMLHelper;

    public XMLTemplate(){

        this.m_XMLHelper=new XmlOperator();
    }
    /**
     * get full information about template in default folder
     * @param tempId template name
     * @return a entity of template
     * @throws TemplateNotFoundException
     */
    public TemplateEntity getTemplateById(String tempId) throws Exception{
        return this.getTemplateById(tempId,InternalKeyWords.DefaultTemplateXmlPath);
    }
    /**
     * get full information about template in specified folder.
     * @param tempId template name
     * @param XMLFolder template folder path
     * @return a entity of template
     * @throws TemplateNotFoundException
     * @throws IllegalTemplateException
     */
    public TemplateEntity getTemplateById(String tempId,String XMLFolder) throws Exception{
        // TODO Auto-generated method stub
        if(this.existTemplate(XMLFolder,tempId))
            return this.getTemplate(this.getTemplateFolder(XMLFolder,tempId),tempId);
        return null;
    }
    /**
     * check whether template is exist in default folder.
     * @param tempId template name
     * @return true|false
     * @throws TemplateNotFoundException
     */
    public Boolean existTemplate(String tempId) throws Exception{
        return this.existTemplate(InternalKeyWords.DefaultTemplateXmlPath,tempId);
    }
    /**
     * check whether template is exist in specified folder.
     * @param tempId template name
     * @param XMLFolder template folder path
     * @return true|false
     * @throws TemplateNotFoundException
     */
    public Boolean existTemplate(String XMLFolder,String tempId) throws Exception{
        // TODO Auto-generated method stub

        String templateFolder=this.getTemplateFolder(XMLFolder,tempId);
        if(FileOperator.existFile(templateFolder))
            return FileOperator.existFile(this.getTemplateXmlPath(templateFolder,tempId));

        throw new TemplateNotFoundException(tempId);
    }
    /**
     * add new template in default folder.
     * @param tempInfo full information about template
     * @return true|false
     * @throws IllegalTemplateException
     */
    public Boolean addTemplate(TemplateEntity tempInfo) throws Exception{
        return this.addTemplate(tempInfo,InternalKeyWords.DefaultTemplateXmlPath);
    }
    /**
     * add new template in specified folder.
     * @param tempInfo full information about template
     * @param XMLFolder template folder path
     * @return true|false
     * @throws PathAccessException
     * @throws IllegalTemplateException
     */
    public Boolean addTemplate(TemplateEntity tempInfo,String XMLFolder) throws Exception{
        // TODO Auto-generated method stub
        if(tempInfo==null)
            throw new IllegalTemplateException("Null");

        try{

            if(this.existTemplate(XMLFolder,tempInfo.id)){
                tempInfo.id = FileOperator.CheckFileName(XMLFolder,tempInfo.id);
            }

        }catch (Exception ex){}

        return this.updateTemplate(tempInfo,XMLFolder);
    }
    /**
     * update template in default folder.
     * @param tempInfo full information about template
     * @return true|false
     * @throws IllegalTemplateException
     */
    public Boolean updateTemplate(TemplateEntity tempInfo) throws Exception{
        return this.updateTemplate(tempInfo,InternalKeyWords.DefaultTemplateXmlPath);
    }
    /**
     * update template in specified folder.
     * @param tempInfo full information about template
     * @param XMLFolder template folder path
     * @return true|false
     * @throws PathAccessException
     * @throws IllegalTemplateException
     */
    public Boolean updateTemplate(TemplateEntity tempInfo,String XMLFolder) throws Exception{
        // TODO Auto-generated method stub
        if(tempInfo==null)
            throw new IllegalTemplateException("Null");

        String tempFolder=this.getTemplateFolder(XMLFolder,tempInfo.id);
        FileOperator.createDir(tempFolder);
        Document tempDocument=this.writeTemplateDocument(tempFolder,tempInfo);

        return this.m_XMLHelper.saveXmlDocument(tempDocument, this.getTemplateXmlPath(tempFolder,tempInfo.id));
    }
    /**
     * rename template in default folder.
     * @return true|false
     * @throws TemplateNotFoundException
     */
    public Boolean renameTemplate(String oldName, String newName) throws Exception{
        return this.renameTemplate(oldName,newName,InternalKeyWords.DefaultTemplateXmlPath);
    }
    /**
     * rename template in specified folder.
     * @param XMLFolder template folder path
     * @return true|false
     * @throws TemplateNotFoundException
     */
    public Boolean renameTemplate(String oldName, String newName,String XMLFolder) throws Exception{
        // TODO Auto-generated method stub
        if(this.existTemplate(XMLFolder,oldName)){

            if(oldName.toLowerCase().equalsIgnoreCase(newName))
                return true;

            newName = FileOperator.CheckFileName(XMLFolder,newName);

            File tempPath=new File(XMLFolder,oldName+File.separator+oldName+".xml");
            tempPath.renameTo(new File(XMLFolder,oldName +File.separator+newName+".xml"));

            File tempFolder=new File(XMLFolder,oldName);
            tempFolder.renameTo(new File(XMLFolder, newName));

            return true;
        }
        return false;
    }
    /**
     * delete template in default folder.
     * @return true|false
     * @throws Exception
     */
    public Boolean deleteTemplate(String tempId) {
        return this.deleteTemplate(tempId,InternalKeyWords.DefaultTemplateXmlPath);
    }
    /**
     * delete template in specified folder.
     * @param XMLFolder template folder path
     * @return true|false
     * @throws Exception
     */
    public Boolean deleteTemplate(String tempId,String XMLFolder) {
        // TODO Auto-generated method stub
        try {

            return FileOperator.deleteFile(this.getTemplateFolder(XMLFolder,tempId));

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get all template info about layout and name in default folder.
     * @param
     * @return a list of template object with layout and name.
     * @throws PathAccessException
     */
    public ArrayList<VisualTemplate> getAllTemplates() throws Exception{
        return this.getAllTemplates(InternalKeyWords.DefaultTemplateXmlPath);
    }

    /**
     * Get all template info about layout and name in specified folder.
     * @param XMLFolder folder path that save template
     * @return a list of template object with layout and name.
     * @throws PathAccessException
     * @throws TemplateNotFoundException
     */
    public ArrayList<VisualTemplate> getAllTemplates(String XMLFolder) throws Exception{
        // TODO Auto-generated method stub
        File[] templateFolders=FileOperator.getAllSubFolder(XMLFolder);
        if(templateFolders==null)
            return null;

        VisualTemplate template=null;
        ArrayList<VisualTemplate> tempList=new ArrayList<VisualTemplate>();
        for (File folder : templateFolders) {
            template=new VisualTemplate();
            template.id=folder.getName();
            template.path=folder.getPath()+File.separator;
            template.thumbImageBitmap=ImageHelper.getBitmap(template.path+InternalKeyWords.TemplateResourceFolder+"thumb.jpg", ResourceSourceType.local,null);
            tempList.add(template);
        }

        return tempList;
    }
    /**
     * import a zip file for template to a default folder.
     * @param importedTempPath path of the zip file
     * @return full information adout imported template..
     * @throws PathAccessException
     * @throws IllegalTemplateException
     */
    public TemplateEntity importTemplate(String importedTempPath){
        return this.importTemplate(importedTempPath,InternalKeyWords.DefaultTemplateXmlPath);
    }
    /**
     * import a zip file for template to a specified folder.
     * @param importedTempPath path of the zip file
     * @return full information adout imported template.
     * @throws PathAccessException
     * @throws IllegalTemplateException
     */
    public TemplateEntity importTemplate(String importedTempPath,String XMLFolder) {
        // TODO Auto-generated method stub

        try {

            //Check template name whether is exist
            File zipFile=new File(importedTempPath);
            if(!zipFile.exists()){
                throw new PathAccessException(importedTempPath);
            }

            String newTempName=zipFile.getName().substring(0, zipFile.getName().lastIndexOf("."));

            //UnZip
            String inputPath=InternalKeyWords.DefaultXmlTempPath+newTempName+File.separator;
            FileOperator.deleteFile(inputPath);
            if(!FileZipHelper.deCompressionFolder(zipFile, InternalKeyWords.DefaultXmlTempPath))
                throw new PathAccessException("Decompression "+ importedTempPath);

            if(FileOperator.existFile(this.getTemplateXmlPath(this.getTemplateFolder(XMLFolder,newTempName),newTempName))){
                String oldName=newTempName;
                newTempName = FileOperator.CheckFileName(XMLFolder,newTempName);
                this.renameTemplate(oldName, newTempName,InternalKeyWords.DefaultXmlTempPath);
                inputPath=InternalKeyWords.DefaultXmlTempPath+newTempName+File.separator;
            }

            //Check Template Xml and return template entity
            TemplateEntity template = this.getTemplate(inputPath, newTempName);

            FileOperator.copyFolder(inputPath, this.getTemplateFolder(XMLFolder,newTempName));
            FileOperator.deleteFile(inputPath);

            return template;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return null;

    }
    /**
     * export a template to a specified folder.
     * @param exportFolder path that template will export to
     * @return true|false.
     * @throws PathAccessException
     * @throws TemplateNotFoundException
     */
    public Boolean exportTemplate(String tempId, String exportFolder){
        return this.exportTemplate(tempId,InternalKeyWords.DefaultTemplateXmlPath,exportFolder);
    }
    /**
     * export a template to a specified folder.
     * @param XMLFolder template folder path
     * @param exportFolder path that template will export to
     * @return true|false.
     * @throws PathAccessException
     * @throws TemplateNotFoundException
     */
    public Boolean exportTemplate(String tempId,String XMLFolder, String exportFolder) {
        // TODO Auto-generated method stub

        String outputPath= InternalKeyWords.DefaultXmlTempPath+tempId+File.separator;
        try {
            if(!this.existTemplate(XMLFolder,tempId))
                throw new TemplateNotFoundException(tempId);

            String tempFolder=this.getTemplateFolder(XMLFolder,tempId);
            Document tempDocument=this.getTemplateDocumentNoMedias(this.getTemplateXmlPath(tempFolder,tempId));

            FileOperator.createWithDeleteDir(outputPath);
            Boolean saveResult=this.m_XMLHelper.saveXmlDocument(tempDocument, this.getTemplateXmlPath(outputPath,tempId));
            if(saveResult){
                FileOperator.copyFolder(tempFolder+ InternalKeyWords.TemplateResourceFolder, outputPath+InternalKeyWords.TemplateResourceFolder);
                return FileZipHelper.compressionFolder(exportFolder+File.separator+tempId+".zip", outputPath);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        finally{
            FileOperator.deleteFile(outputPath);
        }
        return false;
    }
    /**
     * get local template layout sample.
     * @param assetManager path that template will export to
     * @param path path that template will export to
     * @return a list of object that contains template basic info.
     * @throws Exception
     */
    public ArrayList<VisualTemplate> getAllLocalSampleLayouts(AssetManager assetManager, String path) throws Exception{
        // TODO Auto-generated method stub
        String[] files=assetManager.list(path);
        if(files==null||files.length==0)
            return null;

        VisualTemplate templateEntity=null;
        ArrayList<VisualTemplate> templateList=new ArrayList<VisualTemplate>();
        for (String name : files) {
            templateEntity=new VisualTemplate();
            templateEntity.id=name;
            templateEntity.path=path+File.separator+name+File.separator;
            templateEntity.thumbImageBitmap=ImageHelper.getBitmap(templateEntity.path+InternalKeyWords.TemplateResourceFolder+"thumb.jpg", ResourceSourceType.internal,assetManager);
            templateList.add(templateEntity);
        }

        return templateList;
    }
    /**
     * get detail information about local template layout sample.
     * @param visualTemplate object that contains template basic info.
     * @param assetManager
     * @return a template object
     * @throws Exception
     */
    public TemplateEntity getSampleLayout(VisualTemplate visualTemplate,AssetManager assetManager){
        return  this.readTemplateInfoFromAsset(assetManager, visualTemplate.path, visualTemplate.id);
    }
    /**
     * get full information about template in specified folder.
     * @param tempName template name
     * @param tempFolder folder path
     * @return a entity of template
     * @throws TemplateNotFoundException
     * @throws IllegalTemplateException
     */
    private TemplateEntity getTemplate(String tempFolder,String tempName) throws Exception{
        // TODO Auto-generated method stub
        return this.readTemplateInfo(tempFolder, tempName);
    }

    private TemplateEntity readTemplateInfo(String tempFolder,String tempXmlName) throws Exception{
        try {

            Document templateDoc=this.m_XMLHelper.getXmlDocument(tempFolder+tempXmlName+".xml");
            if(templateDoc==null)
                throw new TemplateNotFoundException(tempXmlName);

            return this.readTemplateInfo(tempFolder,tempXmlName, templateDoc, ResourceSourceType.local,null);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw e;
        }
    }

    private TemplateEntity readTemplateInfoFromAsset(AssetManager assetManager,String path,String tempName) {
        try {
            String tempXmlPath=path+tempName+".xml";
            InputStream xmlStream=assetManager.open(tempXmlPath);

            Document templateDocument=this.m_XMLHelper.getXmlDocument(xmlStream);

            return this.readTemplateInfo(path,tempName, templateDocument, ResourceSourceType.internal,assetManager);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return null;
    }

    private TemplateEntity readTemplateInfo(String tempFolder,String tempXmlName,Document templateDoc,ResourceSourceType sourceType,AssetManager assetManager) throws Exception{
        try {

            Element rootElement=templateDoc.getDocumentElement();

            TemplateEntity entity=new TemplateEntity();
            entity.id=tempXmlName;

            String thumbImagePath=tempFolder+InternalKeyWords.TemplateResourceFolder+"thumb.jpg";
            entity.thumbImageBitmap= ImageHelper.getBitmap(thumbImagePath, sourceType,assetManager);

            Element sizeElement=this.m_XMLHelper.getSubNode(rootElement, "Size");
            if(sizeElement!=null){
                entity.width=Integer.valueOf(sizeElement.getAttribute("Width"));
                entity.height=Integer.valueOf(sizeElement.getAttribute("Height"));
            }

            Element bgElement=this.m_XMLHelper.getSubNode(rootElement, "Background");
            if(bgElement!=null){
                entity.backColor=this.convertColorStrToInt(bgElement.getAttribute("Color"));
                String imagePath=bgElement.getAttribute("ImagePath");
                if(imagePath!=null&&!imagePath.isEmpty()){
                    entity.backImagePath=tempFolder+imagePath;
                    entity.backImageBitmap=ImageHelper.getBitmap(entity.backImagePath, sourceType,assetManager);
                }
            }

            ArrayList<Element> compElementList=this.m_XMLHelper.getSubNodeList(rootElement, "Component");
            if(compElementList!=null){
                ComponentEntity componentEntity=null;
                for (Element element : compElementList) {
                    componentEntity=new ComponentEntity();
                    componentEntity.type= ComponentType.fromString(element.getAttribute("Type"));
                    if(componentEntity.type== ComponentType.Unkown)
                        continue;
                    componentEntity.backColor=this.convertColorStrToInt(element.getAttribute("BackColor"));
                    componentEntity.left=Integer.valueOf(element.getAttribute("Left"));
                    componentEntity.top=Integer.valueOf(element.getAttribute("Top"));
                    componentEntity.width=Integer.valueOf(element.getAttribute("Width"));
                    componentEntity.height=Integer.valueOf(element.getAttribute("Height"));
                    componentEntity.property=this.m_XMLHelper.getSubNodeList(element, null);

                    entity.compList.add(componentEntity);
                }
            }

            return entity;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw new IllegalTemplateException(e.getMessage());
        }
    }

    private Document writeTemplateDocument(String tempFolder,TemplateEntity templateEntity) throws Exception{
        try {

            Document tempDocument=this.m_XMLHelper.createXmlDocument("Template");
            if(tempDocument==null)
                return null;

            Element rootElement=tempDocument.getDocumentElement();
            ImageHelper.createTemplateThumb(templateEntity, tempFolder+InternalKeyWords.TemplateResourceFolder+"thumb.jpg");

            Element sizeElement=tempDocument.createElement("Size");
            sizeElement.setAttribute("Width", String.valueOf(templateEntity.width));
            sizeElement.setAttribute("Height", String.valueOf(templateEntity.height));
            rootElement.appendChild(sizeElement);

            Element bgElement=tempDocument.createElement("Background");
            bgElement.setAttribute("Color", String.valueOf(templateEntity.backColor));

            String imagePath="";
            if(templateEntity.backImagePath!=null&&!templateEntity.backImagePath.isEmpty()){
                try {
                    File bgImage=new File(templateEntity.backImagePath);
                    imagePath=bgImage.getName();
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }

            if(templateEntity.backImageBitmap!=null){

                if(imagePath.isEmpty())
                    imagePath="bg.jpg";
                imagePath=InternalKeyWords.TemplateResourceFolder+imagePath;
                ImageHelper.saveBitmap(templateEntity.backImageBitmap, tempFolder+imagePath);
            }

            bgElement.setAttribute("ImagePath", imagePath);
            rootElement.appendChild(bgElement);

            Element compListElement=tempDocument.createElement("Components");
            rootElement.appendChild(compListElement);

            Element compElement=null;
            for (ComponentEntity compEntity : templateEntity.compList) {
                compElement=tempDocument.createElement("Component");
                compElement.setAttribute("Type", compEntity.type.toString());
                compElement.setAttribute("BackColor", String.valueOf(compEntity.backColor));
                compElement.setAttribute("Top", String.valueOf(compEntity.top));
                compElement.setAttribute("Left", String.valueOf(compEntity.left));
                compElement.setAttribute("Width", String.valueOf(compEntity.width));
                compElement.setAttribute("Height", String.valueOf(compEntity.height));

                this.m_XMLHelper.copyNode(tempDocument,compElement,compEntity.property);

                compListElement.appendChild(compElement);
            }

            return tempDocument;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw new IllegalTemplateException(e.getMessage());
        }
    }

    private Document getTemplateDocumentNoMedias(String tempXmlPath) {
        try {

            Document templateDoc=this.m_XMLHelper.getXmlDocument(tempXmlPath);
            if(templateDoc==null)
                return null;

            Element rootElement=templateDoc.getDocumentElement();

            ArrayList<Element> compElementList=this.m_XMLHelper.getSubNodeList(rootElement, "Component");
            if(compElementList!=null){
                ArrayList<Element> removedPropertyElementList=new ArrayList<Element>();
                ArrayList<Element> propertyElementList=null;
                for (Element element : compElementList) {
                    propertyElementList=this.m_XMLHelper.getSubNodeList(element, "Property");
                    for (Element property : propertyElementList) {
                        String propertyName=property.getAttribute("name");
                        if(propertyName.equalsIgnoreCase("PlayList"))
                            removedPropertyElementList.add(property);
                    }

                    for (Element removed : removedPropertyElementList)
                        element.removeChild(removed);

                    removedPropertyElementList.clear();
                }
            }

            return templateDoc;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    private String getTemplateFolder(String XMLFolder,String tempName){

        return XMLFolder+tempName+File.separator;
    }

    private String getTemplateXmlPath(String folder,String tempName){

        return folder+tempName+".xml";
    }

    private Integer convertColorStrToInt(String colorStr) {
        if(colorStr.isEmpty()==false){
            String[] colors=colorStr.split(",");
            if(colors!=null){
                if(colors.length==4)
                    return Color.argb(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]), Integer.parseInt(colors[3]));
                if(colors.length==1)
                    return Integer.valueOf(colorStr);
            }
        }

        return Color.BLACK;
    }
}
