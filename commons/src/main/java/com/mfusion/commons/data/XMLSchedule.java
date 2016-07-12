package com.mfusion.commons.data;

import com.mfusion.commons.entity.schedule.Schedule;


/**
 * Created by jimmy on 7/12/2016.
 *
 * A class that converts Schedule object into XML file and vice versa.
 */
public class XMLSchedule {

    private String m_XMLPath="";
    /**
     *
     */
    public XMLSchedule()
    {
        //Assign a global value here
        this.m_XMLPath ="";
    }
    /**
     * @param XMLPath
     * @return A schedule Object that is to be rendered on schedule designer
     */
    public Schedule LoadSchedule(String XMLPath) throws Exception
    {
        throw  new Exception("not implemented");
    }

    /**
     * Default function to be called externally
     * As there is only one schedule xml for initial version, this is supposed
     * to be used by default unless otherwise required.
     * @return
     */
    public Schedule LoadSchedule() throws Exception
    {
        return this.LoadSchedule(this.m_XMLPath);
    }


    /**
     * Save a schedule object into an XML file
     * @param sch Schedule object
     * @param XMLPath target xml path
     * @return
     */
    public boolean SaveSchedule(Schedule sch,String XMLPath) throws Exception
    {
        throw  new Exception("not implemented");
    }

    /**
     * Default function to be used for saving a schedule XML into default xml location
     * @param sch
     * @return
     * @throws Exception
     */
    public  boolean SaveSchedule(Schedule sch) throws Exception
    {
        return this.SaveSchedule(sch);
    }
}
