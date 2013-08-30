package com.example;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;

/**
 * Put your DPU's configuration here.
 */
public class DPUTemplateConfig implements DPUConfigObject {

        DPUTemplateConfig() {
            width = 100;
            height = 100;
        }
        
        DPUTemplateConfig(int w, int h) {
            width = w;
            height = h;
        }
        
        private int width;
        private int height;
        
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
