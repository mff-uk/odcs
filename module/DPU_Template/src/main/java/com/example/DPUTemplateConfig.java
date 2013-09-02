package com.example;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;

/**
 * Put your DPU's configuration here.
 */
public class DPUTemplateConfig implements DPUConfigObject {

    private int width;
    
    private int height;	
	
	// DPUTemplateConfig must provide public non-parametric constructor
    public DPUTemplateConfig() {
        width = 100;
        height = 100;
    }
    
    public DPUTemplateConfig(int w, int h) {
        width = w;
        height = h;
    }
        
    public int getWidth() {
        return width;    
    }
    
     public int getHeight() {
        return height;    
    }

	@Override
	public boolean isValid() {
		// TODO : Return true in case the current configuration is valid
		return true;
	}

}
