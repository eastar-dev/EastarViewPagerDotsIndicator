package com.eastandroid.demo

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager.adapter = DAdapter()
        indicator.setupWithViewPager(pager)
        indicator_count.setText("${(pager.adapter as DAdapter).count}")
        var d = RxTextView.textChanges(indicator_count)
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged<Any> { text -> Integer.parseInt(text.toString()) }
                .subscribe { text -> indicator.setCount(Integer.parseInt(text.toString())) }
    }

    class DAdapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var v = TextView(container.context)
            v.gravity = Gravity.CENTER
            v.text = "$position"
            v.setBackgroundColor(Color.HSVToColor(0x55, arrayOf(360F * (position.toFloat() / count.toFloat()), 1F, 1F).toFloatArray()))
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
