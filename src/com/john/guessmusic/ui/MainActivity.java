package com.john.guessmusic.ui;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.john.guessmusic.R;
import com.john.guessmusic.data.Const;
import com.john.guessmusic.model.IAlertDialogButtonListener;
import com.john.guessmusic.model.IWordButtonClickListener;
import com.john.guessmusic.model.Song;
import com.john.guessmusic.model.WordButton;
import com.john.guessmusic.myui.MyGridView;
import com.john.guessmusic.util.MyLog;
import com.john.guessmusic.util.MyPlayer;
import com.john.guessmusic.util.Util;

public class MainActivity extends Activity implements IWordButtonClickListener {
	
	public final static String TAG = "MainActivity";
	
	/**��״̬   ��ȷ*/
	public final static int STATUS_ANSWER_RIGHT = 1;
	/**��״̬  ����*/
	public final static int STATUS_ANSWER_WRONG = 2;
	/**��״̬   ������*/
	public final static int STATUS_ANSWER_LACk  = 3;
	/**��˸����*/
	public final static  int SPASH_TIMES = 6;
	
	public final static int ID_DIALOG_DELETE_WORD = 1;
	
	public final static int ID_DIALOG_TIP_ANSWER = 2;
	
	public final static int ID_DIALOG_LACK_COINS = 3;
	
	//��Ƭ��ض���
	private Animation mCdAnim;
	private LinearInterpolator mCdLin;
	
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
		
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	//���ؽ���
	private View mPassView;
	
	//��Ƭ�ؼ�
	private ImageView mViewCd;
	
	//���˿ؼ�
	private ImageView mViewCdBar;
	
	//��ǰ������
	private TextView mCurrentStagePassView;
	
	private TextView mCurrentStageView;
	
	//��ǰ��������
	private TextView mCurrentSongNamePassView;
	
	//Play ����
	private ImageButton mBtnPlayStart;
	//�����Ƿ�������
	private boolean mIsRunning = false;
	
	//���ֿ�����
	private ArrayList<WordButton> mAllWords;
	private ArrayList<WordButton> mBtnSelectWords;
	
	private MyGridView mMyGridView;
	
	//��ѡ�����ֿ�UI����
	private LinearLayout mViewWordsContainer;
	
	//��ǰ����
	private Song mCurrentSong;
	
	//��ǰ�ص�����
	private int mCurrentStageIndex = -1;
	
	//��ǰ��ҵ�����
	private int mCurrentCoins = Const.TOTAL_COINS;
	
