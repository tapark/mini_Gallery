# 작은 갤러리
***

### 권한 요청
~~~kotlin
// in AndroidManifest.xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/> // 추가

// in MainActivity.kt
// requestPermissions(권한 종류, 코드)
requestPermissions(
	arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)

// 사용자의 권한 허용 여부 확인
 ContextCompat.checkSelfPermission(
	this,
	android.Manifest.permission.READ_EXTERNAL_STORAGE)
// 반환(권한허용) : PackageManager.PERMISSION_GRANTED
// 반환(권한거부) : PackageManager.PERMISSION_DENIED

// 사용자 권한 거부 시 교육(설명) UI 호출 여부 확인
shouldShowRequestPermissionRationale()
// true를 반환 할때 교육용 UI 실행(팝업 : AlertDialog 등)

// 사용자가 권한에 응답하면  onRequestPermissionsResult() 을 호출
override fun onRequestPermissionsResult(
	requestCode: Int,
	permissions: Array<out String>,
	grantResults: IntArray
) {
	super.onRequestPermissionsResult(requestCode, permissions, grantResults)
	// requestCode 코드별
	when (requestCode) {
		1000 -> {
			if (grantResults.isNotEmpty() &&
			grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// todo 권한이 부여됨
			}
			else {
				// todo 권한이 거부됨
			}
		}
	}
~~~

### 안드로이드 앨범 호출
~~~kotlin
val intent = Intent(Intent.ACTION_GET_CONTENT)
intent.type = "image/*"
startActivityForResult(intent, 2000)

// startActivityForResult 가 실행되면 onActivityResult()를 호출
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
	super.onActivityResult(requestCode, resultCode, data)

	if (resultCode != Activity.RESULT_OK) {
		return
	}
	when (requestCode) {
		2000 -> {
			// Uri 형태로 이미지를 저장
			val selectedImageUri: Uri? = data?.data
			if (selectedImageUri != null) {
				if (imageUriList.size == 6) {
					Toast.makeText(this, "사진은 6개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
					return
				}
				// Uri List(ResultActivity로 intent) 와 imageViewList(미리보기) 로 각각 저장
				imageUriList.add(selectedImageUri)
				imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri)
			}
			else { }
		}
		else -> { }
	}
}
~~~

### timer Thread : mainThread(UI) 가 아닌 별도의 timer Thread
 - 종료 전까지 일정주기로 반복 실행
~~~kotlin
// timer 변수를 전역으로 선언 : 안드로이 라이프사이클에서 timer가 종료 되는 시점을 정해주어야한다.
private var timer: Timer? = null
//
timer = timer(period = 5 * 1000) {
	runOnUiThread {
		val current = currentPosition
		var next = if (photoList.size <= currentPosition + 1) {
			0
		} else {
			currentPosition + 1
		}
		backgroundPhotoImageView.setImageURI(photoList[current])
		photoImageView.alpha = 0f // 투명도 = 투명함
		photoImageView.setImageURI(photoList[next])
		// 투명도를 0 ~ 1 로 증가 시키며 2개의 ImageView를 fadein ~ fadeout
		photoImageView.animate().alpha(1.0f).setDuration(1000).start()
		currentPosition = next
	}
}
~~~

### 안드로이드 생명주기 (Life_Cycle)
 - 앱이 멈추거나 종료되어도 Thread 가 Background 에서 동작할 수 있음
 - Life_Cycle 에 따른 Thread 관리가 필요함
~~~kotlin
override fun onStop() {
	super.onStop()
	timer?.cancel()
}
//onStop -> onStart
override fun onStart() {
	super.onStart()
	startTimer()
}
// onDestroy : 액티비티(앱)의 종료
override fun onDestroy() {
	super.onDestroy()
	timer?.cancel()
}
~~~