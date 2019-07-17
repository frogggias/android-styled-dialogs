package com.avast.android.dialogs.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;

import com.avast.android.dialogs.R;
import com.avast.android.dialogs.core.BaseDialogBuilder;
import com.avast.android.dialogs.core.BaseDialogFragment;
import com.avast.android.dialogs.iface.INegativeButtonDialogListener;
import com.avast.android.dialogs.iface.INeutralButtonDialogListener;
import com.avast.android.dialogs.iface.ITextPositiveButtonDialogListener;

import java.util.List;
import java.util.regex.Pattern;

public class TextDialogFragment extends BaseDialogFragment {

    private static final String TAG = TextDialogFragment.class.getSimpleName();

    protected final static String ARG_MESSAGE = "message";
    protected final static String ARG_TITLE = "title";
    protected final static String ARG_POSITIVE_BUTTON = "positive_button";
    protected final static String ARG_NEUTRAL_BUTTON = "neutral_button";
    protected final static String ARG_NEGATIVE_BUTTON = "negative_button";
    protected final static String ARG_DEFAULT_VALUE = "value_default";
    protected final static String ARG_ARGUMENTS = "arguments";
    protected final static String ARG_LAYOUT_RES_ID = "layout_res_id";
    protected final static String ARG_INPUT_TYPE = "input_type";
    protected final static String ARG_DIGITS = "digits";
    protected static final String ARG_PATTERN = "pattern";
    protected static final String ARG_ERR_MESSAGE = "error_message";

    public static TextDialogBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new TextDialogBuilder(context, fragmentManager, TextDialogFragment.class);
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
        final EditText text = view.findViewById(android.R.id.text1);

        setContentViewPadding(title, message, view);

        CharSequence defaultValue = getDefaultValue();
        if (!TextUtils.isEmpty(defaultValue)) {
            text.setText(defaultValue);
            text.setSelection(defaultValue.length());
        }

        text.setInputType(getInputType());

        CharSequence digits = getDigits();
        if (digits != null) {
            text.setKeyListener(DigitsKeyListener.getInstance(digits.toString()));
        }

        builder.setView(view);

