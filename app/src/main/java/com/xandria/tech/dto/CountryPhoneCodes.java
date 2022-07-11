package com.xandria.tech.dto;

import androidx.annotation.NonNull;

public enum CountryPhoneCodes {
    Afghanistan(93),
    Albania(355),
    Algeria(213),
    American_Samoa(1-684),
    Uzbekistan(998);
    CountryPhoneCodes(int i) {
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
