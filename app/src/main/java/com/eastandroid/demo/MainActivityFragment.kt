/*
 * Copyright 2018 copyright eastar Jeong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.eastandroid.demo

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {

    private var dispose: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager.adapter = DAdapter()
        indicator.setupWithViewPager(pager)
        indicator_count.setText("${(pager.adapter as DAdapter).count}")
        dispose = RxTextView.textChanges(indicator_count)
            .map { it.toString().toIntOrNull() ?: 0 }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { indicator.setCount(it) }

        indicator.setCount(3)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dispose?.takeUnless { it.isDisposed }?.dispose()
    }

    class DAdapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var v = TextView(container.context)
            v.gravity = Gravity.CENTER
            v.text = "$position"
            v.setBackgroundColor(
                Color.HSVToColor(
                    0x55,
                    arrayOf(360F * (position.toFloat() / count.toFloat()), 1F, 1F).toFloatArray()
                )
            )
            container.addView(v)
            return v
        }

        override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
            container.removeView(o as View?)
        }

        override fun isViewFromObject(v: View, o: Any): Boolean = v == o
        override fun getCount(): Int = 10
    }
}
