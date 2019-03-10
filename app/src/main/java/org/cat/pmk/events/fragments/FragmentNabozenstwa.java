package org.cat.pmk.events.fragments;

import android.content.Context;

public class FragmentNabozenstwa extends AbstractFragmentByDistance {


    public FragmentNabozenstwa() {
        ctx = null;
    }

    public FragmentNabozenstwa(Context ctx) {
        super();
        this.ctx = ctx;
        this.spowiedzFilter = false;
    }


}
