## AR-Framework
Augmented Reality framework to visualize scientific data with longitude and latitude

### Dependency

In order to use our framework in your project, please add the following dependency to your `gradle.build` file:

```javascript
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

```javascript
dependencies {
    compile 'com.github.jplone:AR-Framework:v0.1'
}
```

### Quickstart

You can take a look at the accompanying ARDemo app to see how to use the framework with your project.

Alternatively, we have handled a lot of the niggles with permission and OpenGL context handling in our framework, so you can just simply add our ARFragment to your activity to get started:

```java
  // setting up fragment
  FragmentTransaction ft = getFragmentManager().beginTransaction();

  ARFragment arFragment = ARFragment.newGPSInstance();
  ft.add(R.id.ar_view_container, arFragment);

  ft.commit();
```

We have built a powerful render job pipeline and Landmark class that you can use to get started on displaying information:

```java
  // get the default icon we have included in the framework
  int ara_icon = edu.calstatela.jplone.arframework.R.drawable.ara_icon;

  // set up ARLandmark for New York city
  // parameters are: name, type, latitude, longitude, elevation
  ARLandmark arLandmarkNY = new ARLandmark("New York", "City", 40.730610, -73.935242f, 33.0f);
  
  // add the landmark to the queue to be displayed
  arFragment.addJob(ARGLRenderJob.makeBillboard(5, ara_icon, arLandmarkNY));
```

### Documentation

Please check back later as we continue to build on the framework and improve on our documentation

[![](https://jitpack.io/v/jplone/AR-Framework.svg)](https://jitpack.io/#jplone/AR-Framework)
