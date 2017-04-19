+++
layout      = "single"
title       = "Shortcode Test"
slug        = "shortcode"
+++

# Shortcode test

Render a [shortcode](https://gohugo.io/extras/shortcodes/) that
comes from an environment variable:

```
MY_ENV="{{< MY_ENV >}}"             // <-- comes from layouts/shortcodes/MY_ENV.html
MY_OTHER_ENV="{{< myOtherEnv >}}"   // <-- comes from layouts/shortcodes/myOtherEnv.html
```
