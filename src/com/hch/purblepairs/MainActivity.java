package com.hch.purblepairs;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * 果蔬翻牌
 * 
 * @author hch
 * 
 */
public class MainActivity extends Activity {

	private int[] fruit_images = new int[20];// 图片id
	private String[] fruit_names = new String[20];// 图片名称
	private int[] card_images;// 卡片的图片索引
	private Card[][] cards = new Card[4][5];// 卡片
	private int lastcard_x = -1;// 上一个卡片x索引
	private int lastcard_y = -1;// 上一个卡片y索引
	private int currentcard_x = -1;// 当前卡片x索引
	private int currentcard_y = -1;// 当前卡片y索引

	private ImageView faceImg;
	private TableLayout tableLayout;
	private TextView timeTxt, quantityTxt;
	private ImageButton[][] buttons = new ImageButton[4][5];

	private boolean isFirstClick = false;
	private int second = 0;
	private int quantity = 20;

	/**
	 * 定时器，定时更新时间
	 */
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (second < 999) {
				handler.postDelayed(this, 1000);
				second++;
			} else {
				handler.removeCallbacks(this);
			}
			timeTxt.setText(String.format("%03d", second));
		}
	};

	Runnable runnable1 = new Runnable() {
		@Override
		public void run() {
			int image_index = cards[currentcard_x][currentcard_y]
					.getImage_index();
			if (image_index == cards[lastcard_x][lastcard_y].getImage_index()) {
				buttons[currentcard_x][currentcard_y].setImageDrawable(null);
				buttons[lastcard_x][lastcard_y].setImageDrawable(null);
				cards[currentcard_x][currentcard_y].setRemove(true);
				cards[lastcard_x][lastcard_y].setRemove(true);
				quantity = quantity - 2;
				quantityTxt.setText(String.format("%03d", quantity));
			} else {
				buttons[currentcard_x][currentcard_y]
						.setImageResource(R.drawable.leaf);
				buttons[lastcard_x][lastcard_y]
						.setImageResource(R.drawable.leaf);
				cards[currentcard_x][currentcard_y].setShow(false);
				cards[lastcard_x][lastcard_y].setShow(false);
			}
			lastcard_x = -1;
			lastcard_y = -1;
			checkOver();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		timeTxt = (TextView) findViewById(R.id.timeTxt);
		quantityTxt = (TextView) findViewById(R.id.quantityTxt);
		faceImg = (ImageView) findViewById(R.id.faceImg);
		tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		quantityTxt.setText(String.format("%03d", quantity));

		Typeface face = Typeface.createFromAsset(getAssets(),
				"fonts/digifaw.ttf");
		timeTxt.setTypeface(face);
		quantityTxt.setTypeface(face);

		faceImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setMessage("此游戏的目标是找到所有完全相同的图片。翻转一个图块以显示图片，然后尝试找到其配对。");
				builder.setTitle("帮助说明");
				builder.setNegativeButton("关闭",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			}
		});

		Resources res = getResources();
		fruit_names = res.getStringArray(R.array.fruit_names);
		TypedArray array = res.obtainTypedArray(R.array.fruit_images);
		for (int i = 0; i < array.length(); i++) {
			fruit_images[i] = array.getResourceId(i, 0);
		}
		array.recycle();

		initCardPanel();
	}

	/**
	 * 初始化卡片界面
	 */
	private void initCardPanel() {
		isFirstClick = false;

		// 从20个图片中随机取10个图片索引
		int[] randomNumber = getRandomNumber(0, 19, 10);
		// 复制10个图片，生成双倍图片索引
		card_images = getRandomCardNumber(randomNumber);

		for (int y = 0; y < 5; y++) {
			TableRow tableRow = new TableRow(this);
			tableRow.setLayoutParams(new TableLayout.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.MATCH_PARENT, 1));
			TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.MATCH_PARENT, 1);
			layoutParams.setMargins(0, 0, 0, 0);
			for (int x = 0; x < 4; x++) {
				cards[x][y] = new Card(x, y);
				cards[x][y].setImage_index(card_images[y * 4 + x]);

				buttons[x][y] = new ImageButton(this);
				buttons[x][y].setLayoutParams(layoutParams);
				buttons[x][y].setOnClickListener(new CardClickListener());
				buttons[x][y].setBackgroundResource(R.drawable.box1);
				buttons[x][y].setImageResource(R.drawable.leaf);
				buttons[x][y].setScaleType(ScaleType.FIT_CENTER);
				buttons[x][y].setTag(cards[x][y]);
				tableRow.addView(buttons[x][y]);
			}
			tableLayout.addView(tableRow);
		}
	}

	/**
	 * 卡片点击事件
	 * 
	 */
	private class CardClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (!isFirstClick) {
				handler.postDelayed(runnable, 1000);
				isFirstClick = true;
			}
			int showNum = 0;
			boolean isClick = true;
			for (int y = 0; y < 5; y++) {
				for (int x = 0; x < 4; x++) {
					if (!cards[x][y].isRemove() && cards[x][y].isShow()) {
						showNum++;
					}
					if (showNum == 2) {
						isClick = false;
						break;
					}
				}
			}
			if (isClick) {
				ImageButton button = (ImageButton) v;
				Card card = (Card) button.getTag();
				int x = card.getX(), y = card.getY();
				if (!card.isShow()) {
					button.setImageResource(fruit_images[card.getImage_index()]);
					card.setShow(true);
					currentcard_x = x;
					currentcard_y = y;
					if (lastcard_x == -1) {
						lastcard_x = currentcard_x;
						lastcard_y = currentcard_y;
					} else {
						handler.postDelayed(runnable1, 1000);
					}

				}
			}
		}
	}

	private void checkOver() {
		int rightNum = 0;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 4; x++) {
				if (cards[x][y].isRemove()) {
					rightNum++;
				}
			}
		}

		if (rightNum == 20) {
			handler.removeCallbacks(runnable);
			showMessage("游戏胜利", "恭喜你赢了！一共用时 " + second + "秒");
		}
	}

	/**
	 * 显示信息框
	 * 
	 * @param title
	 * @param message
	 */
	private void showMessage(String title, String message) {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton("重新开始",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						resetTime();
						tableLayout.removeAllViews();
						initCardPanel();
					}
				});
		builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				System.exit(0);
			}
		});
		builder.create().show();
	}

	/**
	 * 重置定时器
	 */
	private void resetTime() {
		second = 0;
		timeTxt.setText("000");
		quantityTxt.setText("000");
		quantity = 20;
		quantityTxt.setText(String.format("%03d", quantity));
	}

	private int[] getRandomCardNumber(int[] arr) {
		int[] arr1 = new int[20];
		for (int j = 0; j < arr1.length; j++) {
			if (j < 10) {
				arr1[j] = arr[j];
			} else {
				arr1[j] = arr[j - 10];
			}
		}
		Random random = new Random();
		for (int m = 0; m < arr1.length; m++) {
			int p = random.nextInt(arr1.length);
			int tmp = arr1[m];
			arr1[m] = arr1[p];
			arr1[p] = tmp;
		}
		random = null;
		return arr1;
	}

	/**
	 * 随机指定范围内N个不重复的数
	 * 
	 * @param min
	 * @param max
	 * @param n
	 * @return
	 */
	private int[] getRandomNumber(int min, int max, int n) {
		if (n > (max - min + 1) || max < min) {
			return null;
		}
		int[] result = new int[n];
		int count = 0;
		while (count < n) {
			int num = (int) (Math.random() * (max - min)) + min;
			boolean flag = true;
			for (int j = 0; j < n; j++) {
				if (num == result[j]) {
					flag = false;
					break;
				}
			}
			if (flag) {
				result[count] = num;
				count++;
			}
		}
		return result;
	}
}