        final CharSequence positiveButtonText = getPositiveButtonText();
        if (!TextUtils.isEmpty(positiveButtonText)) {
            builder.setPositiveButton(positiveButtonText, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!matchesPattern(text.getText())) {
                        text.setError(getErrorMessage());
                        return;
                    }
                    for(ITextPositiveButtonDialogListener listener: getPositiveButtonDialogListeners()) {
                        listener.onPositiveEditTextButtonClicked(mRequestCode, text.getText(), getData());
                    }
                    dismiss();
                }
            });
        }

        final CharSequence neutralButtonText = getNeutralButtonText();
        if (!TextUtils.isEmpty(neutralButtonText)) {
            builder.setNeutralButton(neutralButtonText, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(INeutralButtonDialogListener listener: getNeutralButtonDialogListeners()) {
                        listener.onNeutralButtonClicked(mRequestCode, getData());
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

    private void setContentViewPadding(@Nullable CharSequence title, @Nullable CharSequence message,
                                       @NonNull View input) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        int grid6 = context.getResources().getDimensionPixelSize(R.dimen.grid_6);
        int grid4 = context.getResources().getDimensionPixelSize(R.dimen.grid_4);
        if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(message)) {
            input.setPadding(grid6, 0, grid6, grid4);
        } else {
            input.setPadding(grid6, grid6, grid6, grid4);
        }
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

    protected CharSequence getNeutralButtonText() {
        return getArguments().getCharSequence(ARG_NEUTRAL_BUTTON);
    }

    protected CharSequence getNegativeButtonText() {
        return getArguments().getCharSequence(ARG_NEGATIVE_BUTTON);
    }

    protected CharSequence getDefaultValue() {
        return getArguments().getCharSequence(ARG_DEFAULT_VALUE);
    }

    protected Bundle getData() {
        return getArguments().getBundle(ARG_ARGUMENTS);
    }

    protected int getLayoutResId() {
        final int layoutResId =  getArguments().getInt(ARG_LAYOUT_RES_ID);
        return layoutResId == 0 ? R.layout.sdl_edit_text : layoutResId;
    }

    protected int getInputType() {
        return getArguments().getInt(ARG_INPUT_TYPE);
    }

    protected CharSequence getDigits() {
        return getArguments().getCharSequence(ARG_DIGITS);
    }

    protected List<ITextPositiveButtonDialogListener> getPositiveButtonDialogListeners() {
        return getDialogListeners(ITextPositiveButtonDialogListener.class);
    }

    protected List<INeutralButtonDialogListener> getNeutralButtonDialogListeners() {
        return getDialogListeners(INeutralButtonDialogListener.class);
    }

    protected List<INegativeButtonDialogListener> getNegativeButtonDialogListeners() {
        return getDialogListeners(INegativeButtonDialogListener.class);
    }

    protected Pattern getPattern() {
        return (Pattern) getArguments().getSerializable(ARG_PATTERN);
    }

    protected CharSequence getErrorMessage() {
        return getArguments().getCharSequence(ARG_ERR_MESSAGE);
    }

    private boolean matchesPattern(Editable text) {
        Pattern pattern = getPattern();
        if (pattern == null) {
            return true;
        }
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return pattern.matcher(text.toString()).matches();
    }

    public static class TextDialogBuilder extends BaseDialogBuilder<TextDialogBuilder> {

        private CharSequence mTitle;
        private CharSequence mMessage;
        private CharSequence mPositiveButtonText;
        private CharSequence mNegativeButtonText;
        private CharSequence mNeutralButtonText;
        private CharSequence mDefaultValue;
        private CharSequence mErrorMessage;
        private Pattern mPattern;
        private Bundle mArguments;
        private int mLayoutResId;
        private int mInputType = InputType.TYPE_CLASS_TEXT;
        private CharSequence mDigits;

        public TextDialogBuilder(Context context, FragmentManager fragmentManager, Class<? extends TextDialogFragment> clazz) {
            super(context, fragmentManager, clazz);
        }

        @Override
        protected TextDialogBuilder self() {
            return this;
        }

        public TextDialogBuilder setTitle(@StringRes int titleResourceId) {
            mTitle = mContext.getString(titleResourceId);
            return this;
        }

        public TextDialogBuilder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        /**
         * Allow to set resource string with HTML formatting and bind %s,%i.
         * This is workaround for https://code.google.com/p/android/issues/detail?id=2923
         */
        public TextDialogBuilder setMessage(int resourceId, Object... formatArgs) {
            mMessage = Html.fromHtml(String.format(Html.toHtml(new SpannedString(mContext.getText(resourceId))), formatArgs));
            return this;
        }

        public TextDialogBuilder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public TextDialogBuilder setData(Bundle arguments) {
            mArguments = arguments;
            return this;
        }

        public TextDialogBuilder setDefaultValue(CharSequence defaultValue) {
            mDefaultValue = defaultValue;
            return this;
        }

        public TextDialogBuilder setContentLayout(@LayoutRes int contentLayoutResId) {
            mLayoutResId = contentLayoutResId;
            return this;
        }

        public TextDialogBuilder setPositiveButtonText(int textResourceId) {
            mPositiveButtonText = mContext.getString(textResourceId);
            return this;
        }

        public TextDialogBuilder setPositiveButtonText(CharSequence text) {
            mPositiveButtonText = text;
            return this;
        }

        public TextDialogBuilder setNegativeButtonText(int textResourceId) {
            mNegativeButtonText = mContext.getString(textResourceId);
            return this;
        }

        public TextDialogBuilder setNegativeButtonText(CharSequence text) {
            mNegativeButtonText = text;
            return this;
        }

        public TextDialogBuilder setNeutralButtonText(int textResourceId) {
            mNeutralButtonText = mContext.getString(textResourceId);
            return this;
        }

        public TextDialogBuilder setNeutralButtonText(CharSequence text) {
            mNeutralButtonText = text;
            return this;
        }

        public TextDialogBuilder setInputType(int inputType) {
            mInputType = inputType;
            return this;
        }

        public TextDialogBuilder setDigits(CharSequence digits) {
            mDigits = digits;
            return this;
        }

        public TextDialogBuilder setPattern(Pattern pattern, CharSequence errorMessage) {
            mPattern = pattern;
            mErrorMessage = errorMessage;
            return this;
        }

        public TextDialogBuilder setPattern(Pattern pattern, int errorMessage) {
            return setPattern(pattern, mContext.getString(errorMessage));
        }


        @Override
        protected Bundle prepareArguments() {
            Bundle args = new Bundle();
            args.putCharSequence(ARG_TITLE, mTitle);
            args.putCharSequence(ARG_MESSAGE, mMessage);
            args.putCharSequence(ARG_DEFAULT_VALUE, mDefaultValue);
            args.putCharSequence(ARG_POSITIVE_BUTTON, mPositiveButtonText);
            args.putCharSequence(ARG_NEUTRAL_BUTTON, mNeutralButtonText);
            args.putCharSequence(ARG_NEGATIVE_BUTTON, mNegativeButtonText);
            args.putBundle(ARG_ARGUMENTS, mArguments);
            args.putInt(ARG_LAYOUT_RES_ID, mLayoutResId);
            args.putInt(ARG_INPUT_TYPE, mInputType);
            args.putCharSequence(ARG_DIGITS, mDigits);
            args.putSerializable(ARG_PATTERN, mPattern);
            args.putCharSequence(ARG_ERR_MESSAGE, mErrorMessage);

            return args;
        }
    }
}
