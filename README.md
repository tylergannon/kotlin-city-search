# City Search

Data and functions for a searchable list of 42,905 cities worldwide, with Lat/Lng and TimeZone.

```kotlin
val search: SearchProvider = DefaultSearchProvider()
val results: CitySearchResult = search("Santa Rosa, Ca").first()
println(results.name)

//  > Santa Rosa
```

## Installation

