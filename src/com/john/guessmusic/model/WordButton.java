package com.john.guessmusic.model;

import android.widget.Button;

/**
 * ÎÄ×Ö°´Å¥
 * @author John
 *
 */

public class WordButton {

	public int mIndex;
	public boolean mIsVisiable;
	public String mWordString;
	
	public Button mViewButton;
	
	public WordButton() {
		mIsVisiable = true;
		mWordString = "";
	}
}
