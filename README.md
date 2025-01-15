# mc-text-color-util

converting minecraft color strings

use however you like in your own code; see the license

## some examples
`"string".convertColors().minifyColors()`
```
&cHello &aworld! -> §cHello §aworld!

&#ff0000Hello &#00ff00world! -> §cHello §aworld!

{#ff0000}Hello {#0f0}world! -> §cHello §aworld!

{#black>}0123456   abcdef{#ffffff<} -> "§00§x§1§1§1§1§1§11§x§2§2§2§2§2§22§x§3§3§3§3§3§33§x§4§4§4§4§4§44§85§x§6§6§6§6§6§66   §7a§x§b§b§b§b§b§bb§x§c§c§c§c§c§cc§x§d§d§d§d§d§dd§x§e§e§e§e§e§ee§ff
```

```
val color1 = NamedColor.PURPLE()
// val color1 = NamedColor.PURPLE.invoke() // same as above
val color2 = NamedColor["hummingbird"] ?: CodedColor.BLACK()
val line1 = "&n        &r".gradient(color1, color2)
val line2 = "&n        &r".gradient(color2, color1)
fun wavyLine(repeat: Int = 4) = buildString {
    repeat(repeat) {
        append(line1)
        append(line2)
    }
    append(line1)
}.convertColors(minify = true).let { println(it) }
```

# usage

## 1. clone the repo
```
https://github.com/audizign/mc-text-color-util.git
```

## 2. publish to maven
`gradle publish` or `gradle publishToMavenLocal`

(whichever makes it work)

## 3. use in project or plugin

### 3a. shade
1. see `example.gradle.kts` and utilize in your project
2. gradle -> tasks -> shadowJar

### 3b. use the plugin

1-. download from releases (when i figure out how to) and add to your plugins folder

2a. add `depend: [TextColorUtil]` in your plugin.yml

2b. if you're using the `nokt` version, ensure a kotlin library plugin is also installed: https://hangar.papermc.io/4drian3d/MCKotlin

# goals
- [ ] stop refactoring
- [ ] add better comments
- [ ] color array to gradient string
- [x] improve replace algorithm performance
    - [x] minify gradients
    - [x] convert gradients
    - [x] minify hex
    - [x] convert hex