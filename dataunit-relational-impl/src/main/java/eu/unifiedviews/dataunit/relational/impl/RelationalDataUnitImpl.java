package eu.unifiedviews.dataunit.relational.impl;

import java.sql.Connection;

import eu.unifiedviews.commons.dataunit.AbstractWritableMetadataDataUnit;
import eu.unifiedviews.commons.dataunit.core.CoreServiceBus;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.RelationalDataUnit;

public class RelationalDataUnitImpl extends AbstractWritableMetadataDataUnit implements ManageableWritableRelationalDataUnit {

    public RelationalDataUnitImpl(String dataUnitName, String databaseURL, String writeContextString, CoreServiceBus coreServices) {
        super(dataUnitName, writeContextString, coreServices);
    }

    @Override
    public String getBaseDbTableName() throws DataUnitException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addExistingDbTable(String symbolicName, String dbTableName) throws DataUnitException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String addNewDbTable(String symbolicName) throws DataUnitException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateExistingTableName(String symbolicName, String dbTableName) throws DataUnitException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public RelationalDataUnit.Iteration getIteration() throws DataUnitException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Connection getDatabaseConnection() throws DataUnitException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isType(Type dataUnitType) {
        // TODO Auto-generated method stub
        return false;
    }

}