	//���View
	private TextView mViewCurrentCoins;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//��ȡ�浵
		int [] datas = Util.loadData(this);
		mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];
		
		//��ʼ���ؼ�
		mViewCd = (ImageView) findViewById(R.id.imageView1);
		mViewCdBar = (ImageView) findViewById(R.id.imageView2);
		
		mMyGridView = (MyGridView) findViewById(R.id.gridview);
		
		mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins + "");
		//ע�����
		mMyGridView.registOnWordButtonClick(this);
		
		mViewWordsContainer =(LinearLayout) findViewById(R.id.word_select_container);
		
		
		//��ʼ������
		mCdAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mCdLin = new LinearInterpolator();
		//mPanAnim��������
		mCdAnim.setInterpolator(mCdLin);
		mCdAnim.setAnimationListener(new AnimationListener() {
		
			public void onAnimationStart(Animation animation) {
					
			}		
			public void onAnimationRepeat(Animation animation) {
			}
		
			public void onAnimationEnd(Animation animation) {
				//���˻�ȥ
				mViewCdBar.startAnimation(mBarOutAnim);
			}
		});
		
		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setFillAfter(true);//���ֽ���״̬ ���ص�ԭλ
		
		//mBarInAnim��������
		mBarInAnim.setInterpolator(mBarInLin);
		mBarInAnim.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
							
					}

			public void onAnimationRepeat(Animation animation) {
						
						
					}
			public void onAnimationEnd(Animation animation) {
						//������Ƭ   ��Ƭ��ʼ��ת
						mViewCd.startAnimation(mCdAnim);
						
					}
				});
		
		
		mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setFillAfter(true);//���ֽ���״̬ ���ص�ԭλ

		//mBarOutLin��������
		mBarOutAnim.setInterpolator(mBarOutLin);
		mBarOutAnim.setAnimationListener(new AnimationListener() {
			
			public void onAnimationStart(Animation animation) {
					
			}

			public void onAnimationRepeat(Animation animation) {
				
				
			}
			
			
			public void onAnimationEnd(Animation animation) {
				//�������
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
				
			}
		});
		
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		mBtnPlayStart.setOnClickListener(new OnClickListener() {
		public void onClick(View arg0) {
				
				handlePlayButton();
			}
		});
		
		//��ʼ����Ϸ����
		initCurrentStageData();
		
		//����ɾ�������¼�
		handleDeleteWord();
		
		//������ʾ�����¼�
		handleTipAnswer();
		
		
	}
	
	public void onWordButtonClick(WordButton wordButton){
		setSelectWord(wordButton);
		
		//��ô�״̬
		int checkResult = checkTheAnswer();
		
		//����
		if(checkResult == STATUS_ANSWER_RIGHT){
			//�����Ӧ�Ľ���������
			handlePassEvent();
			Toast.makeText(this,wordButton.mIndex+ "�ѹ���", 1000).show();

		}else if(checkResult == STATUS_ANSWER_WRONG){
			//������ʾ
			sparkTheWords();
		}else if(checkResult == STATUS_ANSWER_LACk){
			//����������ɫΪ��ɫ(normal)
			for(int i = 0; i < mBtnSelectWords.size(); i++){
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
		}
	}
	
	/**
	 * ������ؽ��漰�¼�
	 */
	private void handlePassEvent(){
		//��ʾ���ؽ���
		mPassView = (LinearLayout)this.findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);
		
		//ֹͣδ��ɵĶ���
		mViewCd.clearAnimation();
		
		//ֹͣ���ڲ��ŵ�����
		MyPlayer.stopTheSong(MainActivity.this);
		
		//������Ч
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_STONE_COIN);
		
		//��ǰ�ص�����
		mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
		if( mCurrentStagePassView != null){
			mCurrentStagePassView.setText((mCurrentStageIndex + 1) +"" );
		}
		
		//��ʾ��������
		mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_name_pass);
		
		if(mCurrentSongNamePassView != null){
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}
		
		//��һ�ذ�������
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(judegAppPassed()){
					//����ͨ�ؽ���
					Util.startActivity(MainActivity.this, AllPassView.class);
				}else{
					//��ʼ��һ��
					mPassView.setVisibility(View.GONE);
					//���عؿ�����
					initCurrentStageData();
				}
			}
		});
	}
	
	/**
	 * �ж��Ƿ�ͨ��
	 * @return
	 */
	private boolean judegAppPassed(){
		return (mCurrentStageIndex == Const.SONG_INFO.length - 1);
	}
	
	//�������
	private void clearTheAnswer(WordButton wordButton){
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisiable = false;
		//���ô�ѡ��
		
		setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);

	}
	
	
	/**
	 * ���ô�
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton){
		for(int i = 0; i < mBtnSelectWords.size(); i++){
			if(mBtnSelectWords.get(i).mWordString.length() ==0 ){
				//���ô����ֿ����ݼ��ɼ���
				mBtnSelectWords.get(i).mViewButton.setText(wordButton.mWordString);
				mBtnSelectWords.get(i).mIsVisiable = true;
				mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
				//��¼����
				mBtnSelectWords.get(i).mIndex = wordButton.mIndex;
				
				MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + ""); 
				
				//���ô�ѡ��ɼ���
				setButtonVisiable(wordButton, View.INVISIBLE);
				
				break;
			}
		}
	}
	
	/**
	 * ���ô�ѡ���ֿ��Ƿ�ɼ�
	 * @param button
	 * @param visibility
	 */
	
	private void setButtonVisiable(WordButton button, int visibility){
		button.mViewButton.setVisibility(visibility);
		button.mIsVisiable = (visibility == View.VISIBLE) ? true : false;
		
		MyLog.d(TAG, button.mIsVisiable +"");
	}
	
	 /**
     * ����Բ���м�Ĳ��Ű�ť�����ǿ�ʼ��������
     */
	private void handlePlayButton(){
		if(mViewCdBar != null){
		if(!mIsRunning){
			mIsRunning = true ;
			
			// ��ʼ���˽��붯��
			mViewCdBar.startAnimation(mBarInAnim);
			mBtnPlayStart.setVisibility(View.INVISIBLE);
			
			//��������
			MyPlayer.playSong(MainActivity.this, mCurrentSong.getSongFileName());
		}
	}
}
	
	public void onPause(){
		//������Ϸ����
		Util.saveData(MainActivity.this, mCurrentStageIndex - 1, mCurrentCoins);
		
		mViewCd.clearAnimation();
		
		MyPlayer.stopTheSong(MainActivity.this);
		
		super.onPause();
	}
	
	private Song loadStageInfo(int stageIndex){
		Song song = new Song();
		
		String[] stage = Const.SONG_INFO[stageIndex];
		song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);
		
		return song;
	}
	
	/**
	 * ���ص�ǰ�ص�����
	 */
	private void initCurrentStageData(){
		//��ȡ��ǰ�صĸ�����Ϣ
		mCurrentSong = loadStageInfo(++mCurrentStageIndex);
		
		// ��ʼ����ѡ���
		mBtnSelectWords = initWordSelect();
				
		LayoutParams params = new LayoutParams(140,140);
		
		//���ԭ���Ĵ�
		mViewWordsContainer.removeAllViews();
		
		//�����µĴ𰸿�
		for(int i =0; i < mBtnSelectWords.size(); i++){
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton, params);
		}
		
		//��ʾ��ǰ�ص�����
		mCurrentStageView = (TextView) findViewById(R.id.text_current_stage); 
		
		if(mCurrentStageView != null){
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "" );
		}
		
		//�������
		mAllWords = initAllWord();
		//�������� MyGridView
		mMyGridView.updateData(mAllWords);
		
		//��ʼ��������
		handlePlayButton();
	}
	
	private ArrayList<WordButton> initAllWord(){
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		
		//������д�ѡ����
		String[] words = generateWords();
		
		
		for(int i = 0; i<MyGridView.COUNTS_WORDS; i++){
			WordButton button = new WordButton();
			//���ּ���
			button.mWordString = words[i];
			data.add(button);
		}	
		return data;
	}
	
	/**
	 * ��ʼ����ѡ�����ֿ�
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			View view = Util.getView(MainActivity.this, R.layout.self_ui_gridview_item);
			
			final WordButton holder = new WordButton();
			
			holder.mViewButton = (Button)view.findViewById(R.id.item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisiable = false;
			
			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
			holder.mViewButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					clearTheAnswer(holder);
					
					
				}
			});
			
			data.add(holder);
		}
		
		return data;
	}
	
	/**
	 * �������еĴ�ѡ����
	 * @return
	 */
	private String[] generateWords(){
		Random random = new Random();
		String[] words = new String[MyGridView.COUNTS_WORDS];
		
		//�������
		for (int i = 0; i< mCurrentSong.getNameLength(); i++){
			words[i] = mCurrentSong.getNameCharacters()[i] +"";
		}
		//��ȡ������ֲ���������
		for(int i = mCurrentSong.getNameLength(); i<MyGridView.COUNTS_WORDS; i++){
			words[i] = getRandomChar() + "";
		}
		
		// ��������˳�����ȴ�����Ԫ�������ѡȡһ�����һ��Ԫ�ؽ��н�����
		// Ȼ���ڵڶ���֮��ѡ��һ��Ԫ����ڶ���������֪�����һ��Ԫ�ء�
		// �����ܹ�ȷ��ÿ��Ԫ����ÿ��λ�õĸ��ʶ���1/n
		for(int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--){
			int index = random.nextInt(i+1);
			
			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}
		
		
		return words;
	}
	
	
	/**
	 * �����������
	 * @return
	 */
	private char getRandomChar(){
		String str = "";
		int hightPos;
		int lowPos;
		
		Random random = new Random();
		//abs ����ֵ
		hightPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(93)));
		
		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos)).byteValue();
		b[1] = (Integer.valueOf(lowPos)).byteValue();
		
		try {
			str = new String(b,"GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str.charAt(0);
	}
	
	/**
	 * ����
	 * @return
	 */
	private int checkTheAnswer(){
		//�ȼ�鳤��
		for(int i = 0; i < mBtnSelectWords.size(); i++){
			//�пյģ��𰸲�����
			if(mBtnSelectWords.get(i).mWordString.length() == 0){
				return STATUS_ANSWER_LACk;
			}
		}
		
		//�������������ȷ��
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < mBtnSelectWords.size(); i++){
			sb.append(mBtnSelectWords.get(i).mWordString);
		}
		
		return (sb.toString().equals(mCurrentSong.getSongName()))? STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
	}
	
	/**
	 * ������˸���𰸴���
	 */
	public void sparkTheWords(){
		//��ʱ��
		TimerTask task = new TimerTask() {	
			//�л�����
			boolean mChange = false;
			//��˸��ʱ
			int mSpardTimes = 0;
			
			 public void run() {
				 runOnUiThread(new Runnable() {
					public void run() {
						//��˸6�η���
						if(++mSpardTimes > SPASH_TIMES	){
							return;
						}
						//ִ����˸ ������ʾ��ɫ�Ͱ�ɫ����
						for(int i = 0; i < mBtnSelectWords.size(); i++){
							mBtnSelectWords.get(i).mViewButton.setTextColor(
									mChange ? Color.RED : Color.WHITE);
						}
						mChange = !mChange;				 
					}
				});{
					 
				 }
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1,150);
	}
	
	/**
	 * �Զ�ѡ��һ����
	 */
	private void tipAnswer(){
		
		boolean tipWord = false;
		for(int i = 0; i < mBtnSelectWords.size(); i++){
			if(mBtnSelectWords.get(i).mWordString.length() == 0){
				//���ݵ�ǰ�Ĵ𰸿�����ѡ���Ӧ���ֲ�����
				onWordButtonClick(findIsAnswerWord(i)) ;
				
				tipWord = true;
				
				//���ٽ������
				if(!handleCoins(-getTipCoins())){
					showConfirmDialog(ID_DIALOG_LACK_COINS);	
					return;
				}
				
				break;
			}
		}
		
		// û���ҵ��������𰸵ĵط�
		if(!tipWord){
			//��˸������ʾ�û�
			sparkTheWords();
		}
	}
	
	
	
	/** 
	 * ��Ϸ����
	 * ɾ������
	 */
	private void deleteOnWord(){
		//���ٽ��
		if(!handleCoins(-getDeleteWordCoins())){
			//��Ҳ��㣬��ʾ��ʾ�Ի���
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return ;
		}
		// �����������Ӧ��WordButton����Ϊ���ɼ�
		setButtonVisiable( findNotAnswerWord(),View.INVISIBLE );
	}
	
	/**
	 * ��Ϸ����
	 * �ҵ�һ����
	 * @param index ��ǰ��Ҫ����𰸵�����
	 * @return
	 */
	private WordButton findIsAnswerWord(int index){
		WordButton buf = null;
		
		for(int i = 0; i < MyGridView.COUNTS_WORDS; i++){
			buf = mAllWords.get(i);
			if(buf.mWordString.equals(""+mCurrentSong.getNameCharacters()[index])){
				return buf;
			}
		}
		    return null;
	}
	
	/**
	 * �ҵ�һ�����Ǵ𰸵��ļ������ҵ�ǰ�ǿɼ���
	 * @return
	 */
	private WordButton findNotAnswerWord(){
		Random random = new Random();
		WordButton buf = null;
		while(true){
			int index = random.nextInt(MyGridView.COUNTS_WORDS);
			
			buf = mAllWords.get(index);
			
			if(buf.mIsVisiable && !isTheAnswerWord(buf)){
				return buf;
			}
		}
		
	}
	
	/**
	 * �ж������Ƿ�Ϊ��
	 */
	private boolean isTheAnswerWord(WordButton word){
		boolean result = false;
		
		for(int i = 0; i < mCurrentSong.getNameLength(); i++){
			if(word.mWordString.equals
				   (""+mCurrentSong.getNameCharacters()[i])){
				result = true;
				
				break;
			}
		}
		return result;
	}
	
	/**
	 * ���ӻ����ָ�������Ľ��
	 * @param data
	 * @return true ���ӻ���ٳɹ�  false ʧ��
	 */
	private boolean handleCoins(int data){
		//�жϵ�ǰ�ܵĽ�������Ƿ���Ա�����
		if(mCurrentCoins + data >= 0){
			mCurrentCoins += data;
			mViewCurrentCoins.setText(mCurrentCoins + "");
			return true;
		}else{
			//��Ҳ���
			return false;
		}
	}
	
	/**
	 * �������ļ���ȡɾ��������Ҫ�õĽ��
	 * @return
	 */
	private int getDeleteWordCoins(){
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}
	
	/**
	 * �������ļ���ȡ��ʾ������Ҫ�õĽ��
	 * @return
	 */
	private int getTipCoins(){
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}
	
	
	/**
	 * ����ɾ����ѡ�����¼�
	 */
	private void handleDeleteWord(){
		ImageButton button = (ImageButton) findViewById(R.id.btn_delete_word);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//��ʾ�Ƿ�ɾ��
				showConfirmDialog(ID_DIALOG_DELETE_WORD);
				
			}
		});
	}
	
	
	/**
	 * ������ʾ�����¼�
	 */
	private void handleTipAnswer(){
		ImageButton button = (ImageButton) findViewById(R.id.btn_tip_answer);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//��ʾ�Ƿ���ʾ
				showConfirmDialog(ID_DIALOG_TIP_ANSWER);
				//tipAnswer();
				
			}
		});
	}
	
	//�Զ���AlertDiaog�¼���Ӧ
	//ɾ�������
	private IAlertDialogButtonListener mBtnOkDeleteWordListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// ִ���¼� ɾ������
			deleteOnWord();
			
		}
		
	};
	
	
	//����ʾ
	private IAlertDialogButtonListener mBtnOkTipAnswerListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// ִ���¼� ��ʾһ����
			tipAnswer();
			
		}
		
	};
	
	
	//��Ҳ���
	private IAlertDialogButtonListener mBtnOkLackCoinsListener = new IAlertDialogButtonListener(){

		@Override
		public void onClick() {
			// ִ���¼�  �����̵�
			
		}
		
	};
	
	/**
	 * ��ʾ�Ի���
	 * @param id
	 */
	private void showConfirmDialog(int id){
		switch(id){
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this,
					"ȷ�ϻ���" + getDeleteWordCoins() + "�����ȥ��һ������Ĵ�?",
					 mBtnOkDeleteWordListener);
			break;
		
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this,
					"ȷ�ϻ���" + getTipCoins() + "����һ�ȡһ��������ʾ?",
					 mBtnOkTipAnswerListener);
			break;
		
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this,
					"��Ҳ���",
					 mBtnOkLackCoinsListener);
			break;
		}
	}
	
}
