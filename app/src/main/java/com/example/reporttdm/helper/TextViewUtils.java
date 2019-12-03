package com.example.reporttdm.helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ReplacementSpan;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TextViewUtils {

    public static void separateGroups(@NonNull TextView textView) {

        textView.addTextChangedListener(new SeparateThousandsTextWatcher());
    }

    private TextViewUtils() {
    }

    private static class SeparateThousandsTextWatcher implements TextWatcher {

        private final NumberFormat numberFormat = DecimalFormat.getNumberInstance();

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count,
                                      final int after) {

            // Do nothing.
        }

        @Override
        public void onTextChanged(final CharSequence s, final int start, final int before,
                                  final int count) {

            // Do nothing.
        }

        @Override
        public void afterTextChanged(final Editable s) {
            for (SeparatorSpan currentSeparatorSpan : s
                    .getSpans(0, s.length(), SeparatorSpan.class)) {
                s.removeSpan(currentSeparatorSpan);
            }
            try {
                long value = Long.parseLong(s.toString());
                String formatted = numberFormat.format(value);
                int offset = 0;
                for (int i = 0; i < formatted.length(); i++) {
                    char c = formatted.charAt(i);
                    if (!Character.isDigit(c)) {
                        s.setSpan(new SeparatorSpan(String.valueOf(c)), i - offset, i - offset + 1,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        offset++;
                    }
                }
            } catch (NumberFormatException nfe) {
                // Do nothing.
            }
        }
    }

    private static class SeparatorSpan extends ReplacementSpan {

        private final StringBuilder stringBuilder = new StringBuilder();
        private final CharSequence separator;

        public SeparatorSpan(@NonNull CharSequence separator) {
            this.separator = separator;
        }

        @Override
        public int getSize(@NonNull final Paint paint, final CharSequence text, final int start,
                           final int end, final Paint.FontMetricsInt fm) {

            stringBuilder.setLength(0);
            stringBuilder.append(separator).append(text.subSequence(start, end));
            return (int) paint.measureText(stringBuilder, 0, stringBuilder.length());
        }

        @Override
        public void draw(@NonNull final Canvas canvas, final CharSequence text, final int start,
                         final int end, final float x, final int top, final int y,
                         final int bottom, @NonNull final Paint paint) {

            canvas.drawText(stringBuilder, 0, stringBuilder.length(), x, y, paint);
        }
    }
}
