[![Release](https://jitpack.io/v/djrain/EastarViewPagerDotsIndicator.svg)](https://jitpack.io/#djrain/EastarViewPagerDotsIndicator)

# What is ViewPager dots Indicator(EastarViewPagerDotsIndicator)?

ViewPager에서 현재 위치가 어디 인지를 알려 주는 역할을 하는 UI입니다.<br/>
이 라이브러리는 Indicator를 표시 하기 위한 심플한 라이브러리 입니다.<br/>


<br/><br/>
# Demo
![Screenshot](https://github.com/djrain/EastarViewPagerDotsIndicator/blob/readme/demo.gif?raw=true)    
           




<br/><br/>
# Gradle

### Gradle with jitpack

#### Add it in your root build.gradle at the end of repositories:
```javascript

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

```
#### Add the dependency
```javascript

	dependencies {
	        implementation 'com.github.djrain:EastarViewPagerDotsIndicator:1.0.3'
	}
  
	

```



If you think this library is useful, please press star button at upside.



<br/><br/>
# Code

MainActivityFragment.kt
```javascript
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
```
1. indicator.setupWithViewPager(pager) 함수를 사용하여 pager 겟수만큼 indicator를 표시합니다(기본)
2. adapter must not null
3. DotIndicator.setCount 함수를 이용하여 강제로 indicator수를 조정 할수 있습니다.
4. setCurrentPosition 함수를 이용하여 현재 위치를 지정할수 있습니다.
5. CurrentPosition은 (CurrentPosition % Count) 로 작동합니다. 이것은 무한 스크롤 페이저를 만들때 유용합니다.


#### Add it in your your_layout.xml:
```javascript
    <android.view.DotIndicator
        android:id="@+id/indicator"
        android:layout_width="0dp"
        android:layout_height="10dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="16dp"
        app:dot="@drawable/dots"
        app:gravity="right|top"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:background="#5f00"
        tools:ignore="MissingPrefix" />
```
#### Add it in your dots.xml:
```javascript
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:drawable="@drawable/dots_s" android:state_selected="true" />
    <item android:drawable="@drawable/dots_n" />
</selector>
```

#### Add it in your YourActiviy.java:
```javascript
indicator.setupWithViewPager(pager)
```
                


<br/><br/>
# Customize
You can customize something ...<br />


        
* `app:dot="@drawable/dots" or setDrawable(@DrawableRes int dot) or setDrawable(Drawable dot)`
* `setupWithViewPager(ViewPager pager)` (note : ViewPager count set when count <= 0)
* `setCurrentPosition(int position)`
* `setGravity(int gravity)` (ex : Gravity.LEFT)
* `setDotSpacing(int dotSpacing)` (px)
* `setCount(int count)`






## Thanks 
<br/><br/>


## License 
```code
Copyright 2016 Eastar Jeong

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
