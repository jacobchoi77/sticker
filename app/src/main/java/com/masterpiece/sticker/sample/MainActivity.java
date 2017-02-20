/* Copyright (C) 2012 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.masterpiece.sticker.sample;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.masterpiece.sticker.R;
import com.masterpiece.sticker.Sticker;


public class MainActivity extends Activity implements Sticker.StickerCallback {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        FrameLayout stickerFrameLayout = (FrameLayout) findViewById(R.id.stickerFrameLayout);
        Sticker sticker = new Sticker(this);
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        sticker.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test));
        stickerFrameLayout.addView(sticker, params);

    }

    @Override
    public void closeSticker(Sticker sticker) {

    }

    @Override
    public void selectSticker(Sticker sticker) {

    }
}

