package com.hch.purblepairs;

public class Card {
	private int x, y, image_index;
	private boolean show;// 是否显示图片
	private boolean remove;// 是否消除

	public Card(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public int getImage_index() {
		return image_index;
	}

	public void setImage_index(int image_index) {
		this.image_index = image_index;
	}

	public boolean isRemove() {
		return remove;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}
}
