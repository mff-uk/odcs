package cz.cuni.mff.xrg.odcs.csv.impl;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.csv.interfaces.CsvDataUnit;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CsvFile implements CsvDataUnit, Closeable {

    @Override
    public void madeReadOnly() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void merge(DataUnit unit) throws IllegalArgumentException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void release() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clean() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void save(File directory) throws RuntimeException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void load(File directory) throws FileNotFoundException, RuntimeException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isReadOnly() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DataUnitType getType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDataUnitName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
