/**
 *
 * Copyright (c) 2009-2013 Freedomotic team http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * This Program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Freedomotic; see the file COPYING. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.freedomotic.objects;

import it.freedomotic.model.charting.UsageData;
import it.freedomotic.model.charting.UsageDataFrame;
import it.freedomotic.model.ds.Config;
import it.freedomotic.model.object.DataBehavior;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Matteo Mazzoni <matteo@bestmazzo.it>
 */
public class DataBehaviorLogic implements BehaviorLogic{
    
    private DataBehavior data;
    private boolean changed;
    private DataBehaviorLogic.Listener listener;
    private static  ObjectMapper om = new ObjectMapper();

    public interface Listener {
        
           public void onReceiveData(Config params, boolean fireCommand);
           
    }
    
    public DataBehaviorLogic(DataBehavior pojo){
        this.data=pojo;
    }
    
    public void addListener(DataBehaviorLogic.Listener dataBehaviorListener) {
        this.listener = dataBehaviorListener;
    }
    
    @Override
    public void filterParams(Config params, boolean fireCommand) {
        // send hardware command: ask Harvester for data /subscribe
        //if (params.getProperty("behaviorValue") != null){
        //change behavior value: send data to the Graph Object
        listener.onReceiveData(params, fireCommand);
        //}
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void setChanged(boolean value) {
        changed = value;
    }

    @Override
    public boolean isActive() {
        return data.isActive();
    }

    @Override
    public boolean isReadOnly() {
        return data.isReadOnly();
    }

    @Override
    public String getValueAsString() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            om.writeValue(os, data.getData());    
            return os.toString();
        } catch (IOException ex){
            Logger.getLogger(DataBehaviorLogic.class.getName()).log(Level.SEVERE, null, ex); 
        }
        return null;
    }
    
    public void setData(String JSON){
       UsageDataFrame df;
       
  //     om.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            df = om.readValue(JSON, UsageDataFrame.class);
            if ( df.getFrameType() == UsageDataFrame.FULL_UPDATE ){
                this.data.setData(df.getData());
            } else if ( df.getFrameType() == UsageDataFrame.INCREMENTAL_UPDATE ){
                this.data.addData(df.getData());
            }
            setChanged(true);
         } catch (IOException ex) {
            Logger.getLogger(DataBehaviorLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public List<UsageData> getData(){
        return data.getData();
    }
}
