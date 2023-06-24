# Blogs aggregator for [dailyjavablogs.github.io](https://dailyjavablogs.github.io)

## Used technologies

* [spring-boot](https://spring.io/projects/spring-boot) as a rss feed crawler
* [hugo](https://gohugo.io/) as a template engine
* [hugo PaperMod theme](https://adityatelange.github.io/hugo-PaperMod/) as a theme

## Adding a new blog

* Edit [application.yaml](./src/main/resources/application.yaml) and add entry to the `djb.sources`
* Download an image for given blog and put it to [images folder](./hugo/assets/images) or use
  the [java.png](./hugo/assets/images/java.png)