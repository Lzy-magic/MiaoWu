package com.apps.miaowu.common.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Web - date editor
 * @see org.springframework.beans.propertyeditors.CustomDateEditor
 */
public class DateEditor extends PropertyEditorSupport {
    /** Whether to convert empty strings to null */
    private final boolean emptyAsNull;
    /** Supported date formats */
    private final String[] datePatterns;
    /** Date editor */
    private final DateFormat dateFormater;

    public DateEditor() {
        this(true, new String[]{"yyyy", "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss"}, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public DateEditor(boolean emptyAsNull, String[] datePatterns, DateFormat dateFormater) {
        this.emptyAsNull = emptyAsNull;
        this.datePatterns = datePatterns;
        this.dateFormater = dateFormater;
    }

    @Override
    public String getAsText() {
        return getValue() != null ? dateFormater.format((Date) getValue()) : null;
    }

    @Override
    public void setAsText(String text) {
        if (text == null) {
            setValue(null);
        } else if (emptyAsNull && text.isEmpty()) {
            setValue(null);
        } else {
            try {
                setValue(DateUtils.parseDate(text.trim(), datePatterns));
            } catch (ParseException e) {
                setValue(null);
            }
        }
    }
}
