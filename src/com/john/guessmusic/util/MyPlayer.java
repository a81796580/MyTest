package com.john.guessmusic.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;


/**
 * 音乐播放类
 * @author John
 *
 */
public class MyPlayer {
	
	public final static int INDEX_STONE_ENTER = 0;
	public final static int INDEX_STONE_CANCEL = 1;
	public final static int INDEX_STONE_COIN = 2;
	
	//音效的文件名
	private final static String[] SONG_NAMES = {"enter.mp3","cancel.mp3","coin.mp3"};
	
	//音效
	private static MediaPlayer[] mToneMediaPlayer = new MediaPlayer[SONG_NAMES.length];
	
	//歌曲播放
	private static MediaPlayer mMusicMediaPlayer;
	
	/**
	 * 播放声音效果
	 * @param context
	 * @param index
	 */
	public static void playTone(Context context, int index){
		//加载声音
		AssetManager assetManager = context.getAssets();
		
		if(mToneMediaPlayer[index] == null){
			mToneMediaPlayer[index] = new MediaPlayer();
			
			try {
				AssetFileDescriptor fileDescriptor = assetManager.openFd(SONG_NAMES[index]);
				mToneMediaPlayer[index].setDataSource(fileDescriptor.getFileDescriptor(),
													  fileDescriptor.getStartOffset(),
													  fileDescriptor.getDeclaredLength());
				//开始播放音效
				mToneMediaPlayer[index].prepare();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mToneMediaPlayer[index].start();
		
	}
	
	/**
	 * 播放歌曲
	 * @param context
	 * @param fileName
	 */
	public static void playSong(Context context, String fileName){
		if(mMusicMediaPlayer == null){
			mMusicMediaPlayer = new MediaPlayer();
		}
		
		//强制重置
		mMusicMediaPlayer.reset();
		
		//加载声音文件
		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
			mMusicMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
											fileDescriptor.getStartOffset(),
											fileDescriptor.getLength());
			mMusicMediaPlayer.prepare();
			//声音播放
			mMusicMediaPlayer.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void stopTheSong(Context context){
		if(mMusicMediaPlayer != null){
			mMusicMediaPlayer.stop();
		}
	}

}
