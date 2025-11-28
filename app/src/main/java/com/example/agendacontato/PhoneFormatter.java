package com.example.agendacontato;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Helper class for formatting Brazilian phone numbers
 */
public class PhoneFormatter implements TextWatcher {

    private boolean isFormatting;
    private boolean deletingHyphen;
    private int hyphenStart;
    private boolean deletingBackward;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (isFormatting) {
            return;
        }

        // Check if we're deleting
        if (count > 0) {
            deletingBackward = true;
            deletingHyphen = false;
            if (s.length() > start) {
                char c = s.charAt(start);
                if (c == '-' || c == ' ' || c == '(' || c == ')') {
                    deletingHyphen = true;
                    hyphenStart = start;
                }
            }
        } else {
            deletingBackward = false;
            deletingHyphen = false;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Not used
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isFormatting) {
            return;
        }

        isFormatting = true;

        // Remove all formatting characters
        String phone = s.toString().replaceAll("[^\\d]", "");

        // Build formatted string
        StringBuilder formatted = new StringBuilder();

        if (phone.length() > 0) {
            if (phone.length() <= 2) {
                formatted.append("(").append(phone);
            } else if (phone.length() <= 6) {
                formatted.append("(").append(phone.substring(0, 2)).append(") ");
                formatted.append(phone.substring(2));
            } else if (phone.length() <= 10) {
                formatted.append("(").append(phone.substring(0, 2)).append(") ");
                formatted.append(phone.substring(2, 6)).append("-");
                formatted.append(phone.substring(6));
            } else {
                // Mobile with 9 digits
                formatted.append("(").append(phone.substring(0, 2)).append(") ");
                formatted.append(phone.substring(2, 7)).append("-");
                formatted.append(phone.substring(7, Math.min(11, phone.length())));
            }
        }

        s.replace(0, s.length(), formatted.toString());
        isFormatting = false;
    }

    /**
     * Format a phone string directly (static method)
     * @param phone Raw phone number
     * @return Formatted phone number
     */
    public static String format(String phone) {
        if (phone == null) {
            return "";
        }

        String digits = phone.replaceAll("[^\\d]", "");

        if (digits.length() == 0) {
            return "";
        } else if (digits.length() <= 10) {
            // Landline: (XX) XXXX-XXXX
            if (digits.length() <= 2) {
                return "(" + digits;
            } else if (digits.length() <= 6) {
                return "(" + digits.substring(0, 2) + ") " + digits.substring(2);
            } else {
                return "(" + digits.substring(0, 2) + ") " +
                       digits.substring(2, 6) + "-" + digits.substring(6);
            }
        } else {
            // Mobile: (XX) XXXXX-XXXX
            return "(" + digits.substring(0, 2) + ") " +
                   digits.substring(2, 7) + "-" + digits.substring(7, Math.min(11, digits.length()));
        }
    }
}
