## AR-Framework
Augmented Reality framework to visualize scientific data with longitude and latitude

### Dependency

In order to use our framework in your project, please add the following dependency to your `build.gradle` files:

In Project `build.gradle` file:
```javascript
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

In Module `build.gradle` file:
```javascript
dependencies {
    compile 'com.github.jplone:AR-Framework:v0.1'
}
```

### Quickstart

You can take a look at the accompanying ARDemo app to see how to use the framework with your project.

Alternatively, we have handled a lot of the niggles with permission and OpenGL context handling in our framework, so you can just simply add our ARFragment to your activity to get started:

Here's a sample code on how to create a new instance of the Fragment and add it to your Activity:
```java
  // setting up fragment
  FragmentTransaction ft = getFragmentManager().beginTransaction();

  ARFragment arFragment = ARFragment.newGPSInstance();
  ft.add(R.id.ar_view_container, arFragment);

  ft.commit();
```

You will need to have a FrameLayout or LinearLayout inside your activity's layout configuration (XML file) in order to "contain" the Fragment. For example, you should be able to put this in your `layout.xml` file:

```
  <FrameLayout
    android:id="@+id/ar_view_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

We have built a powerful render job pipeline and Landmark class that you can use to get started on displaying information:

```java
  // get the default icon we have included in the framework
  int ara_icon = edu.calstatela.jplone.arframework.R.drawable.ara_icon;

  // set up ARLandmark for New York city
  // parameters are: name, type, latitude, longitude, elevation
  ARLandmark arLandmarkNY = new ARLandmark("New York", "City", 40.730610f, -73.935242f, 100.0f);
  
  // add the landmark to the queue to be displayed
  arFragment.addJob(ARGLRenderJob.makeBillboard(5, ara_icon, arLandmarkNY));
```

### Documentation

Please check back later as we continue to build on the framework and improve on our documentation

[![](https://jitpack.io/v/jplone/AR-Framework.svg)](https://jitpack.io/#jplone/AR-Framework)
