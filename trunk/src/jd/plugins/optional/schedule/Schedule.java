package jd.plugins.optional.schedule;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import jd.config.MenuItem;
import jd.event.ControlListener;
import jd.plugins.PluginOptional;
import jd.utils.JDLocale;
import jd.utils.JDUtilities;

public class Schedule extends PluginOptional implements ControlListener {
    public static int getAddonInterfaceVersion(){
        return 0;
    }
    
    ScheduleControl sControl = new ScheduleControl();
    
    @Override
    public boolean initAddon() {
      
               logger.info("Schedule OK");
               JDUtilities.getController().addControlListener(this);
               return true;
          
     
    }

    @Override
    public String getRequirements() {
        return "JRE 1.5+";
    }

    @Override
    public ArrayList<MenuItem> createMenuitems() {
        ArrayList<MenuItem> menu = new ArrayList<MenuItem>();
        menu.add(new MenuItem(JDLocale.L("addons.schedule.menu.settings","Settings"),0).setActionListener(this));
        return menu;
    }

    @Override
    public String getCoder() {
        return "Tudels";
    }

    @Override
    public String getPluginID() {
        return "0.5";
    }

    @Override
    public String getVersion() {
        return "0.5";
    }

    public String getPluginName() {
        return JDLocale.L("addons.schedule.name","Schedule");
    }
    
    public void actionPerformed(ActionEvent e) {
        this.sControl.status.start();
        this.sControl.status.setInitialDelay(1000);
        this.sControl.setVisible(true);
    }

    @Override
    public void onExit() {
       
        
    }
    
}
