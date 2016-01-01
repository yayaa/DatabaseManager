# DatabaseManager

<a href="http://developer.android.com/index.html" target="_blank"><img src="https://img.shields.io/badge/platform-android-green.svg"/></a> <a href="https://android-arsenal.com/api?level=14" target="_blank"><img src="https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat"/></a> <a href="http://opensource.org/licenses/MIT" target="_blank"><img src="https://img.shields.io/badge/License-MIT-blue.svg?style=flat"/></a> <a href="http://search.maven.org/#search%7Cga%7C1%7CDatabaseManager" target="_blank"><img src="https://img.shields.io/maven-central/v/com.yayandroid/DatabaseManager.svg"/></a>

<a href="http://www.methodscount.com/?lib=com.yayandroid%3ADatabaseManager%3A1.0.0"><img src="https://img.shields.io/badge/Methods count-139-e91e63.svg"></img></a> <a href="http://www.methodscount.com/?lib=com.yayandroid%3ADatabaseManager%3A1.0.0"><img src="https://img.shields.io/badge/Size-18 KB-e91e63.svg"></img></a>
 
Database usage on mobile platforms may not be extensive but at some point, it is required. Android platform supports SQLite usage in your code base, but you have a lot to do! So i created this library to make database management easier. 

P.S.: I strongly recommend you to use [Realm][1] if it suits your requirements though.
 
# Usage

<ul><li>Create DatabaseManager object</li></ul>
```java 
DatabaseManager databaseManager = new DatabaseManager();
```

<ul><li>Add databases that you would use into the manager</li></ul>
```java 
Database localDB = new Database.Builder(Database.LOCAL, "localDbTag")
                .openWith(new DbOpenHelperLocal(getApplicationContext()))
                .build()
                
databaseManager.addDatabase(localDB);
```

<ul><li>Create your query</li></ul>
```java 
Query query = new Query(databaseTag)
      .set("SELECT * FROM someTable WHERE bla=? AND otherBla=?")
      .withArgs("blaEquivalent", "secondBlaEquivalent")
      .setListener(new QueryListener<CustomObject>() { ... }); // Has deserialization with annotations
```

<ul><li>And call the manager to run it</li></ul>

```java 
databaseManager.select(query);
```
Simple as this!

[Please see the blog for more information, and important points about the library][2]

## Download
Add library dependency to your `build.gradle` file:

```groovy
dependencies {    
     compile 'com.yayandroid:DatabaseManager:1.0.0'
}
```

## License
```
The MIT License (MIT)

Copyright (c) 2016 yayandroid

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```

[1]: https://github.com/realm/realm-java
[2]: http://yayandroid.com/2016/01/databasemanager/
