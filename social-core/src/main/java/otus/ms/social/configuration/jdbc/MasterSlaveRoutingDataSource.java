package otus.ms.social.configuration.jdbc;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


public class MasterSlaveRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceTypeThreadLocal.getDataSourceType();
    }
}
