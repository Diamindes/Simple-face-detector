package simpleDetector.openCvModule;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JTextArea;

public class Statistics {

    Map<String, JTextArea> logMap = new HashMap<>();
    public int counter = 0;

    public void addLog(String logName, JTextArea logWindow) {
        logMap.put(logName, logWindow);
    }

    public JTextArea getLog(String logName) {
        JTextArea log = logMap.get(logName);
        if (log == null) {
            return null;
        }

        return log;
    }
}
/*
                log.getValue().setText("");

                log.append(" Face:   " + userName );
                String[] mark =LocalDateTime.now().toString().split("T");
                log2.append( mark[0] +" "+ mark[1]+ " | "+userName+"\n");
                
                index = 0;

      log1.setText("");
                    log1.append(" Face:   " + userName );
                    String[] mark =LocalDateTime.now().toString().split("T");
                    log2.append( mark[0] +" "+ mark[1]+ " | "+userName+"\n");
                    index = 0;
                    statistic.clear();
 */
