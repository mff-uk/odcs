package cz.cuni.mff.xrg.odcs.dpu.filetofilestransformer;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.DirectoryHandler;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.FileHandler;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.Handler;

class FileDataUnitOnlyFilesIterator implements Iterator<FileHandler> {
    private Deque<Iterator<Handler>> stack = new LinkedList<>();
    private Iterator<Handler> currentIterator;
    private FileHandler nextAvailableItem = null;
    
    public FileDataUnitOnlyFilesIterator(DirectoryHandler rootDir) {
        currentIterator= rootDir.iterator();    
    }
    
    
    @Override
    public boolean hasNext() {
        return nextInternal() != null;
    }

    @Override
    public FileHandler next() {
        FileHandler result = nextInternal();
        nextAvailableItem = null;
        if (result == null) {
            throw new NoSuchElementException();
        }
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    private FileHandler nextInternal() {
        if (nextAvailableItem == null) {
            nextAvailableItem = hasNextInternal();
        }
        return nextAvailableItem;
    }
    
    private FileHandler hasNextInternal() {
        while (currentIterator != null) {
            while (currentIterator.hasNext()) {
                Handler item = currentIterator.next();
                if (item instanceof FileHandler) {
                    return (FileHandler) item;
                } else if (item instanceof DirectoryHandler) {
                    stack.push(currentIterator);
                    currentIterator = ((DirectoryHandler) item).iterator();
                }
            }
            currentIterator = stack.poll();
        }
        return null;
    }
}