
Seems like there is a bug with with the following scenario:
- enter fullscreen using the new `WindowInsetsController`
- ensure fullscreen restore after various other interactions that steals focus and might show system bar using an `OnApplyWindowInsetsListener`

The bug being a race between these methods with the insetsListener receiving the old insets, from before actually hiding the system bars.

```
    window.getWindowInsetsController().apply {
        hide(WindowInsetsCompat.Type.systemBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
```

```
    window.decorView.setOnApplyWindowInsetsListener { _, insets ->
        if (insets.isVisible(statusBars())) {
            setAsImmersive() // (the code from just above)
        }
        insets
    }
```
