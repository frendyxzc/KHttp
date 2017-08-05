# KHttp
Wrapper of OKHttp3 with Kotlin DSL

## Usage

### 1. Add it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### 2. Add the dependency:

```
dependencies {
	compile 'com.github.frendyxzc:KHttp:0.0.2'
}
```

### 3. Add your request as follow:

#### 3.1 Get without callback

```
Request(this).getNewsList("0", "0")
```

```
fun getNewsList(uid: String, cid: String) {
	val url_get = RequestCommon.GET_NEWS_LIST + "&uid=" + uid + "&type_id=" + cid

	http {
		url = url_get
		method = "get"
		onSuccess {
			jsonStr: String -> Log.i("", jsonStr)
		}
		onFail {
			e -> Log.i("", e.message)
		}
	}
}
```

#### 3.2 Get with callback

```
Request(this).getNewsList("0", "0", object : Callback<ArrayList<News>> {
	override fun onSuccess(data: ArrayList<News>) {
		Log.i("", "GET = ${data[0].title}")
	}
	override fun onFail(error: String?) {
		Log.i("", "GET error = $error")
	}
})
```

```
fun getNewsList(uid: String, cid: String, callback: Callback<ArrayList<News>>) {
	val url_get = RequestCommon.GET_NEWS_LIST + "&uid=" + uid + "&type_id=" + cid

	http {
		url = url_get
		method = "get"
		onSuccess {
			jsonStr: String -> callback.onSuccess(gson.fromJson(jsonStr, RespGetNews::class.java).data)
		}
		onFail {
			e -> callback.onFail(e.message)
		}
	}
}
```

#### 3.3 Post with callback

```
Request(this).init(object : Callback<UserID>{
	override fun onSuccess(data: UserID) {
		Log.i("", "POST = ${data.user_id}")
	}
	override fun onFail(error: String?) {
		Log.i("", "POST error = $error")
	}
})
```

```
fun init(callback: Callback<UserID>) {
	val url_post = RequestCommon.BASE_URL

	val json = JSONObject()
	json.put("action", "init")
	...
	val postBody = RequestBody.create(JSON, json.toString())

	http {
		url = url_post
		method = "post"
		body = postBody
		onSuccess {
			jsonStr: String -> callback.onSuccess(gson.fromJson(jsonStr, RespInit::class.java).data)
		}
		onFail {
			e -> callback.onFail(e.message)
		}
	}
}
```
