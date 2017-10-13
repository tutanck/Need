package com.aj.need.tools.components.services;

import com.aj.need.R;

/**
 * Created by joan on 23/09/2017.
 */

public class FormFieldKindTranslator {
    public static int tr(int kind){
        switch (kind) {
            case 0:
               return R.layout.fragment_form_field_multiline;
            case 1:
                return  R.layout.fragment_form_field_oneliner;
            default:
                return R.layout.fragment_form_field_multiline;
        }
    }
}
