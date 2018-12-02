package com.avast.android.dialogs.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;

import android.text.Html;
import android.text.InputType;
import android.text.SpannedString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.avast.android.dialogs.R;
import com.avast.android.dialogs.core.BaseDialogBuilder;
import com.avast.android.dialogs.core.BaseDialogFragment;
import com.avast.android.dialogs.iface.INegativeButtonDialogListener;
import com.avast.android.dialogs.iface.INeutralButtonDialogListener;
import com.avast.android.dialogs.iface.INumberPositiveButtonDialogListener;
import com.avast.android.dialogs.util.NumberHelper;

import java.text.NumberFormat;
import java.util.List;
import java.util.regex.Pattern;

public class NumberPickerDialogFragment extends BaseDialogFragment {

    private static final String TAG = NumberPickerDialogFragment.class.getSimpleName();

    protected final static String ARG_MESSAGE = "message";
    protected final static String ARG_TITLE = "title";
    protected final static String ARG_POSITIVE_BUTTON = "positive_button";
    protected final static String ARG_NEGATIVE_BUTTON = "negative_button";
    protected final static String ARG_DEFAULT_VALUE = "value_default";
    protected final static String ARG_ARGUMENTS = "arguments";
    protected final static String ARG_LAYOUT_RES_ID = "layout_res_id";
    protected final static String ARG_INPUT_TYPE = "input_type";
    protected final static String ARG_MIN_VALUE = "value_min";
    protected final static String ARG_MAX_VALUE = "value_max";
    protected static final String ARG_ERR_MESSAGE = "error_message";

