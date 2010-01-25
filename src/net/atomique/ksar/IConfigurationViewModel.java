package net.atomique.ksar;

import java.io.File;

import javax.swing.LookAndFeel;

public interface IConfigurationViewModel {

	int getImageWidth();
	void setImageWidth(int value);
	
	int getImageHeight();
	void setImageHeight(int value);
	
	LookAndFeel getLookAndFeel();
	void setLookAndFeel(LookAndFeel lookAndFeel);
	
	boolean isHtmlIndexEnabled();
	void setHtmlIndexEnabled(boolean value);
	
	String getPdfBottomText();
	void setPdfBottomText(String text);
	
	String getPdfUpperRightText();
	void setPdfUpperRightText(String text);
	
	String getPdfIndexPageText();
	void setPdfIndexPageText(String text);
	
	File getBackgroundImageFile();
	void setBackgroundImageFile(File file);
	
	File getSshKeyFile();
	void setSshKeyFile(File file);
	
	boolean isSshStrictHostCheckEnabled();
	void setSshStrictHostCheckEnabled(boolean value);
	
	long getDataUpdateInterval();
	void setDataUpdateInterval(long value);
}
