package jFFT;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LOG {

    public enum LOG_LEVEL {
        FATAL,
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE,
        ALL
    };

    String[] slevel = {
        "FATAL",
        "ERROR",
        "WARN",
        "INFO",
        "DEBUG",
        "TRACE",
        "ALL"
    };

    static LOG_LEVEL level = LOG_LEVEL.INFO;
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("[yyyy/MM/dd HH:mm:ss]");


    static public void init( LOG_LEVEL l ){
        level = l;
    }

    static void log_print( LOG_LEVEL l, String msg ){
        if ( l.ordinal() <= level.ordinal() ){
            System.err.println(
                String.format( "%s %5s: %s",
                    dtf.format(LocalDateTime.now()), l, msg
                )
            );
        }
    }


    static void fatal ( String msg ) {
        log_print( LOG_LEVEL.FATAL, msg );
    }
    static void error ( String msg ) {
        log_print( LOG_LEVEL.ERROR, msg );
    }
    static void warn ( String msg ) {
        log_print( LOG_LEVEL.WARN, msg );
    }
    static void info ( String msg ) {
        log_print( LOG_LEVEL.INFO, msg );
    }
    static void debug ( String msg ) {
        log_print( LOG_LEVEL.DEBUG, msg );
    }
    static void trace ( String msg ) {
        log_print( LOG_LEVEL.TRACE, msg );
    }

}
