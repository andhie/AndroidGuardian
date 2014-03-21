package com.sentulasia.enl.widget;

import com.sentulasia.enl.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * This view takes in an attribute ratio in width-to-height (i.e. 9:16 = 0.5625)
 * Original source is taken from
 *
 * https://code.google.com/p/libs123/source/browse/trunk/src/hoang/hut/app/tool/view/ScaleImageView.java
 *
 *
 * This view will auto determine the width or height by determining if the
 * height or width is set and scale the other dimension depending on the images dimension
 *
 * This view also contains an ImageChangeListener which calls changed(boolean isEmpty) once
 * a change has been made to the ImageView
 *
 * @author Maurycy Wojtowicz
 * @author andhie
 */
public class ScaleImageView extends ImageView {

    private ImageChangeListener imageChangeListener;

    // this flag determines if should measure height manually dependent of width
    private boolean scaleToWidth = false;

    private float ratio;

    public ScaleImageView(Context context) {
	super(context);
	init();
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	init();
	init(context, attrs);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
	super(context, attrs);
	init();
	init(context, attrs);
    }

    private void init() {
	this.setScaleType(ScaleType.CENTER_INSIDE);
    }

    private void init(Context context, AttributeSet attrs) {
	TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScaleImageView);
	ratio = a.getFloat(R.styleable.ScaleImageView_ratio, 0);
	a.recycle();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
	super.setImageBitmap(bm);
	if (imageChangeListener != null) {
	    imageChangeListener.changed((bm == null));
	}
    }

    @Override
    public void setImageDrawable(Drawable d) {
	super.setImageDrawable(d);
	if (imageChangeListener != null) {
	    imageChangeListener.changed((d == null));
	}
    }

    @Override
    public void setImageResource(int id) {
	super.setImageResource(id);
    }

    public interface ImageChangeListener {
	// a callback for when a change has been made to this imageView
	void changed(boolean isEmpty);
    }

    public ImageChangeListener getImageChangeListener() {
	return imageChangeListener;
    }

    public void setImageChangeListener(ImageChangeListener imageChangeListener) {
	this.imageChangeListener = imageChangeListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

	int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	int width = MeasureSpec.getSize(widthMeasureSpec);
	int height = MeasureSpec.getSize(heightMeasureSpec);

	/**
	 * if both width and height are set scale width first. modify in future if necessary
	 */

	if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
	    scaleToWidth = true;
	} else if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
	    scaleToWidth = false;
	} else {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    return;
//	    throw new IllegalStateException(
//		    "width or height needs to be set to match_parent or a specific dimension");
	}

	if (getDrawable() == null || getDrawable().getIntrinsicWidth() == 0) {
	    // nothing to measure
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    return;
	} else {
	    if (scaleToWidth) {
		int iw = this.getDrawable().getIntrinsicWidth();
		int ih = this.getDrawable().getIntrinsicHeight();
		int heightC = 0;
		if (ratio > 0) {
		    heightC = (int) (width * ratio);
		} else {
		    heightC = width * ih / iw;
		}

		if (height > 0) {
		    if (heightC > height) {
			// dont let height be greater then set max
			heightC = height;
			width = heightC * iw / ih;
		    }
		}

		this.setScaleType(ScaleType.CENTER_CROP);
		setMeasuredDimension(width, heightC);

	    } else {
		// need to scale to height instead
		int marg = 0;
		if (getParent() != null) {
		    if (getParent().getParent() != null) {
			marg += ((ViewGroup) getParent().getParent()).getPaddingTop();
			marg += ((ViewGroup) getParent().getParent()).getPaddingBottom();
		    }
		}

		int iw = this.getDrawable().getIntrinsicWidth();
		int ih = this.getDrawable().getIntrinsicHeight();

		width = height * iw / ih;
		height -= marg;
		setMeasuredDimension(width, height);
	    }

	}
    }

}
