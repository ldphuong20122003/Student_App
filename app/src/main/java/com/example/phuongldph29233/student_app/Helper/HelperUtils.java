package com.example.phuongldph29233.student_app.Helper;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class HelperUtils {
    private static final String PHONE_ERROR_MSG = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0";
    private static final String NON_DIGIT_ERROR_MSG = "Chỉ được nhập số (0-9)";
    private static final int PHONE_NUMBER_LENGTH = 10;
    private static final String EMAIL_ERROR_MSG = "Email không hợp lệ";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    public static void setupPhoneNumberValidation(EditText editText) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PHONE_NUMBER_LENGTH)});
        editText.addTextChangedListener(new PhoneNumberTextWatcher(editText));
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        if (!phoneNumber.matches("\\d+")) {
            return false;
        }
        return phoneNumber.length() == PHONE_NUMBER_LENGTH &&
                phoneNumber.startsWith("0");
    }

    private static class PhoneNumberTextWatcher implements TextWatcher {
        private final EditText editText;
        PhoneNumberTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            String phone = s.toString();
            if (phone.isEmpty()) {
                editText.setError(null);
                return;
            }
            if (!phone.matches("\\d+")) {
                editText.setError(NON_DIGIT_ERROR_MSG);
                return;
            }
            if (!isValidPhoneNumber(phone)) {
                editText.setError(PHONE_ERROR_MSG);
            } else {
                editText.setError(null);
            }
        }
    }

    public static void setupEmailValidation(EditText editText) {
        setupEmailValidation(editText, EMAIL_ERROR_MSG);
    }
    public static void setupEmailValidation(EditText editText, String errorMessage) {
        editText.addTextChangedListener(new EmailTextWatcher(editText, errorMessage));
    }
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private static class EmailTextWatcher implements TextWatcher {
        private final EditText editText;
        private final String errorMessage;

        EmailTextWatcher(EditText editText, String errorMessage) {
            this.editText = editText;
            this.errorMessage = errorMessage;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            String email = s.toString();
            if (email.isEmpty()) {
                editText.setError(null);
                return;
            }

            if (!isValidEmail(email)) {
                editText.setError(errorMessage);
            } else {
                editText.setError(null);
            }
        }
    }

    public interface DatePickerCallback {
        void onDateSelected(String displayDate, String rawDate);
    }

    public static void setupDatePicker(
            Context context,
            EditText editText,
            String displayFormat,
            String saveFormat,
            DatePickerCallback callback
    ) {
        editText.setFocusable(false);
        editText.setClickable(true);

        editText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    (view, year, month, dayOfMonth) -> {
                        String displayDate = String.format(Locale.getDefault(), displayFormat,
                                dayOfMonth, month + 1, year);
                        String rawDate = String.format(Locale.getDefault(), saveFormat,
                                dayOfMonth, month + 1, year);
                        editText.setText(displayDate);
                        editText.setTag(rawDate);

                        if (callback != null) {
                            callback.onDateSelected(displayDate, rawDate);
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    public static void setupDatePicker(Context context, EditText editText) {
        setupDatePicker(
                context,
                editText,
                "%02d/%02d/%04d",
                "%02d%02d%04d",
                null
        );
    }
}
