package com.sentulasia.enl.widget;

import com.sentulasia.enl.R;
import com.sentulasia.enl.model.GuardianPortal;
import com.sentulasia.enl.util.Util;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by andhie on 2/1/14.
 */
public class PortalCard extends LinearLayout {

    public PortalCard(Context context) {
        super(context);
        init();
    }

    public PortalCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PortalCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private TextView mPortalName;

    private TextView mPortalLocation;

    private TextView mOwner;

    private TextView mMatureDate;

    private TextView mAge;

    private void init() {

        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.card_bg);
        int pad = Util.toPx(getContext(), 15);
        setPadding(pad, pad, pad, pad);

        View v = LayoutInflater.from(getContext()).inflate(R.layout.card_portal, this, true);

        mPortalName = (TextView) v.findViewById(R.id.portal_name);
        mPortalLocation = (TextView) v.findViewById(R.id.portal_location);
        mOwner = (TextView) v.findViewById(R.id.owner);
        mMatureDate = (TextView) v.findViewById(R.id.mature_date);
        mAge = (TextView) v.findViewById(R.id.age);
    }

    private SpannableStringBuilder ssb;

    public void setData(GuardianPortal portal) {
        mPortalName.setText(portal.getPortal_name());
        mPortalLocation.setText(portal.getLocation());
        mOwner.setText(portal.getAgent_name());
        mAge.setText(String.valueOf(portal.getPortalAge()));

        if (portal.isLive()) {
            mMatureDate.setText(portal.printGuardianMilestone());
            mMatureDate.setTextColor(Color.BLACK);
        } else {

            if (ssb == null) {
                ssb = new SpannableStringBuilder();
            }

            String destroyer = portal.getDestroyed_by();

            ssb.clear();
            ssb.append("Tears collected by ").append(destroyer);
            ssb.setSpan(new ForegroundColorSpan(
                    getResources().getColor(R.color.text_green_enlightened)),
                    ssb.length() - destroyer.length(),
                    ssb.length(),
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            mMatureDate.setText(ssb);
            Util.setStrikeThru(mAge);
            Util.setStrikeThru(mOwner);
        }
    }
}
