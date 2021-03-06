package org.jbpm.process.audit.query;

import static org.kie.internal.query.QueryParameterIdentifiers.DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;

import java.sql.Timestamp;
import java.util.Date;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.command.AuditCommand;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.command.Context;
import org.kie.internal.query.AbstractQueryBuilderImpl;
import org.kie.internal.runtime.manager.audit.query.AuditQueryBuilder;

public class AbstractAuditDeleteBuilderImpl<T> extends AbstractQueryBuilderImpl<T> implements AuditQueryBuilder<T> {

    protected final CommandExecutor executor; 
    protected final JPAAuditLogService jpaAuditService; 
    
    protected AbstractAuditDeleteBuilderImpl(JPAAuditLogService jpaService) { 
        this.executor = null;
        this.jpaAuditService = jpaService;
    }
    
    protected AbstractAuditDeleteBuilderImpl(CommandExecutor cmdExecutor) { 
        this.executor = cmdExecutor;
        this.jpaAuditService = null;
    }
   
    // service methods
    
    protected JPAAuditLogService getJpaAuditLogService() { 
        JPAAuditLogService jpaAuditLogService = this.jpaAuditService;
        if( jpaAuditLogService == null ) { 
           jpaAuditLogService = this.executor.execute(getJpaAuditLogServiceCommand);
        }
        return jpaAuditLogService;
    }
    
    private AuditCommand<JPAAuditLogService> getJpaAuditLogServiceCommand = new AuditCommand<JPAAuditLogService>() {
        private static final long serialVersionUID = 101L;
        @Override
        public JPAAuditLogService execute( Context context ) {
            setLogEnvironment(context);
            return (JPAAuditLogService) this.auditLogService;
        }
    };

    // query builder methods
    
    @Override
    @SuppressWarnings("unchecked")
    public T processInstanceId( long... processInstanceId ) {
        addLongParameter(PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceId);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T processId( String... processId ) {
        addObjectParameter(PROCESS_ID_LIST, "process id", processId);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T date( Date... date ) {
        addObjectParameter(DATE_LIST, "date", date);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T dateRangeStart( Date rangeStart ) {
        addRangeParameter(DATE_LIST, "date range start", rangeStart, true);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T dateRangeEnd( Date rangeStart ) {
        addRangeParameter(DATE_LIST, "date range end", rangeStart, false);
        return (T) this;
    }

    protected boolean checkIfNotNull(Object...parameter) {
    	if( parameter == null ) { 
            return true;
        }
        for( int i = 0; i < parameter.length; ++i ) { 
           if( parameter[i] == null ) { 
        	   return true;
           }
        }
        
        return false;
    }
    
    protected Date[] ensureDateNotTimestamp(Date...date) {
		Date[] validated = new Date[date.length];
		for (int i = 0; i < date.length; ++i) {
			if (date[i] instanceof Timestamp) {
				validated[i] = new Date(date[i].getTime());
			} else {
				validated[i] = date[i];
			}
		}
		
		return validated;
    }
  
}