    public static NumberPickerDialogBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new NumberPickerDialogBuilder(context, fragmentManager, NumberPickerDialogFragment.class);
    }

    @Override
    protected Builder build(Builder builder) {
        final CharSequence title = getTitle();
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        final CharSequence message = getMessage();
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }

        final LayoutInflater inflater = builder.getLayoutInflater();
        @SuppressLint("InflateParams")
        final View view = inflater.inflate(getLayoutResId(), null, false);
        final TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setText(NumberFormat.getIntegerInstance().format(getDefaultValue()));

        final View plus = view.findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = NumberHelper.parseInt(text.getText().toString(), getDefaultValue());
                val = Math.min(++val, getMaxValue());
                text.setText(NumberFormat.getIntegerInstance().format(val));
            }
        });

        final View minus = view.findViewById(R.id.minus);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = NumberHelper.parseInt(text.getText().toString(), getDefaultValue());
                val = Math.max(--val, getMinValue());
                text.setText(NumberFormat.getIntegerInstance().format(val));
            }
        });

        builder.setView(view);

        final CharSequence positiveButtonText = getPositiveButtonText();
        if (!TextUtils.isEmpty(positiveButtonText)) {
            builder.setPositiveButton(positiveButtonText, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(INumberPositiveButtonDialogListener listener: getPositiveButtonDialogListeners()) {
                        listener.onPositiveEditTextButtonClicked(mRequestCode,
                                NumberHelper.parseInt(text.getText().toString(), getDefaultValue()),
                                getData());
                    }
                    dismiss();
                }
            });
        }

        final CharSequence negativeButtonText = getNegativeButtonText();
        if (!TextUtils.isEmpty(negativeButtonText)) {
            builder.setNegativeButton(negativeButtonText, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(INegativeButtonDialogListener listener: getNegativeButtonDialogListeners()) {
                        listener.onNegativeButtonClicked(mRequestCode, getData());
                    }
                    dismiss();
                }
            });
        }

        return builder;
    }

    protected CharSequence getTitle() {
        return getArguments().getCharSequence(ARG_TITLE);
    }

    protected CharSequence getMessage() {
        return getArguments().getCharSequence(ARG_MESSAGE);
    }

    protected CharSequence getPositiveButtonText() {
        return getArguments().getCharSequence(ARG_POSITIVE_BUTTON);
    }

    protected CharSequence getNegativeButtonText() {
        return getArguments().getCharSequence(ARG_NEGATIVE_BUTTON);
    }

    protected int getDefaultValue() {
        return getArguments().getInt(ARG_DEFAULT_VALUE, 0);
    }

    protected int getMinValue() {
        return getArguments().getInt(ARG_MIN_VALUE);
    }

    protected int getMaxValue() {
        return getArguments().getInt(ARG_MAX_VALUE);
    }

    protected Bundle getData() {
        return getArguments().getBundle(ARG_ARGUMENTS);
    }

    protected int getLayoutResId() {
        final int layoutResId =  getArguments().getInt(ARG_LAYOUT_RES_ID);
        return layoutResId == 0 ? R.layout.sdl_number_picker : layoutResId;
    }

    protected int getInputType() {
        return getArguments().getInt(ARG_INPUT_TYPE);
    }

    protected List<INumberPositiveButtonDialogListener> getPositiveButtonDialogListeners() {
        return getDialogListeners(INumberPositiveButtonDialogListener.class);
    }

    protected List<INeutralButtonDialogListener> getNeutralButtonDialogListeners() {
        return getDialogListeners(INeutralButtonDialogListener.class);
    }

    protected List<INegativeButtonDialogListener> getNegativeButtonDialogListeners() {
        return getDialogListeners(INegativeButtonDialogListener.class);
    }

    protected CharSequence getErrorMessage() {
        return getArguments().getCharSequence(ARG_ERR_MESSAGE);
    }

    public static class NumberPickerDialogBuilder extends BaseDialogBuilder<NumberPickerDialogBuilder> {

        private CharSequence mTitle;
        private CharSequence mMessage;
        private CharSequence mPositiveButtonText;
        private CharSequence mNegativeButtonText;
        private int mDefaultValue;
        private int mMinValue = Integer.MIN_VALUE;
        private int mMaxValue = Integer.MAX_VALUE;
        private CharSequence mErrorMessage;
        private Pattern mPattern;
        private Bundle mArguments;
        private int mLayoutResId;
        private int mInputType = InputType.TYPE_CLASS_TEXT;

        public NumberPickerDialogBuilder(Context context, FragmentManager fragmentManager, Class<? extends NumberPickerDialogFragment> clazz) {
            super(context, fragmentManager, clazz);
        }

        @Override
        protected NumberPickerDialogBuilder self() {
            return this;
        }

        public NumberPickerDialogBuilder setTitle(@StringRes int titleResourceId) {
            mTitle = mContext.getString(titleResourceId);
            return this;
        }

        public NumberPickerDialogBuilder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        /**
         * Allow to set resource string with HTML formatting and bind %s,%i.
         * This is workaround for https://code.google.com/p/android/issues/detail?id=2923
         */
        public NumberPickerDialogBuilder setMessage(int resourceId, Object... formatArgs) {
            mMessage = Html.fromHtml(String.format(Html.toHtml(new SpannedString(mContext.getText(resourceId))), formatArgs));
            return this;
        }

        public NumberPickerDialogBuilder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public NumberPickerDialogBuilder setData(Bundle arguments) {
            mArguments = arguments;
            return this;
        }

        public NumberPickerDialogBuilder setMinValue(int minValue) {
            mMinValue = minValue;
            return this;
        }

        public NumberPickerDialogBuilder setMaxValue(int maxValue) {
            mMaxValue = maxValue;
            return this;
        }

        public NumberPickerDialogBuilder setDefaultValue(int defaultValue) {
            mDefaultValue = defaultValue;
            return this;
        }

        public NumberPickerDialogBuilder setContentLayout(@LayoutRes int contentLayoutResId) {
            mLayoutResId = contentLayoutResId;
            return this;
        }

        public NumberPickerDialogBuilder setPositiveButtonText(int textResourceId) {
            mPositiveButtonText = mContext.getString(textResourceId);
            return this;
        }

        public NumberPickerDialogBuilder setPositiveButtonText(CharSequence text) {
            mPositiveButtonText = text;
            return this;
        }

        public NumberPickerDialogBuilder setNegativeButtonText(int textResourceId) {
            mNegativeButtonText = mContext.getString(textResourceId);
            return this;
        }

        public NumberPickerDialogBuilder setNegativeButtonText(CharSequence text) {
            mNegativeButtonText = text;
            return this;
        }

        public NumberPickerDialogBuilder setInputType(int inputType) {
            mInputType = inputType;
            return this;
        }

        public NumberPickerDialogBuilder setPattern(Pattern pattern, CharSequence errorMessage) {
            mPattern = pattern;
            mErrorMessage = errorMessage;
            return this;
        }

        public NumberPickerDialogBuilder setPattern(Pattern pattern, int errorMessage) {
            return setPattern(pattern, mContext.getString(errorMessage));
        }


        @Override
        protected Bundle prepareArguments() {
            Bundle args = new Bundle();
            args.putCharSequence(ARG_TITLE, mTitle);
            args.putCharSequence(ARG_MESSAGE, mMessage);
            args.putInt(ARG_DEFAULT_VALUE, mDefaultValue);
            args.putInt(ARG_MIN_VALUE, mMinValue);
            args.putInt(ARG_MAX_VALUE, mMaxValue);
            args.putCharSequence(ARG_POSITIVE_BUTTON, mPositiveButtonText);
            args.putCharSequence(ARG_NEGATIVE_BUTTON, mNegativeButtonText);
            args.putBundle(ARG_ARGUMENTS, mArguments);
            args.putInt(ARG_LAYOUT_RES_ID, mLayoutResId);
            args.putInt(ARG_INPUT_TYPE, mInputType);

            return args;
        }
    }
}
