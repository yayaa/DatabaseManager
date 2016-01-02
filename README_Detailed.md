# DatabaseManager

<a href="http://developer.android.com/index.html" target="_blank"><img src="https://img.shields.io/badge/platform-android-green.svg"/></a> <a href="https://android-arsenal.com/api?level=14" target="_blank"><img src="https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat"/></a> <a href="http://opensource.org/licenses/MIT" target="_blank"><img src="https://img.shields.io/badge/License-MIT-blue.svg?style=flat"/></a> <a href="http://search.maven.org/#search%7Cga%7C1%7CDatabaseManager" target="_blank"><img src="https://img.shields.io/maven-central/v/com.yayandroid/DatabaseManager.svg"/></a>

<a href="http://www.methodscount.com/?lib=com.yayandroid%3ADatabaseManager%3A1.0.0"><img src="https://img.shields.io/badge/Methods count-139-e91e63.svg"></img></a> <a href="http://www.methodscount.com/?lib=com.yayandroid%3ADatabaseManager%3A1.0.0"><img src="https://img.shields.io/badge/Size-18 KB-e91e63.svg"></img></a>
 
Database usage on mobile platforms may not be extensive but at some point, it is required. Android platform supports SQLite usage in your code base, but you have a lot to do! So i created this library to make database management easier. 

P.S.: I strongly recommend you to use [Realm][1] if it suits your requirements though.
 
# Usage

<ul>
<li>Create DatabaseManager object</li>
<li>Add databases that you would use into the manager</li>
<li>Create your query</li>
<li>And call the manager to run it</li>
</ul>
Simple as this!

**Have a single instance**

This library has no static singleton structure, because it keeps tracking your database objects so attaching that to static instance may cause leak. Instead, create a single instance in your application class and reach that out in your activities. You can see usage in [sample application][2].

**Database**

Create your database objects and add them to the manager, so that you can use them by passing single tag name. 
*Warning:* To prevent having "java.lang.IllegalStateException: SQLiteDatabase created and never closed" it is recommended to create database object with application context, so that it will live as long as application lives and you won't need to close it manually. 

```java 
Database localDB = new Database.Builder(Database.LOCAL, "localDbTag")
                .openWith(new DbOpenHelperLocal(getApplicationContext()))
                .build()
                
databaseManager.addDatabase(localDB);
```
DatabaseManager will not open your databases until you actually run a query on them, so adding databases when you first create your databaseManager and not thinking about them anymore, will be the right decision :)

Databases can have 2 types: `Database.LOCAL` and `Database.DISC`
<ul><li>
If you create database object with Database.LOCAL type, then you need to set `openWith` method to declare which SQLiteOpenHelper will be used while database open process. Because SQLiteOpenHelper quite straight forward, i didn't do anything about it, just pass databaseName, databaseVersion and handle create / update situations.
</li></ul>
<ul><li>
If you create database object with Database.DISC type, then you do not need to set openWith because it won't be used. Instead, you need to specify the `path` of required database in Disc.
</li></ul>

```java 
.path(String path)
```
This must be called while declaring database with `Database.DISC` type, not obligatory with `Database.LOCAL` type. But if you are going to use `getByMerged` or its derivatives, library will need your databases path to attach it to the query, so only then you need to set path with local type as well. 

And if you need to declare your path with `Database.LOCAL` type, you may find the path such as:
```java 
// if local database 
context.getDatabasePath(databaseName).getPath();

// if database in assets folder (via Android-Sqlite-Asset-Helper)
context.getApplicationInfo().dataDir + "/databases/" + databaseName; 
```
By the way, if your database is not saved in DISC nor as it is LOCAL, but in ASSETS folder then i would suggest you to use [Android-Sqlite-Asset-Helper-Library][3]

```java 
.openFlags(int flags)
```
Via this method, you can determine database to open as readOnly or readWrite. But be aware, library will accept only `SQLiteDatabase.OPEN_READWRITE` and `SQLiteDatabase.OPEN_READONLY` flags. As default, library will open databases with `SQLiteDatabase.OPEN_READWRITE` flag.

```html 
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
``` 
If you are going to read/write database from DISC, do not forget to add these permissions to the manifest file.

**Query**

Create a Query object and pass that to relevant method in DatabaseManager and it will take care of the rest. Query object does not have builder pattern, but still uses chaining so you can link all methods to each other to create more readable code pattern.

```java 
new Query(databaseTag)
      .set("SELECT * FROM someTable WHERE bla=? AND otherBla=?")
      .withArgs("blaEquivalent", "secondBlaEquivalent")
      .setListener(queryListener) // Defined below
```
Here, given `databaseTag` needs to be same within database object so that manager can find and run the query on it. And before you run any query make sure you add required database object to the manager, otherwise it will crash.

Query object has 2 main method: `set` and `insert`.
`insert` method is actually creates set methods inside, nothing special but it is there just because INSERT query is mostly same. Use that if only matches your requirements, if not, if you need to create a complex insert query then use `set` method which accepts every query statements.

You do not have to use `.withArgs` method, but be aware if you are going to type your parameters in queryText as well then you have to use inside of "'" marks. Otherwise, just put "?" in place of the variables and call `.withArgs(String... values)` method in same order and same count with your "?" marks.

**QueryListener**

DatabaseManager will return to QueryListener if you call one of the async methods on it, if not there is no need to set any listener.

```java 
private QueryListener<CustomObject> queryListener = new QueryListener<>() {
    
    public void onComplete(Query query) { }
    
    public void onListReceived(ArrayList<CustomObject> result) { }
    
    public void onSingleItemReceived(CustomObject result) {
          // This will be called if only there is a single object to return, 
          // if you create QueryListener with constructor has flag and pass it as true, 
          // then this method will not be called at all 
          // instead it will call onListReceived again.
    }
    
    public void noDataFound() { }

};
```
QueryListener has 2 different constructor: one is default no args and the other with a boolean which indicates that the listener needs to return objects as in list, even if there is single object to return.

QueryListener injects data to required objects. So while you define your custom object, you need to have variables with same name in table's column or you need to have `@ColumnName("Column")` declared above your variables. So that library can deserialize it. Important: CustomOBject HAS to default no args contructor! Otherwise deserialization will not work.

```java 
public class CustomObject {

    @ColumnName("tableColumn")
    private String myValue;

    private String sameWithTableColumn;

}
```

**DatabaseManager**

Finally you can call variaty of methods in DatabaseManager, just pick up which one mostly suits your statement that's all.

```java 
    public void execute(Query query);

    public void executeSync(Query query);
    
    public void select(Query query);
    
    public <T> ArrayList<T> selectSync(Class<T> clazz, Query query);
    
    public void selectByMerged(Query query, String databaseTagToMerge);
    
    public <T> ArrayList<T> selectByMergedSync(Class<T> clazz, Query query, String databaseTagToMerge);
    
    public void deleteAndInsert(Query deleteQuery, Query insertQuery);
    
    public void insert(Query query);
    
    public long insertSync(Query query);
    
    public void update(Query query);
    
    public int updateSync(Query query);
    
    public void delete(Query query);
    
    public int deleteSync(Query query);
```

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
[2]: https://github.com/yayaa/DatabaseManager/blob/master/Sample/app/src/main/java/com/yayandroid/databasemanager/sample/SampleApplication.java
[3]: https://github.com/jgilfelt/android-sqlite-asset-helper
