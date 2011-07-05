package it.quickorder.gui;

import javax.swing.JInternalFrame;

@SuppressWarnings("serial")
public class TransparentJInternalFrame extends JInternalFrame 
{
	public TransparentJInternalFrame()
	{
		super();
		((javax.swing.plaf.basic.BasicInternalFrameUI) 
			      this.getUI()).setNorthPane(null);
		((javax.swing.plaf.basic.BasicInternalFrameUI) 
			      this.getUI()).setSouthPane(null);
		setOpaque(false);	
	}
}
