##Android NumberProgressBar

-----

This repository is a fork of original [daimajia/NumberProgressBar](https://github.com/daimajia/NumberProgressBar)

---

###Demo

![NumberProgressBar](http://ww3.sinaimg.cn/mw690/610dc034jw1efyrd8n7i7g20cz02mq5f.gif)

###Usage
----

#### Gradle

Project build.gradle
```groovy
allprojects {
	 repositories {
		 maven { url "http://dl.bintray.com/dona278/maven/" }
	 }
}
```

Module build.gradle
```groovy
dependencies {
   compile "com.daimajia.numberprogressbar:library:1.3@aar"
}
```

Use it in your own code:

```xml
<com.daimajia.numberprogressbar.NumberProgressBar
	android:id="@+id/number_progress_bar"
	android:layout_width="wrap_content"
	android:layout_height="wrap_content" />
```	

I made some predesign style. You can use them via `style` property.

![Preset color](http://ww1.sinaimg.cn/mw690/610dc034jw1efyslmn5itj20f30k074r.jpg)

Use the preset style just like below:

```java
<com.daimajia.numberprogressbar.NumberProgressBar
	android:id="@+id/number_progress_bar"
	style="@style/NumberProgressBar_Default" />
```	

In the above picture, the style is : 

`NumberProgressBar_Default`
`NumberProgressBar_Passing_Green`
`NumberProgressBar_Relax_Blue`
`NumberProgressBar_Grace_Yellow`
`NumberProgressBar_Warning_Red`
`NumberProgressBar_Funny_Orange`
`NumberProgressBar_Beauty_Red`
`NumberProgressBar_Twinkle_Night`

You can get more beautiful color from [kular](https://kuler.adobe.com), and you can also contribute your color style to NumberProgressBar!  

###Attributes

There are several attributes you can set:

![](http://ww2.sinaimg.cn/mw690/610dc034jw1efyttukr1zj20eg04bmx9.jpg)

The **reached area** and **unreached area**:

* color
* height 

The **text area**:

* color
* text size
* visibility
* distance between **reached area** and **unreached area**

The **bar**:

* max progress
* current progress
* custom value
* suffix 
* prefix

for example, the default style:

```java
<com.daimajia.numberprogressbar.NumberProgressBar
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	
	custom:numberProgressBarUnreachedColor="#CCCCCC"
	custom:numberProgressBarReachedColor="#3498DB"
	
	custom:numberProgressBarUnreachedBarHeight="0.75dp"
	custom:numberProgressBarReachedBarHeight="1.5dp"
	
	custom:numberProgressBarTextSize="10sp"
	custom:numberProgressBarTextColor="#3498DB"
	custom:numberProgressBarTextOffset="1dp"
	custom:numberProgressBarTextVisibility="visible"
	
	custom:numberProgressBarMax="100"
	custom:numberProgressBarCurrent="75"
	custom:numberProgressBarCustomValue="4" 

	custom:numberProgressBarPrefix="Is "
	custom:numberProgressBarSuffix="th step"/>
```