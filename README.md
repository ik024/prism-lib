# Prism Library - For all your network related operation

## Usage
In order to use this library in your project follow the following steps

First import the prism-lib module in your project and then add the following in your
 **build.gradle** file

```
...
dependencies {
    compile project(':prism-lib')
}
...
```

## Features
Currently this library supporst API that return Bitmap and String but can be extended as per requirement

##Fetching Bitmap
There are two ways to fetch bitmap using Prism

```
Prism.getInstance()
     .loadBitmapFrom(url, requestTag)
     .into(imagView)
     .execute();
```
OR
```
Prism.getInstance()
     .loadBitmapFrom(url, requestTag)
     .new BitmapResponseListener(){
           @overrid
           public void onResponse(Bitmap bitmap){//play with your bitmap}
           
           @override
           public void onError(String errorMsg){}
      }.execute();
```

##Fetching String
```
Prism.getInstance()
     .loadStringFrom(url, requestTag)
     .new StringResponseListener(){
           @overrid
           public void onResponse(String response){}
           
           @override
           public void onError(String errorMsg){}
      }.execute();
```
##Example
You can look at the source code of CacheLoading project which uses Prism Lib for its network operations
https://github.com/ik024/prism-lib/tree/master/app

