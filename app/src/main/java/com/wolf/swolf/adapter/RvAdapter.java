package com.wolf.swolf.adapter;

import com.wolf.wlibrary.recyclerview.SwAdapter;
import com.wolf.wlibrary.recyclerview.SwViewHolder;

import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-07 10:06
 */
public class RvAdapter extends SwAdapter<String>{

    public RvAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(SwViewHolder helper, String item) {

    }
}
